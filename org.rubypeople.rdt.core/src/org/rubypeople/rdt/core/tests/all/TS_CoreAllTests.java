package org.rubypeople.rdt.core.tests.all;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.core.TS_Core;
import org.rubypeople.rdt.internal.core.TS_InternalCore;

public class TS_CoreAllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.core.tests.all");
		//$JUnit-BEGIN$
		suite.addTest(TS_Core.suite());
		suite.addTest(TS_InternalCore.suite());
		//$JUnit-END$
		return suite;
	}
}
