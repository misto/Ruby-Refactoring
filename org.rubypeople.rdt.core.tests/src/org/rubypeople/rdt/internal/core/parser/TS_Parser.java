package org.rubypeople.rdt.internal.core.parser;

import org.rubypeople.rdt.internal.core.parser.ast.*;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_Parser {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.internal.core.parser");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(TC_RubyParser.class));
		suite.addTest(new TestSuite(TC_RubyParserStack.class));
		suite.addTest(new TestSuite(TC_RubyParserUtil.class));
		suite.addTest(new TestSuite(TC_RubyElement.class));
		//$JUnit-END$
		return suite;
	}
}
