package com.aptana.rdt.internal.core.parser.warnings;

import org.rubypeople.rdt.internal.core.parser.warnings.RubyLintVisitor;

import com.aptana.rdt.internal.parser.warnings.SimilarVariableNameVisitor;

public class TC_SimilarVariableNameVisitor extends WarningVisitorTest {

	@Override
	protected RubyLintVisitor createVisitor(String code) {
		return new SimilarVariableNameVisitor(code);
	}
	
	public void testEmptyHasNoProblems() throws Exception {
		String code = "";
		parse(code);
		assertEquals(0, numberOfProblems());
	}
	
	public void testReferToLocalWithSimilarName() throws Exception {
		String code = "class Ralph\n" +
				"  def name\n" +
				"    local = 1\n" +
				"    puts lcal\n" +
				"  end\n" +
				"end\n";
		parse(code);
		assertEquals(1, numberOfProblems());
	}
	
	public void testTranspositionDoesntPushSmallVariablesAboveThreshold() throws Exception {
		String code = "class Ralph\n" +
				"  def name\n" +
				"    local = 1\n" +
				"    puts lcoal\n" +
				"  end\n" +
				"end\n";
		parse(code);
		assertEquals(1, numberOfProblems());
	}
	
	public void testLocalDoesntClashWithInstance() throws Exception {
		String code = "class Ralph\n" +
				"  def name\n" +
				"    local = 1\n" +
				"    puts @local\n" +
				"  end\n" +
				"end\n";
		parse(code);
		assertEquals(0, numberOfProblems());
	}
	
	public void testLocalDoesntClashWithClassVar() throws Exception {
		String code = "class Ralph\n" +
				"  def name\n" +
				"    localcalifragillistic = 1\n" +
				"    puts @@localcalifragillistic\n" +
				"  end\n" +
				"end\n";
		parse(code);
		assertEquals(0, numberOfProblems());
	}
	
	public void testInstanceDoesntClashWithClassVar() throws Exception {
		String code = "class Ralph\n" +
				"  def name\n" +
				"    @localcalifragillistic = 1\n" +
				"    puts @@localcalifragillistic\n" +
				"  end\n" +
				"end\n";
		parse(code);
		assertEquals(0, numberOfProblems());
	}
	
	public void testReallySmallVariablesDontTriggerProblem() throws Exception {
		String code = "class Ralph\n" +
				"  def name\n" +
				"    @ca = 1\n" +
				"    puts @cb\n" +
				"  end\n" +
				"end\n";
		parse(code);
		assertEquals(0, numberOfProblems());
	}
	
	public void testReallySmallClassVariablesDontTriggerProblem() throws Exception {
		String code = "class Ralph\n" +
				"  def name\n" +
				"    @@a = 1\n" +
				"    puts @@b\n" +
				"  end\n" +
				"end\n";
		parse(code);
		assertEquals(0, numberOfProblems());
	}
	
	public void testTooDisimilarNameWontTriggerProblem() throws Exception {
		String code = "class Ralph\n" +
				"  def name\n" +
				"    local = 1\n" +
				"    puts llal\n" +
				"  end\n" +
				"end\n";
		parse(code);
		assertEquals(0, numberOfProblems());
	}
	
//	 TODO Also watch for similarity in constant names?

	public void testReferToInstanceVarWithSimilarName() throws Exception {
		String code = "class Ralph\n" +
				"  def name\n" +
				"    @local = 1\n" +
				"    puts @lcal\n" +
				"  end\n" +
				"end\n";
		parse(code);
		assertEquals(1, numberOfProblems());
	}
	
	public void testReferToClassVarWithSimilarName() throws Exception {
		String code = "class Ralph\n" +
				"  def self.name\n" +
				"    @@local = 1\n" +
				"    puts @@lcal\n" +
				"  end\n" +
				"end\n";
		parse(code);
		assertEquals(1, numberOfProblems());
	}

}