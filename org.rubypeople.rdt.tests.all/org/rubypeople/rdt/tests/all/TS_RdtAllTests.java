package org.rubypeople.rdt.tests.all;

import org.rubypeople.rdt.core.tests.all.TS_CoreAllTests;
import org.rubypeople.rdt.debug.ui.tests.all.TS_DebugUiAllTests;
import org.rubypeople.rdt.launching.tests.TS_LaunchingAllTests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_RdtAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.tests.all");
		//$JUnit-BEGIN$
		suite.addTest(TS_CoreAllTests.suite());
		suite.addTest(TS_DebugUiAllTests.suite());
		suite.addTest(TS_LaunchingAllTests.suite());
		//$JUnit-END$
		return suite;
	}
}
