package org.rubypeople.rdt.internal.debug.ui.tests.launcher;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.internal.debug.ui.tests.TC_RubyConsoleTracker;
import org.rubypeople.rdt.internal.debug.ui.tests.TC_RubySourceLocator;

public class TS_Launcher {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rubypeople.rdt.internal.debug.ui.launcher");
		//$JUnit-BEGIN$
		suite.addTest(new TestSuite(TC_RubyArgumentsTab.class));
		suite.addTest(new TestSuite(TC_RubyApplicationShortcut.class));
		suite.addTest(new TestSuite(TC_RubyEntryPointTab.class));
		suite.addTest(new TestSuite(TC_RubyEnvironmentTab.class));
		suite.addTest(new TestSuite(TC_RubySourceLocator.class));
		suite.addTest(new TestSuite(TC_RubyConsoleTracker.class));
		//$JUnit-END$
		return suite;
	}
}
