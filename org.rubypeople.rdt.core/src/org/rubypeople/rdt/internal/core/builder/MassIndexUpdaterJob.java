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

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.internal.core.symbols.SymbolSchedulingRule;

public class MassIndexUpdaterJob extends Job {

    private final List rubyProjects;
    private MassIndexUpdater massIndexUpdater;

    public MassIndexUpdaterJob(IndexUpdater indexUpdater, List rubyProjects) {
        this(new MassIndexUpdater(indexUpdater), rubyProjects);
    }

    public MassIndexUpdaterJob(MassIndexUpdater massIndexUpdater, List rubyProjects) {
        super("Update Ruby Symbol Index");
        this.massIndexUpdater = massIndexUpdater;
        this.rubyProjects = rubyProjects;
        setRule(new SymbolSchedulingRule());
        setSystem(true);
        setPriority(Job.LONG);
    }

    
    protected IStatus run(IProgressMonitor monitor) {
        massIndexUpdater.updateProjects(rubyProjects, monitor);
        return Status.OK_STATUS;
    }
   
    public boolean equals(Object obj) {
        if (!(obj instanceof MassIndexUpdaterJob))
            return false;
        MassIndexUpdaterJob that = (MassIndexUpdaterJob) obj;
        return this.rubyProjects.equals(that.rubyProjects)
            && massIndexUpdater.equals(massIndexUpdater);
    }
    
    public int hashCode() {
        return 0;
    }

}
