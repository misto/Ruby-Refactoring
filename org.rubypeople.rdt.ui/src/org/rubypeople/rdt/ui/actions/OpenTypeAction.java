package org.rubypeople.rdt.ui.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.actions.ActionMessages;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.ui.wizards.RubyTypeSelectionDialog;

public class OpenTypeAction implements IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void dispose() {
		this.window = null;
	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		
		RubyTypeSelectionDialog dialog = new RubyTypeSelectionDialog(window.getShell());
		dialog.setMessage(ActionMessages.OpenTypeAction_message);
		
		if (dialog.open() == Window.OK) {
			IRubyElement selected = (IRubyElement) dialog.getFirstResult();
			try {
				IEditorPart editor = EditorUtility.openInEditor(selected, true);
				EditorUtility.revealInEditor(editor, selected);
			} catch (PartInitException e) {
				showError(e);
			} catch (RubyModelException e) {
				showError(e);
			}
        }
	}
	
	private void showError(CoreException e) {
		ErrorDialog.openError(window.getShell(),
				ActionMessages.OpenTypeAction_error_title,
				ActionMessages.OpenTypeAction_error_messageProblems,  
				e.getStatus());
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
