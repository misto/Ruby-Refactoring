package org.rubypeople.rdt.internal.debug.ui.tests;

import org.rubypeople.rdt.internal.debug.ui.tests.launcher.TS_Launcher;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_DebugUiAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.debug.ui.tests.all");
		//$JUnit-BEGIN$
		suite.addTest(TS_Launcher.suite());
		//$JUnit-END$
		return suite;
	}
}
