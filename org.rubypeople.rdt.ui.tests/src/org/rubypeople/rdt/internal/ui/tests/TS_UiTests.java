package org.rubypeople.rdt.internal.ui.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_UiTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		
		suite.addTestSuite(TC_RubyViewerFilter.class);
		suite.addTestSuite(TC_RubyParser.class);
		
		return suite;
	}
}