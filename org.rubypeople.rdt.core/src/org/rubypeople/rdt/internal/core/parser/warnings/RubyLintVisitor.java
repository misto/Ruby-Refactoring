package org.rubypeople.rdt.internal.core.parser.warnings;

import org.jruby.ast.Node;
import org.jruby.ast.visitor.AbstractVisitor;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.parser.IProblem;
import org.rubypeople.rdt.internal.core.parser.Error;
import org.rubypeople.rdt.internal.core.parser.NodeUtil;
import org.rubypeople.rdt.internal.core.parser.Warning;

public abstract class RubyLintVisitor extends AbstractVisitor {

	private IProblemRequestor problemRequestor;
	private String contents;

	public RubyLintVisitor(String contents, IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
		this.contents = contents;
	}
	
	protected String getSource(Node node) {
		return NodeUtil.getSource(contents, node);
	}

	protected void createProblem(ISourcePosition position, String message) {
		String value = RubyCore.getOption(getOptionKey());
		if (value != null && value.equals(RubyCore.IGNORE))
			return;
		IProblem problem;
		if (value != null && value.equals(RubyCore.ERROR))
			problem = new Error(position, message);
		else
		  problem = new Warning(position, message);
		problemRequestor.acceptProblem(problem);
	}
	
	@Override
	protected Instruction visitNode(Node iVisited) {
		return null;
	}

	/**
	 * The key used to store the error/warning severity option.
	 * @return a String key
	 */
	abstract protected String getOptionKey();

}
