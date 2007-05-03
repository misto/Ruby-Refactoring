package com.aptana.rdt.internal.core.parser.warnings;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.jruby.ast.Node;
import org.rubypeople.rdt.core.compiler.IProblem;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.parser.warnings.DelegatingVisitor;
import org.rubypeople.rdt.internal.core.parser.warnings.RubyLintVisitor;

public abstract class WarningVisitorTest extends TestCase {

	private MockProblemRequestor problemRequestor;
	private RubyParser parser;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		problemRequestor = new MockProblemRequestor();
		parser = new RubyParser();
	}

	protected void parse(String code) {
		Node root = parser.parse(code);
		List<RubyLintVisitor> visitors = new ArrayList<RubyLintVisitor>();
		visitors.add(createVisitor(code));
		DelegatingVisitor visitor = new DelegatingVisitor(visitors);
		root.accept(visitor);
	}

	public int numberOfProblems() {
		return problemRequestor.numberOfProblems();
	}
	
	protected IProblem getProblemAtLine(int i) {
		return problemRequestor.getProblemAtLine(i);
	}
	
	abstract protected RubyLintVisitor createVisitor(String code);
}
