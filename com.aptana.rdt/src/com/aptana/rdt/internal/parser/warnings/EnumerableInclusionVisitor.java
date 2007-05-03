package com.aptana.rdt.internal.parser.warnings;

import org.jruby.ast.ArrayNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.internal.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class EnumerableInclusionVisitor extends RubyLintVisitor {
	
	private static final String INCLUDE = "include";
	private static final String ENUMERABLE_METHOD = "each";
	private static final String ENUMERABLE = "Enumerable";
	
	private boolean includedEnumerable = false;
	private boolean definedEnumerableMethod;
	private ISourcePosition pos;
	
	public EnumerableInclusionVisitor(String code) {
		super(code);
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_ENUMERABLE_MISSING_METHOD;
	}

	@Override
	public Instruction visitFCallNode(FCallNode iVisited) {
		if (includedEnumerable) return null;
		String callName = iVisited.getName();
		if (!callName.equals(INCLUDE))
			return null;
		Node args = iVisited.getArgsNode();
		if (args instanceof ArrayNode) {
			ArrayNode array = (ArrayNode) args;
			for (Object arg : array.childNodes()) {
				if (!(arg instanceof ConstNode))
					continue;
				ConstNode constNode = (ConstNode) arg;
				if (!(constNode.getName().equals(ENUMERABLE)))
					continue;
				pos = constNode.getPosition();
				includedEnumerable = true;
				return null;
			}
		}
		return null;
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		String methodName = iVisited.getName();
		if (methodName.equals(ENUMERABLE_METHOD)) {
			definedEnumerableMethod = true;
		}
		return super.visitDefnNode(iVisited);
	}
	
	@Override
	public void exitClassNode(ClassNode iVisited) {
		if (includedEnumerable && !definedEnumerableMethod) {
			createProblem(pos, "Included Enumerable but did not define an each method");
		}
		includedEnumerable = false;
		pos = null;
		definedEnumerableMethod = false;
	}

}
