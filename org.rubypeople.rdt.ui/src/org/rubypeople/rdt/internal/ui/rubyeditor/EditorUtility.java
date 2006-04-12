package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class EditorUtility {
	
	/**
	 * Opens a Ruby editor for an element (IJavaElement, IFile, IStorage...)
	 * @return the IEditorPart or null if wrong element type or opening failed
	 */
	public static IEditorPart openInEditor(Object inputElement, boolean activate) throws RubyModelException, PartInitException {

		if (inputElement instanceof IFile)
			return openInEditor((IFile) inputElement, activate);

		IEditorInput input= getEditorInput(inputElement);
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fileInput= (IFileEditorInput) input;
			return openInEditor(fileInput.getFile(), activate);
		}

		if (input != null)
			return openInEditor(input, getEditorID(input, inputElement), activate);

		return null;
	}
	
	private static IEditorPart openInEditor(IFile file, boolean activate) throws PartInitException {
		if (file != null) {
			IWorkbenchPage p= RubyPlugin.getActivePage();
			if (p != null) {
				IEditorPart editorPart= IDE.openEditor(p, file, activate);
				initializeHighlightRange(editorPart);
				return editorPart;
			}
		}
		return null;
	}
	
	private static IEditorPart openInEditor(IEditorInput input, String editorID, boolean activate) throws PartInitException {
		if (input != null) {
			IWorkbenchPage p= RubyPlugin.getActivePage();
			if (p != null) {
				IEditorPart editorPart= p.openEditor(input, editorID, activate);
				initializeHighlightRange(editorPart);
				return editorPart;
			}
		}
		return null;
	}
	
	private static void initializeHighlightRange(IEditorPart editorPart) {
		if (editorPart instanceof ITextEditor) {
			IAction toggleAction= editorPart.getEditorSite().getActionBars().getGlobalActionHandler(ITextEditorActionDefinitionIds.TOGGLE_SHOW_SELECTED_ELEMENT_ONLY);
			boolean enable= toggleAction != null; 
			if (enable && editorPart instanceof RubyEditor)
				enable= RubyPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.EDITOR_SHOW_SEGMENTS);
			else
				enable= enable && toggleAction.isEnabled() && toggleAction.isChecked();
			if (enable) {
				if (toggleAction instanceof TextEditorAction) {
					// Reset the action
					((TextEditorAction)toggleAction).setEditor(null);
					// Restore the action
					((TextEditorAction)toggleAction).setEditor((ITextEditor)editorPart);
				} else {
					// Un-check
					toggleAction.run();
					// Check
					toggleAction.run();
				}
			}
		}
	}
	
	private static String getEditorID(IEditorInput input, Object inputObject) {
		IEditorDescriptor editorDescriptor;
		try {
			editorDescriptor= IDE.getEditorDescriptor(input.getName());
		} catch (PartInitException e) {
			return null;
		}

		if (editorDescriptor != null)
			return editorDescriptor.getId();

		return null;
	}
	
	public static IEditorInput getEditorInput(Object input) throws RubyModelException {
		if (input instanceof IRubyElement)
			return getEditorInput((IRubyElement) input);

		if (input instanceof IFile)
			return new FileEditorInput((IFile) input);

		return null;
	}
	
	private static IEditorInput getEditorInput(IRubyElement element) throws RubyModelException {
		while (element != null) {
			if (element instanceof IRubyScript) {
				IRubyScript unit= RubyModelUtil.toOriginal((IRubyScript) element);
					IResource resource= unit.getResource();
					if (resource instanceof IFile)
						return new FileEditorInput((IFile) resource);
			}

			element= element.getParent();
		}

		return null;
	}
	
	/**
	 * Selects a Java Element in an editor
	 */
	public static void revealInEditor(IEditorPart part, IRubyElement element) {
		if (element == null)
			return;

		if (part instanceof RubyEditor) {
			((RubyEditor) part).setSelection(element);
			return;
		}

		// Support for non-Ruby editor
		try {
			ISourceRange range= null;
			if (element instanceof IRubyScript)
				range= null;
//			else if (element instanceof ILocalVariable)
//				range= ((ILocalVariable)element).getNameRange();
			else if (element instanceof IMember)
				range= ((IMember)element).getNameRange();
			else if (element instanceof ISourceReference)
				range= ((ISourceReference)element).getSourceRange();

			if (range != null)
				revealInEditor(part, range.getOffset(), range.getLength());
		} catch (RubyModelException e) {
			// don't reveal
		}
	}
	
	/**
	 * Selects and reveals the given offset and length in the given editor part.
	 */
	public static void revealInEditor(IEditorPart editor, final int offset, final int length) {
		if (editor instanceof ITextEditor) {
			((ITextEditor)editor).selectAndReveal(offset, length);
			return;
		}

		// Support for non-text editor - try IGotoMarker interface
		 if (editor instanceof IGotoMarker) {
			final IEditorInput input= editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				final IGotoMarker gotoMarkerTarget= (IGotoMarker)editor;
				WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
					protected void execute(IProgressMonitor monitor) throws CoreException {
						IMarker marker= null;
						try {
							marker= ((IFileEditorInput)input).getFile().createMarker(IMarker.TEXT);
							marker.setAttribute(IMarker.CHAR_START, offset);
							marker.setAttribute(IMarker.CHAR_END, offset + length);

							gotoMarkerTarget.gotoMarker(marker);

						} finally {
							if (marker != null)
								marker.delete();
						}
					}
				};

				try {
					op.run(null);
				} catch (InvocationTargetException ex) {
					// reveal failed
				} catch (InterruptedException e) {
					Assert.isTrue(false, "this operation can not be canceled"); //$NON-NLS-1$
				}
			}
			return;
		}

		/*
		 * Workaround: send out a text selection
		 * XXX: Needs to be improved, see https://bugs.eclipse.org/bugs/show_bug.cgi?id=32214
		 */
		if (editor != null && editor.getEditorSite().getSelectionProvider() != null) {
			IEditorSite site= editor.getEditorSite();
			if (site == null)
				return;

			ISelectionProvider provider= editor.getEditorSite().getSelectionProvider();
			if (provider == null)
				return;

			provider.setSelection(new TextSelection(offset, length));
		}
	}
	
	/**
	 * Returns the Ruby project for a given editor input or <code>null</code> if no corresponding
	 * Ruby project exists.
	 *
	 * @param input the editor input
	 * @return the corresponding Ruby project
	 *
	 * @since 0.9.0
	 */
	public static IRubyProject getRubyProject(IEditorInput input) {
		IRubyProject rProject= null;
		if (input instanceof IFileEditorInput) {
			IProject project= ((IFileEditorInput)input).getFile().getProject();
			if (project != null) {
				rProject= RubyCore.create(project);
				if (!rProject.exists())
					rProject= null;
			}
		}
		return rProject;
	}

}
