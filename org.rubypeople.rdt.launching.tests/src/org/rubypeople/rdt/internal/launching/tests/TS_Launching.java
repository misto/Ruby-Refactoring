package org.rubypeople.rdt.internal.launching.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_Launching {

	public static Test suite() {
		TestSuite suite = new TestSuite();
	
		suite.addTestSuite(TC_RubyInterpreter.class);
		suite.addTestSuite(TC_RubyRuntime.class);
		suite.addTestSuite(TC_RunnerLaunching.class) ;
		return suite;
	}
}