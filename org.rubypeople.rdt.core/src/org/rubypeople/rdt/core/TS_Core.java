package org.rubypeople.rdt.core;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_Core {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.core");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(TC_RubyProject.class));
		//$JUnit-END$
		return suite;
	}
}
