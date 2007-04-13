package org.rubypeople.rdt.refactoring.core.renamemodule;

import org.rubypeople.rdt.refactoring.core.IRefactoringConfig;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.ModuleNodeWrapper;
import org.rubypeople.rdt.refactoring.util.NodeUtil;

public class RenameModuleConditionChecker extends RefactoringConditionChecker {
	
	public static final String DEFAULT_ERROR = "Please select the name of a module definition.";

	private RenameModuleConfig config;

	public RenameModuleConditionChecker(IRefactoringConfig config) {
		super(config);
	}

	@Override
	protected void checkInitialConditions() {
		if (config.getSelectedModule() == null) {
			addError(DEFAULT_ERROR);
		}
	}

	@Override
	public void init(IRefactoringConfig configObj) {
		config = (RenameModuleConfig) configObj;
		
		ModuleNodeWrapper selectedModule = SelectionNodeProvider.getSelectedModuleNode(config.getDocumentProvider().getActiveFileRootNode(), config.getCarretPosition());
		config.setSelectedModule(selectedModule);
		if(config.getSelectedModule() == null || caretIsNotOnModuleName(selectedModule)) {
			return;
		}
		config.setNewName(selectedModule.getName());
	}

	private boolean caretIsNotOnModuleName(ModuleNodeWrapper selectedModule) {
		return !NodeUtil.positionIsInNode(config.getCarretPosition(), selectedModule.getWrappedNode().getCPath());
	}

}
