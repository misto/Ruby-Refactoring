package org.rubypeople.rdt.internal.core.parser.warnings;

import org.jruby.ast.BlockNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.SClassNode;
import org.jruby.ast.WhenNode;
import org.jruby.ast.visitor.AbstractVisitor;
import org.jruby.evaluator.Instruction;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.IProblem;
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
			problem = new Error(position, message, getProblemID());
		else
		  problem = new Warning(position, message, getProblemID());
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
	
	/**
	 * Meant to be overriden by classes needing to perform some action when a class definition was exited.
	 * @param iVisited
	 */
	public void exitClassNode(ClassNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a method definition.
	 * @param iVisited
	 */
	public void exitDefnNode(DefnNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitIfNode(IfNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitBlockNode(BlockNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitDefsNode(DefsNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitModuleNode(ModuleNode iVisited) {}

	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitWhenNode(WhenNode iVisited) {}
	
	/**
	 * To be overriden by subclasses who need to run particular behavior/code when exiting a singleton method definition.
	 * @param iVisited
	 */
	public void exitSClassNode(SClassNode iVisited) {}
	
	protected int getProblemID() {
		return IProblem.Uncategorized;
	}
	
}
