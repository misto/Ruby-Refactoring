package org.rubypeople.rdt.refactoring.core.extractconstant;

import org.jruby.ast.Node;
import org.rubypeople.rdt.refactoring.editprovider.ReplaceEditProvider;

public class ExtractedConstantCall extends ReplaceEditProvider {

	private ExtractConstantConfig config;

	public ExtractedConstantCall(ExtractConstantConfig config) {
		super(false);
		this.config = config;
	}

	protected int getOffsetLength() {
		return getEndOffset() - getStartOffset();
	}

	private int getStartOffset() {
		return config.getSelectedNodes().getPosition().getStartOffset();
	}

	private int getEndOffset() {
		return config.getSelectedNodes().getPosition().getEndOffset();
	}

	protected Node getEditNode(int offset, String document) {
		return config.getConstantCallNode();
	}

	protected int getOffset(String document) {
		return getStartOffset();
	}
}
