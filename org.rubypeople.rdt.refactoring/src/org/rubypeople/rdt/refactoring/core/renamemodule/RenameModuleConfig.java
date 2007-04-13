package org.rubypeople.rdt.refactoring.core.renamemodule;

import org.rubypeople.rdt.refactoring.core.IRefactoringConfig;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.ModuleNodeWrapper;
import org.rubypeople.rdt.refactoring.ui.INewNameReceiver;

public class RenameModuleConfig implements IRefactoringConfig, INewNameReceiver {

	private IDocumentProvider doc;
	private final int carretPosition;
	private ModuleNodeWrapper selectedModule;
	private String newName;

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

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getSelectedModuleName() {
		return selectedModule != null ? selectedModule.getName() : null;
	}

	public int getCarretPosition() {
		return carretPosition;
	}

	public ModuleNodeWrapper getSelectedModule() {
		return selectedModule;
	}

	public void setSelectedModule(ModuleNodeWrapper selectedModule) {
		this.selectedModule = selectedModule;
	}

	public String getNewName() {
		return newName;
	}
}
