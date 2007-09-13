package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.compiler.BuildContext;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.core.compiler.CompilationParticipant;
import org.rubypeople.rdt.internal.core.RubyModelManager;

public abstract class AbstractRdtCompiler {

    protected final IProject project;
    protected final IMarkerManager markerManager;
    protected CompilationParticipant[] fParticipants;
    
    public AbstractRdtCompiler(IProject project, IMarkerManager markerManager) {
        this.project = project;
        this.markerManager = markerManager;
    }

    protected abstract void removeMarkers(IMarkerManager markerManager);
    protected abstract List<IFile> getFilesToCompile() throws CoreException;
    protected abstract List getFilesToClear() throws CoreException;

    public void compile(IProgressMonitor monitor) throws CoreException {    
    	IRubyProject rubyProject = getRubyProject();
        fParticipants = RubyModelManager.getRubyModelManager().compilationParticipants.getCompilationParticipants(rubyProject);        
        for (int i = 0; i < fParticipants.length; i++) {
        	fParticipants[i].aboutToBuild(rubyProject);
        }
        BuildContext[] files = getBuildContexts();
        int filesToClear = getFilesToClear().size();
        int taskCount = (filesToClear) + (files.length * fParticipants.length);
        
        monitor.beginTask("Building " + project.getName() + "...", taskCount);
        
        monitor.subTask("Removing Markers...");
        removeMarkers(markerManager);
        monitor.worked(filesToClear);
		
        monitor.subTask("Analyzing Files...");
        compileFiles(files, monitor);
		
        monitor.done();
    }   

	private void compileFiles(BuildContext[] contexts, IProgressMonitor monitor) throws CoreException {
 		if (fParticipants != null) {
 			for (int i = 0; i < fParticipants.length; i++) {
 				if (monitor.isCanceled())
 	                return;
 				fParticipants[i].buildStarting(contexts, true);
 			}
 		}
 		for (int i = 0; i < contexts.length; i++) {
 			CategorizedProblem[] problems = contexts[i].getProblems();
 			if (problems == null || problems.length == 0) continue;
 			for (int j = 0; j < problems.length; j++) { 				
 				markerManager.addProblem(contexts[i].getFile(), problems[j]);
 			}
 		}
    }

	private BuildContext[] getBuildContexts() throws CoreException {
		return getBuildContexts(getFilesToCompile());
	}
	
    private BuildContext[] getBuildContexts(List<IFile> list) {
		List<BuildContext> contexts = new ArrayList<BuildContext>();
    	for (IFile file : list) {
			contexts.add(new BuildContext(file));
		}
    	return contexts.toArray(new BuildContext[contexts.size()]);
	}

	private IRubyProject getRubyProject() {
		return RubyCore.create(project);
	}
}
