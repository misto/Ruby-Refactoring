package org.rubypeople.rdt.internal.debug.ui.launcher;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_Launcher {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.internal.debug.ui.launcher");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(TC_RubyApplicationShortcut.class));
		//$JUnit-END$
		return suite;
	}
}
