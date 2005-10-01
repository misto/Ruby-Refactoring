package org.rubypeople.rdt.internal.core.builder;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_CoreBuilder {

    public static Test suite() {
        TestSuite suite = new TestSuite();
    
        suite.addTestSuite(TC_TaskCompiler.class);
        suite.addTestSuite(TC_RdtCompiler.class);

        return suite;
    }
}
