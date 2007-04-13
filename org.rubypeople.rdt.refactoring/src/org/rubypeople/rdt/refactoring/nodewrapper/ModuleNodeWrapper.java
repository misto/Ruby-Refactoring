package org.rubypeople.rdt.refactoring.nodewrapper;

import org.jruby.ast.ModuleNode;

public class ModuleNodeWrapper implements INodeWrapper {
	private final ModuleNode moduleNode;
	private ModuleNodeWrapper parentModule;

	public ModuleNodeWrapper(ModuleNode moduleNode, ModuleNodeWrapper parentModule) {
		this.moduleNode = moduleNode;
		this.parentModule = parentModule;
	}

	public ModuleNode getWrappedNode() {
		return moduleNode;
	}

	public ModuleNodeWrapper getParentModule() {
		return parentModule;
	}

	public void setParentModule(ModuleNodeWrapper parentModule) {
		this.parentModule = parentModule;
	}

	public String getName() {
		return moduleNode.getCPath().getName();
	}

	public String getFullName() {
		return (parentModule != null ? parentModule.getFullName() + "::" : "") + getName();
	}
}
