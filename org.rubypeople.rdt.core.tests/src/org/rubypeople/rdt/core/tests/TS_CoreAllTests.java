package org.rubypeople.rdt.core.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.core.tests.core.TS_Core;

public class TS_CoreAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.core.tests.all");
		//$JUnit-BEGIN$
		suite.addTest(TS_Core.suite());
		//$JUnit-END$
		return suite;
	}
}
