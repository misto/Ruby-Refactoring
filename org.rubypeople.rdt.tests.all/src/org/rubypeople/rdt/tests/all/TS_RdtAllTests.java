package org.rubypeople.rdt.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.internal.core.TS_Core;
import org.rubypeople.rdt.internal.formatter.TC_CodeFormatter;
import org.rubypeople.rdt.debug.core.tests.TS_Debug;
import org.rubypeople.rdt.internal.debug.ui.tests.launcher.TS_Launcher;
import org.rubypeople.rdt.internal.launching.tests.TS_Launching;
import org.rubypeople.rdt.internal.ui.TS_UiTests;

public class TS_RdtAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("RDT all tests");
		//$JUnit-BEGIN$

		// org.rubypeople.rdt.core.tests
		suite.addTest(TS_Core.suite());
		suite.addTestSuite(TC_CodeFormatter.class) ;		
		
		// org.rubypeople.rdt.launching.tests
		suite.addTest(TS_Launching.suite());
		
		// org.rubypeople.rdt.ui.tests
		suite.addTest(TS_UiTests.suite());
		
		// org.rubypeople.rdt.debug.core.tests
		suite.addTest(TS_Debug.suite());
		
		// org.rubypeople.rdt.debug.ui.tests
		suite.addTest(TS_Launcher.suite());

		//$JUnit-END$
		return suite;
	}
}
