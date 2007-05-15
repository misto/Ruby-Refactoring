package org.rubypeople.rdt.internal.core.builder;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class CleanRdtCompiler extends AbstractRdtCompiler  {

    private List<IFile> projectFiles;

    public CleanRdtCompiler(IProject project) {
        this(project, new MarkerManager());
    }

    public CleanRdtCompiler(IProject project, 
            IMarkerManager markerManager, List singleCompilers) {
        super(project, markerManager, singleCompilers);
    }

    private CleanRdtCompiler(IProject project,
            MarkerManager markerManager) {
        this(project, markerManager, singleFileCompilers(markerManager));
    }

    protected void removeMarkers(IMarkerManager markerManager) {
        markerManager.removeProblemsAndTasksFor(project);
    }

    protected List<IFile> getFilesToClear() {
        return projectFiles;
    }

    protected List<IFile> getFilesToCompile() {
        return projectFiles;
    }

    protected void analyzeFiles() throws CoreException {
        ProjectFileFinder finder = new ProjectFileFinder(project);
        projectFiles = finder.findFiles();
    }

}
