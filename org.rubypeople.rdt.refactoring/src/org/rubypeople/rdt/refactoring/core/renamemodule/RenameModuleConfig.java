package org.rubypeople.rdt.refactoring.core.renamemodule;

import org.jruby.ast.ModuleNode;
import org.rubypeople.rdt.refactoring.core.IRefactoringConfig;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;
import org.rubypeople.rdt.refactoring.ui.INewNameReceiver;

public class RenameModuleConfig implements IRefactoringConfig, INewNameReceiver {

	private IDocumentProvider doc;
	private final int carretPosition;
	private ModuleNode selectedModule;

	public RenameModuleConfig(IDocumentProvider doc, int carretPosition) {
		this.doc = doc;
		this.carretPosition = carretPosition;
	}

	public IDocumentProvider getDocumentProvider() {
		return doc;
	}

	public void setDocumentProvider(IDocumentProvider doc) {
		this.doc = doc;
	}

	public void setNewName(String name) {
		// TODO Auto-generated method stub
		
	}

	public String getSelectedModuleName() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getCarretPosition() {
		return carretPosition;
	}

	public ModuleNode getSelectedModule() {
		return selectedModule;
	}

	public void setSelectedModule(ModuleNode selectedModule) {
		this.selectedModule = selectedModule;
	}
}
