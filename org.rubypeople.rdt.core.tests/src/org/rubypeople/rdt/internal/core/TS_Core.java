package org.rubypeople.rdt.internal.core;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_Core {

	public static Test suite() {
		TestSuite suite = new TestSuite();
	
		suite.addTestSuite(TC_LoadPathEntry.class);
		suite.addTestSuite(TC_RubyCore.class);
		suite.addTestSuite(TC_RubyProject.class);

		return suite;
	}
}