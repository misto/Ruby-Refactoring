package com.aptana.rdt.internal.parser.warnings;

import java.util.HashMap;
import java.util.Map;

import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.Node;
import org.jruby.ast.SelfNode;
import org.jruby.ast.VCallNode;
import org.jruby.evaluator.Instruction;
import org.jruby.runtime.Visibility;
import org.rubypeople.rdt.core.parser.warnings.RubyLintVisitor;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

import com.aptana.rdt.AptanaRDTPlugin;

public class UnusedPrivateMethodVisitor extends RubyLintVisitor {

	private Map<String, Node> privateMethods = new HashMap<String, Node>();
	private Visibility visibility;
	
	public UnusedPrivateMethodVisitor(String contents) {
		super(AptanaRDTPlugin.getDefault().getOptions(), contents);
		visibility = Visibility.PUBLIC;
	}
	
	public Instruction visitFCallNode(FCallNode iVisited) {
		privateMethods.remove(iVisited.getName()); // we've used the method
		// FIXME Handle case where we call public/private/protected methods with arguments (so current visibility is not changed, but existing methods' visibility is changed)
		return null;
	}

	public Instruction visitCallNode(CallNode iVisited) {
		Node receiver = iVisited.getReceiverNode();
		if (receiver instanceof SelfNode)
			privateMethods.remove(iVisited.getName()); // we've used the method
		return null;
	}
	
	public Instruction visitVCallNode(VCallNode iVisited) {
		privateMethods.remove(iVisited.getName()); // we've used the method
		if (iVisited.getName().equals("private")) {
			visibility = Visibility.PRIVATE;
		} else if (iVisited.getName().equals("protected")) {
			visibility = Visibility.PROTECTED;
		} else if (iVisited.getName().equals("public")) {
			visibility = Visibility.PUBLIC;
		}
		return null;
	}
	
	public Instruction visitClassNode(ClassNode iVisited) {
		privateMethods.clear();
		visibility = Visibility.PUBLIC;
		return null;
	}
	
	public void exitClassNode(ClassNode iVisited) {
		for (Node method : privateMethods.values()) {
			createProblem(method.getPosition(), "Unused private method " + ASTUtil.getNameReflectively(method));
		}		
	}
	
	public Instruction visitDefnNode(DefnNode iVisited) {
		if (visibility.isPrivate()) {
			privateMethods.put(iVisited.getName(), iVisited);
		}		
		return null;
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_UNUSED_PRIVATE_MEMBER;
	}	

}
