package org.rubypeople.rdt.internal.core.symbols;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_CoreSymbols extends TestSuite {

    public static Test suite() {
        TestSuite suite = new TestSuite();
    
        suite.addTestSuite(TC_ClassSymbol.class);
        suite.addTestSuite(TC_Location.class);
        suite.addTestSuite(TC_SymbolIndex.class);

        return suite;
    }
}