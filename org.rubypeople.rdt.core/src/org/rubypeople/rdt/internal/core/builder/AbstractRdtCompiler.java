package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
import java.util.Iterator;
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
import org.rubypeople.rdt.internal.core.util.ListUtil;

public abstract class AbstractRdtCompiler {

    protected final IProject project;
    protected final IMarkerManager markerManager;
    protected final List<SingleFileCompiler> singleFileCompilers;
    protected final List<MultipleFileCompiler> multiFileCompilers;
    
    public AbstractRdtCompiler(IProject project,
            IMarkerManager markerManager, List<SingleFileCompiler> singleCompilers, List<MultipleFileCompiler> multiFileCompilers) {
        this.project = project;
        this.markerManager = markerManager;
        this.singleFileCompilers = singleCompilers;
        this.multiFileCompilers = multiFileCompilers;
    }
    
    public AbstractRdtCompiler(IProject project,
            IMarkerManager markerManager, List<SingleFileCompiler> singleCompilers) {
        this(project, markerManager, singleCompilers, new ArrayList<MultipleFileCompiler>());
    }

    protected abstract void removeMarkers(IMarkerManager markerManager);
    protected abstract List<IFile> getFilesToCompile();
    protected abstract void analyzeFiles() throws CoreException;

    protected static List<SingleFileCompiler> singleFileCompilers(MarkerManager markerManager) {
        return ListUtil.create(new RubyCodeAnalyzer(markerManager), 
                new TaskCompiler(markerManager));
    }

    public void compile(IProgressMonitor monitor) throws CoreException {
        analyzeFiles();
        List<IFile> files = getFilesToCompile();
        int filesToClear = getFilesToClear().size();
        int taskCount = (filesToClear) +  (files.size() * (singleFileCompilers.size() + multiFileCompilers.size()));
        
        monitor.beginTask("Building "+project.getName() + "...", taskCount);
        monitor.subTask("Removing Markers...");
        
        removeMarkers(markerManager);
        monitor.worked(filesToClear);
		
        compileFiles(files, monitor);
		
        monitor.done();
    }
    
    protected abstract List getFilesToClear();

	private void compileFiles(List<IFile> list, IProgressMonitor monitor) throws CoreException {
        for (MultipleFileCompiler compiler : multiFileCompilers) {
        	if (monitor.isCanceled())
                return;
			compiler.compileFile(list, monitor);
		}       
    	for (IFile file : list) {
    		if (monitor.isCanceled())
                return;
            
            monitor.subTask(file.getFullPath().toString());
            compileFile(file, monitor);
		}
    	BuildContext[] contexts = getBuildContexts(list);
        CompilationParticipant[] participants = RubyModelManager.getRubyModelManager().compilationParticipants.getCompilationParticipants(getRubyProject());
 		if (participants != null) {
 			for (int i = 0; i < participants.length; i++) {
 				if (monitor.isCanceled())
 	                return;
 				participants[i].buildStarting(contexts, true);
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

	private void compileFile(IFile file, IProgressMonitor monitor) throws CoreException {
        for (Iterator<SingleFileCompiler> cIter = singleFileCompilers.iterator(); cIter.hasNext();) {
            SingleFileCompiler fileCompiler = cIter.next();
            fileCompiler.compileFile(file);
            monitor.worked(1);
        }
    }
}
