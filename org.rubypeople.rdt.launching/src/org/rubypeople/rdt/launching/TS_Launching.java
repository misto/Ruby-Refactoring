package org.rubypeople.rdt.launching;

import junit.framework.TestSuite;

public class TS_Launching extends TestSuite {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(TC_RubyInterpreter.class);
		suite.addTestSuite(TC_RubyRuntime.class);

		return suite;
	}
}