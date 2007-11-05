package org.rubypeople.rdt.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.swt.widgets.Composite;
import org.rubypeople.rdt.refactoring.core.ITextSelectionProvider;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;
import org.rubypeople.rdt.refactoring.ui.ClassesSelectionDialog;
import org.rubypeople.rdt.refactoring.ui.CodeViewer;
import org.rubypeople.rdt.refactoring.util.RefactoringCodeFormatter;

public interface RefactoringObjectFactory {
	
	ITextSelectionProvider createTextSelectionProvider(IAction action);
	
	IFile getActiveFile();
	
	Change createDynamicValidationChange(String key, String value);
	
	RefactoringCodeFormatter getFormatter();
	
	CodeViewer getCodeViewer(Composite parent);
	
	ClassesSelectionDialog getClassesSelectionDialog(IDocumentProvider doc, String title, String methodname);
}
