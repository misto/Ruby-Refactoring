package ch.hsr.ch.refactoring.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.jface.action.IAction;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.refactoring.RefactoringObjectFactory;
import org.rubypeople.rdt.refactoring.core.ITextSelectionProvider;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;
import org.rubypeople.rdt.refactoring.ui.ClassesSelectionDialog;
import org.rubypeople.rdt.refactoring.ui.CodeViewer;
import org.rubypeople.rdt.refactoring.util.RefactoringCodeFormatter;

import ch.hsr.ch.refactoring.ui.DltkCodeViewer;
import ch.hsr.ch.refactoring.ui.IncludedClassesSelectionDialog;
import ch.hsr.ch.refactoring.util.DltkCodeFormatter;

public class RdtRefactoringObjectFactory implements RefactoringObjectFactory {

	public ITextSelectionProvider createTextSelectionProvider(IAction action) {
		return new TextSelectionProvider(action);
	}

	public IFile getActiveFile() {
		ScriptEditor editor = (ScriptEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		return editor != null ? ((IFileEditorInput) editor.getEditorInput()).getFile() : null;
	}

	public Change createDynamicValidationChange(String key, String value) {
		Change renameResourceChange = new RenameResourceChange(null, ResourcesPlugin.getWorkspace().getRoot().findMember(key), value, "comment");
		DynamicValidationStateChange dynamicValidationStateChange = new DynamicValidationStateChange(renameResourceChange);
		return dynamicValidationStateChange;
	}

	public RefactoringCodeFormatter getFormatter() {
		return new DltkCodeFormatter();
	}

	public CodeViewer getCodeViewer(Composite parent) {
		return new DltkCodeViewer(parent);
	}

	public ClassesSelectionDialog getClassesSelectionDialog(IDocumentProvider doc, String title, String methodname) {
		return new IncludedClassesSelectionDialog(doc, title, methodname);
	}
}
