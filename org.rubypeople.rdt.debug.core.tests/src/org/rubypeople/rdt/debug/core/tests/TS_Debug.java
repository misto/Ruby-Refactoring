package org.rubypeople.rdt.debug.core.tests;
import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_Debug {

	public static Test suite() {
		TestSuite suite = new TestSuite();
	
		suite.addTestSuite(TC_DebuggerCommunicationTest.class);
		suite.addTestSuite(TC_DebuggerProxyTest.class) ;
		suite.addTestSuite(TC_ReadStrategyTest.class) ;
		return suite;
	}
}