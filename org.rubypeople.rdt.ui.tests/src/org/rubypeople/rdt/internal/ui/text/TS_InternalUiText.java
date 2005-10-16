package org.rubypeople.rdt.internal.ui.text;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_InternalUiText {
    public static Test suite() {
        TestSuite suite = new TestSuite("org.rubypeople.rdt.internal.ui.text");      
        suite.addTestSuite(TC_RubyPartitionScanner.class);
        return suite;
    }
}
