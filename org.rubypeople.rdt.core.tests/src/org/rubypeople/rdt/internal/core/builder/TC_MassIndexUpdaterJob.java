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

import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.eclipse.shams.runtime.ShamMonitor;
import org.rubypeople.rdt.internal.core.ShamMassIndexUpdater;
import org.rubypeople.rdt.internal.core.util.ListUtil;

import junit.framework.TestCase;

public class TC_MassIndexUpdaterJob extends TestCase {
    public void testJob() throws Exception {
        ShamMassIndexUpdater massIndexUpdater = new ShamMassIndexUpdater();
        ShamProject project = new ShamProject("test1");
        MassIndexUpdaterJob job = new MassIndexUpdaterJob(massIndexUpdater, ListUtil.create(project));
        ShamMonitor monitor = new ShamMonitor();
        job.schedule();
        job.join();
        massIndexUpdater.assertProjectsUpdated(ListUtil.create(project));
    }
}
