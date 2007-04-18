package org.rubypeople.rdt.refactoring.core.extractconstant;

import org.jruby.ast.ArrayNode;
import org.jruby.ast.BignumNode;
import org.jruby.ast.FalseNode;
import org.jruby.ast.FixnumNode;
import org.jruby.ast.HashNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.TrueNode;
import org.jruby.ast.ZArrayNode;
import org.rubypeople.rdt.refactoring.core.IRefactoringConfig;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;

public class ExtractConstantConditionChecker extends RefactoringConditionChecker {

	private ExtractConstantConfig config;

	public ExtractConstantConditionChecker(ExtractConstantConfig config) {
		super(config);
	}
	
	protected void checkInitialConditions() {
		if(!existSelectedNodes()) {
			addError("There is nothing selected to extract.");
		} else if (!isPrimitive()) {
			addError("Extracting constant not possible on things other than strings, numbers, hashes or arrays.");
		}
	}

	/**
	 * Tell whether the node selected is a "primitive" - base types that are easy to extract,
	 * not method calls.
	 * @return
	 */
	private boolean isPrimitive() {
		return (config.getSelectedNodes() instanceof ZArrayNode) ||
		(config.getSelectedNodes() instanceof ArrayNode) ||
		(config.getSelectedNodes() instanceof HashNode) ||
		(config.getSelectedNodes() instanceof FixnumNode) ||
		(config.getSelectedNodes() instanceof BignumNode) ||
		(config.getSelectedNodes() instanceof NilNode) ||
		(config.getSelectedNodes() instanceof TrueNode) ||
		(config.getSelectedNodes() instanceof FalseNode) ||
		(config.getSelectedNodes() instanceof StrNode);
	}
	
	private boolean existSelectedNodes() {
		return !NodeProvider.isEmptyNode(config.getSelectedNodes());
	}

	@Override
	public void init(IRefactoringConfig configObj) {
		this.config = (ExtractConstantConfig) configObj;
		config.init();
		if (!NodeProvider.isEmptyNode(config.getSelectedNodes())) {
			// FIXME Do something!?
		}			
	}

}
