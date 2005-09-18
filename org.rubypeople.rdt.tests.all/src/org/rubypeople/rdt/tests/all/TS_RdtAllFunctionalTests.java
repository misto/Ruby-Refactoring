package org.rubypeople.rdt.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.internal.core.TS_Core;
import org.rubypeople.rdt.internal.formatter.TC_CodeFormatter;
import org.rubypeople.rdt.debug.core.tests.TS_Debug;
import org.rubypeople.rdt.internal.debug.ui.tests.launcher.TS_Launcher;
import org.rubypeople.rdt.internal.launching.tests.TS_Launching;
import org.rubypeople.rdt.internal.ui.TS_UiTests;

public class TS_RdtAllFunctionalTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("RDT all functional tests");
		//$JUnit-BEGIN$
		
		// org.rubypeople.rdt.debug.core.tests
		suite.addTest(TS_Debug.suite());

		//$JUnit-END$
		return suite;
	}
}
