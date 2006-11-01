package org.rubypeople.rdt.internal.core.builder;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;
import org.rubypeople.rdt.internal.core.util.ListUtil;

public abstract class AbstractRdtCompiler {

    protected final IProject project;
    protected final IMarkerManager markerManager;
    protected final SymbolIndex symbolIndex;
    protected final List compilers;
    
    public AbstractRdtCompiler(IProject project, SymbolIndex symbolIndex, 
            IMarkerManager markerManager, List singleCompilers) {
        this.project = project;
        this.symbolIndex = symbolIndex;
        this.markerManager = markerManager;
        this.compilers = singleCompilers;
    }

    protected abstract void removeMarkers(IMarkerManager markerManager);
    protected abstract void flushIndexEntries(SymbolIndex symbolIndex);
    protected abstract List getFilesToCompile();
    protected abstract void analyzeFiles() throws CoreException;

    protected static List compilers(MarkerManager markerManager) {
        return ListUtil.create(new RubyCodeAnalyzer(markerManager), 
                new TaskCompiler(markerManager));
    }

    public void compile(IProgressMonitor monitor) throws CoreException {
        analyzeFiles();
        List list = getFilesToCompile();
        int fileCount = list.size();
        monitor.beginTask("Building "+project.getName() + "...", fileCount * (compilers.size() + 2));
        monitor.subTask("Removing Markers...");
        
        removeMarkers(markerManager);
        monitor.worked(fileCount);
        flushIndexEntries(symbolIndex);
        monitor.worked(fileCount);
    
        compileFiles(list, monitor);
        monitor.done();
    }

    private void compileFiles(List list, IProgressMonitor monitor) throws CoreException {
        for (Iterator iter = list.iterator(); iter.hasNext();) {
            IFile file = (IFile) iter.next();
            
            if (monitor.isCanceled())
                break;
            
            monitor.subTask(file.getFullPath().toString());
            compileFile(file, monitor);
        }
    }

    private void compileFile(IFile file, IProgressMonitor monitor) throws CoreException {
        for (Iterator cIter = compilers.iterator(); cIter.hasNext();) {
            SingleFileCompiler fileCompiler = (SingleFileCompiler) cIter.next();
            fileCompiler.compileFile(file);
            monitor.worked(1);
        }
    }

}
