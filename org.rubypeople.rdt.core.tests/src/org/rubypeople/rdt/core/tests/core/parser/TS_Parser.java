package org.rubypeople.rdt.core.tests.core.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_Parser {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.core.tests.core.parser");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(TC_RubyClass.class));
		suite.addTest(new TestSuite(TC_Tree.class));
		//$JUnit-END$
		return suite;
	}
}
