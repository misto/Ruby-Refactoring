package org.rubypeople.rdt.refactoring.core.renamemodule;

import java.util.ArrayList;
import java.util.Collection;

import org.jruby.ast.IScopingNode;
import org.rubypeople.rdt.refactoring.editprovider.FileMultiEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.IMultiFileEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.MultiFileEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.ScopingNodeRenameEditProvider;

public class RenameModuleEditProvider implements IMultiFileEditProvider {

	private final RenameModuleConfig config;

	public RenameModuleEditProvider(RenameModuleConfig config) {
		this.config = config;
	}
	
	private ScopingNodeRenameEditProvider getModuleEditProvider() {
		ArrayList<IScopingNode> modules = new ArrayList<IScopingNode>();
		modules.add(config.getSelectedModule().getWrappedNode());
		return new ScopingNodeRenameEditProvider(modules, config.getNewName());
	}

	public Collection<FileMultiEditProvider> getFileEditProviders() {
		MultiFileEditProvider fileEdits = new MultiFileEditProvider();
		
		fileEdits.addEditProviders(getModuleEditProvider().getEditProviders());
		
		return fileEdits.getFileEditProviders();
	}
}
