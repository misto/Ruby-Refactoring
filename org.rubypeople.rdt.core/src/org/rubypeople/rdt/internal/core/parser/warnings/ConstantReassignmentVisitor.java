package org.rubypeople.rdt.internal.core.parser.warnings;

import java.util.HashSet;
import java.util.Set;

import org.jruby.ast.ConstDeclNode;
import org.jruby.evaluator.Instruction;

public class ConstantReassignmentVisitor extends RubyLintVisitor {

	private Set<String> assignedConstants;
	
	public ConstantReassignmentVisitor(String contents) {
		super(contents);
		assignedConstants = new HashSet<String>();
	}

	@Override
	protected String getOptionKey() {
		// FIXME Set up a compiler option for this!
		return null;
	}
	
	public Instruction visitConstDeclNode(ConstDeclNode iVisited) {
		String name = iVisited.getName();
		if (assignedConstants.contains(name)) {
			createProblem(iVisited.getPosition(), "Reassignment of a constant");
		} else
			assignedConstants.add(name);
		return super.visitConstDeclNode(iVisited);
	}

}
