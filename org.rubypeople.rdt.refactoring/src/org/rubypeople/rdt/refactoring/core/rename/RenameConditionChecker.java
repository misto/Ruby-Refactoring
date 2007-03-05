package org.rubypeople.rdt.refactoring.core.rename;

import org.rubypeople.rdt.refactoring.core.IRefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.renameclass.RenameClassConditionChecker;
import org.rubypeople.rdt.refactoring.core.renameclass.RenameClassConfig;
import org.rubypeople.rdt.refactoring.core.renamefield.RenameFieldConditionChecker;
import org.rubypeople.rdt.refactoring.core.renamefield.RenameFieldConfig;
import org.rubypeople.rdt.refactoring.core.renamelocalvariable.RenameLocalConditionChecker;
import org.rubypeople.rdt.refactoring.core.renamelocalvariable.RenameLocalConfig;
import org.rubypeople.rdt.refactoring.core.renamemethod.RenameMethodConditionChecker;
import org.rubypeople.rdt.refactoring.core.renamemethod.RenameMethodConfig;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentProvider;

public class RenameConditionChecker extends RefactoringConditionChecker {

	private enum RenameType {
		INVALID, LOCAL, FIELD, METHOD, CLASS
	};

	private RenameType selectedType;

	private RenameLocalConditionChecker localConditionChecker;

	private RefactoringConditionChecker fieldConditionChecker;

	private RefactoringConditionChecker methodConditionChecker;

	private RefactoringConditionChecker classConditionChecker;

	public RenameConditionChecker(RenameConfig config) {
		super(config.getDocumentProvider(), config);
	}

	@Override
	protected void checkInitialConditions() {
		if (selectedType == RenameType.INVALID) {
			addErrorMessage();
		}
	}

	private void addErrorMessage() {
		addErrorIfNotDefaultError(localConditionChecker, RenameLocalConditionChecker.DEFAULT_ERROR);
		addErrorIfNotDefaultError(fieldConditionChecker, RenameFieldConditionChecker.DEFAULT_ERROR);
		addErrorIfNotDefaultError(methodConditionChecker, RenameMethodConditionChecker.DEFAULT_ERROR);
		addErrorIfNotDefaultError(classConditionChecker, RenameClassConditionChecker.DEFAULT_ERROR);
		if (!hasErrors()) {

			addError(Messages.RenameConditionChecker_NothingSelected);

		}
	}

	private void addErrorIfNotDefaultError(RefactoringConditionChecker checker, String defaultError) {
		String firstError = checker.getInitialMessages().get(IRefactoringConditionChecker.ERRORS).toArray(new String[0])[0];
		if (!firstError.equals(defaultError)) {
			addError(firstError);
		}
	}

	@Override
	protected void init(Object configObj) {
		RenameConfig config = (RenameConfig) configObj;
		int offset = config.getOffset();
		DocumentProvider docProvider = config.getDocumentProvider();
		localConditionChecker = new RenameLocalConditionChecker(new RenameLocalConfig(docProvider, offset));
		fieldConditionChecker = new RenameFieldConditionChecker(new RenameFieldConfig(docProvider, offset));
		methodConditionChecker = new RenameMethodConditionChecker(new RenameMethodConfig(docProvider, offset));
		classConditionChecker = new RenameClassConditionChecker(new RenameClassConfig(docProvider, offset));
		if (localConditionChecker.shouldPerform()) {
			selectedType = RenameType.LOCAL;
		} else if (fieldConditionChecker.shouldPerform()) {
			selectedType = RenameType.FIELD;
		} else if (methodConditionChecker.shouldPerform()) {
			selectedType = RenameType.METHOD;
		} else if (classConditionChecker.shouldPerform()) {
			selectedType = RenameType.CLASS;
		} else {
			selectedType = RenameType.INVALID;
		}
	}

	public boolean shouldRenameLocal() {
		return selectedType == RenameType.LOCAL;
	}

	public boolean shouldRenameField() {
		return selectedType == RenameType.FIELD;
	}

	public boolean shouldRenameMethod() {
		return selectedType == RenameType.METHOD;
	}

	public boolean shouldRenameClass() {
		return selectedType == RenameType.CLASS;
	}
}
