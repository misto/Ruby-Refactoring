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

package org.rubypeople.rdt.internal.core;

import java.util.Collection;
import java.util.List;

import junit.framework.Assert;

import org.rubypeople.rdt.internal.core.builder.MassIndexUpdater;

public class ShamMassIndexUpdater extends MassIndexUpdater {

    private Collection projectsArg;

    public ShamMassIndexUpdater() {
        super(null);
    }

    public void assertProjectsUpdated(Collection expectedProjects) {
        Assert.assertEquals(expectedProjects, projectsArg);
    }
    
    public void updateProjects(List projects) {
        projectsArg = projects;
    }

    public void assertUpdateProjectsNotCalled() {
        Assert.assertNull("Unexpected call to updateProjects", projectsArg);
    }
    

}
