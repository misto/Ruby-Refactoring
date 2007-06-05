package org.rubypeople.rdt.internal.ui.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.ICodeAssist;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyScriptEditorInput;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.ui.IWorkingCopyManager;

public class SelectionConverter {

    public static IRubyScript getInputAsRubyScript(RubyEditor editor) {
        Object editorInput = SelectionConverter.getInput(editor);
        if (editorInput instanceof IRubyScript)
            return (IRubyScript) editorInput;
        else
            return null;
    }
    
    public static IRubyElement getInput(RubyEditor editor) {
    	if (editor == null)
            return null;
        IEditorInput input= editor.getEditorInput();
        if (input instanceof IRubyScriptEditorInput) {
        	IRubyScriptEditorInput scriptEditor = (IRubyScriptEditorInput) input;
        	return scriptEditor.getRubyScript();
        }
        IWorkingCopyManager manager= RubyPlugin.getDefault().getWorkingCopyManager();               
        return manager.getWorkingCopy(input);           
    }

	public static boolean canOperateOn(RubyEditor editor) {
		if (editor == null)
			return false;
		return getInput(editor) != null;		
	}
	
	private static final IRubyElement[] EMPTY_RESULT= new IRubyElement[0];

	/**
	 * Converts the text selection provided by the given editor a Ruby element by
	 * asking the user if code reolve returned more than one result. If the selection 
	 * doesn't cover a Ruby element <code>null</code> is returned.
	 */
	public static IRubyElement codeResolve(RubyEditor editor, Shell shell, String title, String message) throws RubyModelException {
		IRubyElement[] elements= codeResolve(editor);
		if (elements == null || elements.length == 0)
			return null;
		IRubyElement candidate= elements[0];
		if (elements.length > 1) {
			candidate= OpenActionUtil.selectRubyElement(elements, shell, title, message);
		}
		return candidate;
	}
	
	public static IRubyElement[] codeResolve(RubyEditor editor) throws RubyModelException {
		return codeResolve(getInput(editor), (ITextSelection)editor.getSelectionProvider().getSelection());
	}
	
	public static IRubyElement[] codeResolve(IRubyElement input, ITextSelection selection) throws RubyModelException {
		return codeResolve(input, selection.getOffset(), selection.getLength());
	}
	
	public static IRubyElement[] codeResolve(IRubyElement input, int offset, int length) throws RubyModelException {
		if (input instanceof ICodeAssist) {
			if (input instanceof IRubyScript) {
				RubyModelUtil.reconcile((IRubyScript) input);
			}
			IRubyElement[] elements= ((ICodeAssist)input).codeSelect(offset, length);
			if (elements != null && elements.length > 0)
				return elements;
		}
		return EMPTY_RESULT;
	}

	/**
	 * Perform a code resolve in a separate thread.
	 * @param primaryOnly if <code>true</code> only primary working copies will be returned
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @since 1.0
	 */
	public static IRubyElement[] codeResolveForked(RubyEditor editor, boolean primaryOnly) throws InvocationTargetException, InterruptedException {
		return performForkedCodeResolve(getInput(editor, primaryOnly), (ITextSelection)editor.getSelectionProvider().getSelection());
	}
	
	/**
	 * @param primaryOnly if <code>true</code> only primary working copies will be returned
	 * @since 1.0
	 */
	private static IRubyElement getInput(RubyEditor editor, boolean primaryOnly) {
		if (editor == null)
			return null;
		return EditorUtility.getEditorInputRubyElement(editor, primaryOnly);
	}
	
	private static IRubyElement[] performForkedCodeResolve(final IRubyElement input, final ITextSelection selection) throws InvocationTargetException, InterruptedException {
		final class CodeResolveRunnable implements IRunnableWithProgress {
			IRubyElement[] result;
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					result= codeResolve(input, selection);
				} catch (RubyModelException e) {
					throw new InvocationTargetException(e);
				}
			}
		}
		CodeResolveRunnable runnable= new CodeResolveRunnable();
		PlatformUI.getWorkbench().getProgressService().busyCursorWhile(runnable);
		return runnable.result;
	}

}
