package com.aptana.rdt.internal.parser.warnings;

import java.util.Map;

import org.jruby.ast.CaseNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.IfNode;
import org.jruby.ast.WhenNode;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.internal.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.AptanaRDTPlugin;

public class TooManyBranchesVisitor extends RubyLintVisitor {

	private int maxBranches;
	private int branchCount;
	private Map fOptions;

	public TooManyBranchesVisitor(String contents) {
		this(AptanaRDTPlugin.getDefault().getOptions(), contents);		
	}
	
	public TooManyBranchesVisitor(Map options, String contents) {
		super(contents);
		fOptions = options;
		maxBranches = getInt(AptanaRDTPlugin.COMPILER_PB_MAX_BRANCHES, 5); 
		branchCount = 0;
	}
	private int getInt(String key, int defaultValue) {
		try {
			return Integer.parseInt((String) fOptions.get(key));
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	@Override
	protected String getOptionKey() {
		return AptanaRDTPlugin.COMPILER_PB_MAX_BRANCHES;
	}

	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		branchCount = 0;
		return super.visitDefsNode(iVisited);
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		branchCount = 0;
		return super.visitDefnNode(iVisited);
	}

	@Override
	public Instruction visitIfNode(IfNode iVisited) {
		// TODO Make sure this doesn't count modifiers
		if (iVisited.getThenBody() != null) {
			branchCount++;
		}
		if (iVisited.getElseBody() != null) {
			branchCount++;
		}
		return super.visitIfNode(iVisited);
	}

	@Override
	public Instruction visitCaseNode(CaseNode iVisited) {
		WhenNode when = (WhenNode) iVisited.getFirstWhenNode();
		while (when != null) {
			branchCount++;
			when = (WhenNode) when.getNextCase();
		}
		return super.visitCaseNode(iVisited);
	}

	public void exitDefnNode(DefnNode iVisited) {
		if (branchCount > maxBranches) {
			createProblem(iVisited.getNameNode().getPosition(), "Too many branches: " + branchCount);
		}
		branchCount = 0;
	}
	
	@Override
	public void exitDefsNode(DefsNode iVisited) {
		if (branchCount > maxBranches) {
			createProblem(iVisited.getNameNode().getPosition(), "Too many branches: " + branchCount);
		}
		branchCount = 0;
		super.exitDefsNode(iVisited);
	}
}
