package com.aptana.rdt.internal.parser.warnings;

import java.util.HashMap;
import java.util.Map;

import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.internal.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class MethodMissingWithoutRespondTo extends RubyLintVisitor {

	private Map<String, DefnNode> methods = new HashMap<String, DefnNode>();
	
	public MethodMissingWithoutRespondTo(String contents) {
		super(contents);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_METHOD_MISSING_NO_RESPOND_TO;
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		methods.put(iVisited.getName(), iVisited);
		return super.visitDefnNode(iVisited);
	}
	
	@Override
	public void exitClassNode(ClassNode iVisited) {
		if (methods.containsKey("method_missing") && !methods.containsKey("respond_to")) {
			createProblem(methods.get("method_missing").getNameNode().getPosition(), "Class defines method_missing, but does not define custom respond_to");
		}
		methods.clear();
		super.exitClassNode(iVisited);
	}

}
