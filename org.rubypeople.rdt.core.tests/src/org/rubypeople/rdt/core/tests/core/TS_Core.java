package org.rubypeople.rdt.core.tests.core;

import junit.framework.TestSuite;

public class TS_Core extends TestSuite {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(TC_LoadPathEntry.class);
		suite.addTestSuite(TC_RubyCore.class);

		return suite;
	}
}