package org.rubypeople.rdt.internal.formatter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_InternalFormatter {

	public static Test suite() {
		TestSuite suite = new TestSuite();
		suite.addTestSuite(TC_CodeFormatter.class);
		return suite;
	}
}