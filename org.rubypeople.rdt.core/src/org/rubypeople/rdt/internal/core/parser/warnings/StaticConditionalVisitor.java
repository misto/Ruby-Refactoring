package org.rubypeople.rdt.internal.core.parser.warnings;

import org.jruby.ast.FalseNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.Node;
import org.jruby.ast.TrueNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.core.IProblemRequestor;

public class StaticConditionalVisitor extends RubyLintVisitor {

	public StaticConditionalVisitor(String contents, IProblemRequestor problemRequestor) {
		super(contents, problemRequestor);
	}

	@Override
	protected String getOptionKey() {
//		 FIXME Set up a compiler option for this!
		return null;
	}
	
	public Instruction visitIfNode(IfNode iVisited) {
		Node condition = iVisited.getCondition();
		if (condition instanceof TrueNode) {
			createProblem(iVisited.getPosition(), "Condition is always true");
		} else if ((condition instanceof FalseNode)	|| (condition instanceof NilNode)) {
			createProblem(iVisited.getPosition(), "Condition is always false");
		}
		return super.visitIfNode(iVisited);
	}

}
