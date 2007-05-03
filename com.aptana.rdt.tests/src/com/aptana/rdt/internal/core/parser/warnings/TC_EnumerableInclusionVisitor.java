package com.aptana.rdt.internal.core.parser.warnings;

import org.rubypeople.rdt.internal.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.internal.parser.warnings.EnumerableInclusionVisitor;

public class TC_EnumerableInclusionVisitor extends WarningVisitorTest {

	@Override
	protected RubyLintVisitor createVisitor(String code) {
		return new EnumerableInclusionVisitor(code);
	}
	
	public void testBasicCase() {
		parse("class Chris\n" +
				"  include Enumerable\n" +
				"end\n");
		assertEquals(1, numberOfProblems());
	}

	public void testNoFalsePositive() {
		parse("class Chris\n" +
				"  include Enumerable\n" +
				"  def each\n" +
				"    yield 1\n" +
				"  end\n" +
				"end\n");
		assertEquals(0, numberOfProblems());
	}
	
}
