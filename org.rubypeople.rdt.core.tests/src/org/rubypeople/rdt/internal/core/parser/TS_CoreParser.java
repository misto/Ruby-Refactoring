package org.rubypeople.rdt.internal.core.parser;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_CoreParser {

    public static Test suite() {
        TestSuite suite = new TestSuite();
    
        suite.addTestSuite(TC_TaskParser.class);
        suite.addTestSuite(TC_ImmediateWarnings.class);

        return suite;
    }
}
