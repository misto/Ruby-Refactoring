package org.rubypeople.rdt.core.tests.all;

import org.rubypeople.rdt.internal.core.TS_Core;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_CoreAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.core.tests.all");
		//$JUnit-BEGIN$
		suite.addTest(TS_Core.suite());
		//$JUnit-END$
		return suite;
	}
}
