package org.rubypeople.rdt.refactoring.core.rename;

import org.eclipse.jface.wizard.IWizardPage;
import org.rubypeople.rdt.refactoring.core.IRefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.RubyRefactoring;
import org.rubypeople.rdt.refactoring.core.TextSelectionProvider;
import org.rubypeople.rdt.refactoring.core.renameclass.RenameClassRefactoring;
import org.rubypeople.rdt.refactoring.core.renamefield.RenameFieldRefactoring;
import org.rubypeople.rdt.refactoring.core.renamelocalvariable.RenameLocalVariableRefactoring;
import org.rubypeople.rdt.refactoring.core.renamemethod.RenameMethodRefactoring;

public class RenameRefactoring extends RubyRefactoring {

	public static final String NAME = "Rename";
	private RubyRefactoring delegateRenameRefactoring;

	public RenameRefactoring(TextSelectionProvider selectionProvider) {
		super(NAME);

		RenameConfig config = new RenameConfig(getDocumentProvider(), selectionProvider.getCarretPosition());
		RenameConditionChecker checker = new RenameConditionChecker(config);
		setRefactoringConditionChecker(checker);
		if(checker.shouldPerform()) {
			if(checker.shouldRenameLocal()) {
				delegateRenameRefactoring = new RenameLocalVariableRefactoring(selectionProvider);
			} else if (checker.shouldRenameField()) {
				delegateRenameRefactoring = new RenameFieldRefactoring(selectionProvider);
			} else if(checker.shouldRenameMethod()) {
				delegateRenameRefactoring = new RenameMethodRefactoring(selectionProvider);
			} else if(checker.shouldRenameClass()) {
				delegateRenameRefactoring = new RenameClassRefactoring(selectionProvider);
			}
			IRefactoringConditionChecker delegateConditionChecker = delegateRenameRefactoring.getConditionChecker();
			setRefactoringConditionChecker(delegateConditionChecker);
			if(delegateConditionChecker.shouldPerform()) {
				setName(delegateRenameRefactoring.getName());
				setEditProvider(delegateRenameRefactoring.getMultiFileEditProvider());
				setEditProvider(delegateRenameRefactoring.getEditProvider());
				for(IWizardPage aktPage : delegateRenameRefactoring.getPages()) {
					pages.add(aktPage);
				}
			}
		}
	}
}
