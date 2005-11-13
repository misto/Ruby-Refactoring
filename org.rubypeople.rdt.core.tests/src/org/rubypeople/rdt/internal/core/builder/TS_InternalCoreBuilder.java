/*
 * Author: David Corbin
 *
 * Copyright (c) 2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.internal.core.builder;

import junit.framework.Test;
import junit.framework.TestSuite;

public class TS_InternalCoreBuilder {

    public static Test suite() {
        TestSuite suite = new TestSuite();
    
        suite.addTestSuite(TC_TaskCompiler.class);
        suite.addTestSuite(TC_RubyCodeAnalyzer.class);
        suite.addTestSuite(TC_MassIndexUpdater.class);
        suite.addTestSuite(TC_IndexUpdater.class);
        suite.addTestSuite(TC_CleanRdtCompiler.class);
        suite.addTestSuite(TC_IncrementalRdtCompiler.class);
        suite.addTestSuite(TC_MassIndexUpdaterJob.class);

        return suite;
    }
}
