package org.rubypeople.rdt.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.core.tests.core.TS_Core;
import org.rubypeople.rdt.internal.debug.ui.tests.launcher.TS_Launcher;
import org.rubypeople.rdt.internal.launching.tests.TS_Launching;
import org.rubypeople.rdt.internal.ui.tests.TS_UiTests;

public class TS_RdtAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.tests.all");
		//$JUnit-BEGIN$
		suite.addTest(TS_Core.suite());
		suite.addTest(TS_Launcher.suite());
		suite.addTest(TS_Launching.suite());
		suite.addTest(TS_UiTests.suite());
		//$JUnit-END$
		return suite;
	}
}
