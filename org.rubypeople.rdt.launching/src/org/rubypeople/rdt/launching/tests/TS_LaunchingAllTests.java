package org.rubypeople.rdt.launching.tests;

import org.rubypeople.rdt.internal.launching.TS_Launching;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_LaunchingAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.launching.tests");
		//$JUnit-BEGIN$
		suite.addTest(TS_Launching.suite());
		//$JUnit-END$
		return suite;
	}
}
