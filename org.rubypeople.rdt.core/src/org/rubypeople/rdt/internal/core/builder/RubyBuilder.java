package org.rubypeople.rdt.internal.core.builder;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.RubyCore;

/**
 * @author Chris
 * 
 */
public class RubyBuilder extends IncrementalProjectBuilder {

    private static final int TOTAL_WORK = 10000;

    private IProject currentProject;

	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) throws CoreException {
	    this.currentProject = getProject();
		monitor.beginTask("build", TOTAL_WORK);
		IProject[] returnProjects = new IProject[0];
		if (currentProject == null || !currentProject.isAccessible()) 
            return returnProjects;
		
        RubyCore.trace("Started " + buildType(kind) + " build of " + buildDescription()); //$NON-NLS-1$

        MarkerManager rubyMarkerManager = new MarkerManager();
        if (!isPartialBuild(kind))
            rubyMarkerManager.removeProblemsAndTasksFor(currentProject);
        
        List files = createFileFinder(kind).findFiles();
        doCompile(files, monitor);

        RubyCore.trace("Finished build of " + buildDescription()); //$NON-NLS-1$
		return returnProjects;
	}
    private String buildType(int kind) {
        return isPartialBuild(kind) ? "Incremental" : "Full";
    }
    
    private IFileFinder createFileFinder(int kind) {
        if (isPartialBuild(kind)) 
            return new IncrementalFileFinder(getDelta(currentProject));
        return new ProjectFileFinder(currentProject);
    }

    private String buildDescription() {
        return currentProject.getName() + " @ " + new Date(System.currentTimeMillis());
    }

    private boolean isPartialBuild(int kind) {
        return kind == INCREMENTAL_BUILD || kind == AUTO_BUILD;
    }

    protected void doCompile(List files, IProgressMonitor monitor) {
        new RubyCompiler(TOTAL_WORK/files.size()).compile(files, monitor);
	}}
