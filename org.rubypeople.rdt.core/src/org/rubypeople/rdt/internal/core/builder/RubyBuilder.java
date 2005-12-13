/*
?* Author: Chris, David Corbin
?*
?* Copyright (c) 2005 RubyPeople.
?*
?* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
?*/

package org.rubypeople.rdt.internal.core.builder;

import java.util.Date;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;


public class RubyBuilder extends IncrementalProjectBuilder {

    public static boolean DEBUG;

    private IProject currentProject;

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
	    this.currentProject = getProject();
		if (currentProject == null || !currentProject.isAccessible()) 
            return null;
		
        if (DEBUG)
            RubyCore.trace("Started " + buildType(kind) + " build of " + buildDescription()); //$NON-NLS-1$

        AbstractRdtCompiler compiler = createCompiler(kind);
        compiler.compile(monitor);

        if (DEBUG)
            RubyCore.trace("Finished build of " + buildDescription()); //$NON-NLS-1$
		return null;
	}
    
    private AbstractRdtCompiler createCompiler(int kind) {
        SymbolIndex symbolIndex = RubyCore.getPlugin().getSymbolIndex();
        if (isPartialBuild(kind))
            return new IncrementalRdtCompiler(currentProject, getDelta(currentProject), symbolIndex);
        return new CleanRdtCompiler(currentProject, symbolIndex);
        
    }
    private String buildType(int kind) {
        return isPartialBuild(kind) ? "Incremental" : "Full";
    }
    
    private String buildDescription() {
        return currentProject.getName() + " @ " + new Date(System.currentTimeMillis());
    }

    private boolean isPartialBuild(int kind) {
        return kind == INCREMENTAL_BUILD || kind == AUTO_BUILD;
    }
    
    public static void setVerbose(boolean verbose) {
        RubyBuilder.DEBUG = verbose;
    }
}
