package org.rubypeople.rdt.internal.core;

import junit.framework.TestSuite;

public class TS_Core extends TestSuite {

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();

		suite.addTestSuite(TC_RubyProject.class);

		return suite;
	}
}