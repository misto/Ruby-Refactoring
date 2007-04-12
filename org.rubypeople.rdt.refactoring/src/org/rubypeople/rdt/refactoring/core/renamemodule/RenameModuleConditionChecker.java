package org.rubypeople.rdt.refactoring.core.renamemodule;

import org.jruby.ast.ModuleNode;
import org.rubypeople.rdt.refactoring.core.IRefactoringConfig;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
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
		
		ModuleNode selectedModule = (ModuleNode) SelectionNodeProvider.getSelectedNodeOfType(config.getDocumentProvider().getActiveFileRootNode(), config.getCarretPosition(), ModuleNode.class);
		config.setSelectedModule(selectedModule);
		if(config.getSelectedModule()== null || !NodeUtil.positionIsInNode(config.getCarretPosition(), selectedModule.getCPath())) {
			return;
		}
		config.setNewName(selectedModule.getCPath().getName());
	}

}
