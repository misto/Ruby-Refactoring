package org.rubypeople.rdt.internal.core;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.rubypeople.rdt.internal.core.builder.TS_CoreBuilder;
import org.rubypeople.rdt.internal.core.parser.TS_CoreParser;
import org.rubypeople.rdt.internal.core.symbols.TS_CoreSymbols;

public class TS_Core {

	public static Test suite() {
		TestSuite suite = new TestSuite();
	
		suite.addTestSuite(TC_LoadPathEntry.class);
		suite.addTestSuite(TC_RubyCore.class);
		suite.addTestSuite(TC_RubyProject.class);
		suite.addTest(TS_CoreBuilder.suite());
		suite.addTest(TS_CoreParser.suite());
		suite.addTest(TS_CoreSymbols.suite());

		return suite;
	}
}