package org.rubypeople.rdt.internal.core;

import junit.framework.TestSuite;

public class TS_InternalCore extends TestSuite {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(TC_LoadPathEntry.class);
		suite.addTestSuite(TC_RubyCore.class);

		return suite;
	}
}