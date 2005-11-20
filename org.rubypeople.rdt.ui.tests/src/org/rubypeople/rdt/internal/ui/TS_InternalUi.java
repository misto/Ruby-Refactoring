package org.rubypeople.rdt.internal.ui;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.internal.ui.symbols.TS_UiSymbols;

public class TS_InternalUi {

	public static Test suite() {
		TestSuite suite = new TestSuite("org.rubypeople.rdt.internal.ui");		
		suite.addTestSuite(TC_RubyViewerFilter.class);
		suite.addTestSuite(TC_StackTraceLine.class);
		suite.addTestSuite(TC_ResourceAdapterFactory.class);
		suite.addTestSuite(TC_RubyFileMatcher.class);
		suite.addTest(TS_UiSymbols.suite());
		return suite;
	}
}