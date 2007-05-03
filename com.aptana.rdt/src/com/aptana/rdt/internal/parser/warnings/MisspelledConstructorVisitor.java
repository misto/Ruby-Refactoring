package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.DefnNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.internal.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.RubyRedPlugin;

public class MisspelledConstructorVisitor extends RubyLintVisitor {

	public MisspelledConstructorVisitor(String contents) {
		super(contents);
	}

	public Instruction visitDefnNode(DefnNode iVisited) {
		String methodName = iVisited.getName();
		if (methodName.equals("intialize") || methodName.equals("initialise") || methodName.equals("initalize")) {
			createProblem(iVisited.getPosition(), "Possible mis-spelling of constructor");
		}
		return null;
	}
	
	@Override
	protected String getOptionKey() {
		return RubyRedPlugin.COMPILER_PB_MISSPELLED_CONSTRUCTOR;
	}

}
