package org.rubypeople.rdt.core.tests.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.core.tests.core.parser.TC_RubyParser;

public class TS_Core {

	public static Test suite() {
		TestSuite suite = new TestSuite();
	
		suite.addTestSuite(TC_LoadPathEntry.class);
		suite.addTestSuite(TC_ResourceAdapterFactory.class);
		suite.addTestSuite(TC_RubyCore.class);
		suite.addTestSuite(TC_RubyProject.class);
		suite.addTestSuite(TC_RubyParser.class);

		return suite;
	}
}