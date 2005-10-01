package org.rubypeople.rdt.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.debug.core.tests.TS_Debug;

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
