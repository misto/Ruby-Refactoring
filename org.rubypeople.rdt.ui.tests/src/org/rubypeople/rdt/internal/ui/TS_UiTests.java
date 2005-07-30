package org.rubypeople.rdt.internal.ui;

import org.rubypeople.rdt.internal.ui.rubyeditor.TC_TabExpander;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_UiTests {

	public static Test suite() {
		TestSuite suite = new TestSuite();		
		suite.addTestSuite(TC_RubyViewerFilter.class);
		suite.addTestSuite(TC_StackTraceLine.class);
		suite.addTestSuite(TC_ResourceAdapterFactory.class);
		suite.addTestSuite(TC_TabExpander.class);
		return suite;
	}
}