package org.rubypeople.rdt.refactoring.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.refactoring.RefactoringObjectFactory;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;
import org.rubypeople.rdt.refactoring.ui.ClassesSelectionDialog;
import org.rubypeople.rdt.refactoring.ui.CodeViewer;
import org.rubypeople.rdt.refactoring.ui.IncludedClassesSelectionDialog;
import org.rubypeople.rdt.refactoring.ui.RdtCodeViewer;
import org.rubypeople.rdt.refactoring.util.RefactoringCodeFormatter;
import org.rubypeople.rdt.refactoring.util.RdtCodeFormatter;

public class RdtRefactoringObjectFactory implements RefactoringObjectFactory {

	public ITextSelectionProvider createTextSelectionProvider(IAction action) {
		try {
			return new TextSelectionProvider(action);
		} catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public IFile getActiveFile() {
		RubyEditor editor = (RubyEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		return editor != null ? ((IFileEditorInput) editor.getEditorInput()).getFile() : null;
	}

	public Change createDynamicValidationChange(String key, String value) {
		Change renameResourceChange = new RenameResourceChange(null, ResourcesPlugin.getWorkspace().getRoot().findMember(key), value, "comment");
		DynamicValidationStateChange dynamicValidationStateChange = new DynamicValidationStateChange(renameResourceChange);
		return dynamicValidationStateChange;
	}

	public RefactoringCodeFormatter getFormatter() {
		return new RdtCodeFormatter();
	}

	public CodeViewer getCodeViewer(Composite parent) {
		return RdtCodeViewer.create(parent);
	}

	public ClassesSelectionDialog getClassesSelectionDialog(IDocumentProvider doc, String title, String methodname) {
		return new IncludedClassesSelectionDialog(doc, title, methodname);
	}
}
