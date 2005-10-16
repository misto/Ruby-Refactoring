package org.rubypeople.rdt.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.core.TS_Core;
import org.rubypeople.rdt.debug.ui.tests.TS_DebugUi;
import org.rubypeople.rdt.internal.launching.TS_InternalLaunching;
import org.rubypeople.rdt.internal.ui.TS_InternalUi;

public class TS_RdtAllUnitTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("RDT all unit tests");
		//$JUnit-BEGIN$

		// org.rubypeople.rdt.core.tests
		suite.addTest(TS_Core.suite());
		
		// org.rubypeople.rdt.launching.tests
		suite.addTest(TS_InternalLaunching.suite());
		
		// org.rubypeople.rdt.ui.tests
		suite.addTest(TS_InternalUi.suite());
		
		// org.rubypeople.rdt.debug.ui.tests
		suite.addTest(TS_DebugUi.suite());

		//$JUnit-END$
		return suite;
	}
}
