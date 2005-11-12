package org.rubypeople.rdt.internal.core.builder;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;

public class CleanRdtCompiler extends AbstractRdtCompiler  {

    private List projectFiles;

    public CleanRdtCompiler(IProject project, SymbolIndex symbolIndex) {
        this(project, symbolIndex, new MarkerManager());
    }

    public CleanRdtCompiler(IProject project, SymbolIndex symbolIndex, 
            IMarkerManager markerManager, List singleCompilers) {
        super(project, symbolIndex, markerManager, singleCompilers);
    }

    private CleanRdtCompiler(IProject project, SymbolIndex symbolIndex, 
            MarkerManager markerManager) {
        this(project,symbolIndex, markerManager, compilers(markerManager));
    }

    protected void flushIndexEntries(SymbolIndex symbolIndex) {
        symbolIndex.flush(project);
    }

    protected void removeMarkers(IMarkerManager markerManager) {
        markerManager.removeProblemsAndTasksFor(project);
    }

    protected List getFilesToClear() {
        return projectFiles;
    }

    protected List getFilesToCompile() {
        return projectFiles;
    }

    protected void analyzeFiles() throws CoreException {
        ProjectFileFinder finder = new ProjectFileFinder(project);
        projectFiles = finder.findFiles();
    }

}
