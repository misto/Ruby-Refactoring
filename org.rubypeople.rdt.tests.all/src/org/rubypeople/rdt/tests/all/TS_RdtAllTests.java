package org.rubypeople.rdt.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.core.tests.TS_CoreAllTests;
import org.rubypeople.rdt.internal.debug.ui.tests.TS_DebugUiAllTests;

public class TS_RdtAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.tests.all");
		//$JUnit-BEGIN$
		suite.addTest(TS_CoreAllTests.suite());
		suite.addTest(TS_DebugUiAllTests.suite());
//		suite.addTest(TS_LaunchingAllTests.suite());
		//$JUnit-END$
		return suite;
	}
}
