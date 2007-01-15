package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
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
    protected final List<SingleFileCompiler> singleFileCompilers;
    protected final List<MultipleFileCompiler> multiFileCompilers;
    
    public AbstractRdtCompiler(IProject project, SymbolIndex symbolIndex, 
            IMarkerManager markerManager, List<SingleFileCompiler> singleCompilers, List<MultipleFileCompiler> multiFileCompilers) {
        this.project = project;
        this.symbolIndex = symbolIndex;
        this.markerManager = markerManager;
        this.singleFileCompilers = singleCompilers;
        this.multiFileCompilers = multiFileCompilers;
    }
    
    public AbstractRdtCompiler(IProject project, SymbolIndex symbolIndex, 
            IMarkerManager markerManager, List<SingleFileCompiler> singleCompilers) {
        this(project, symbolIndex, markerManager, singleCompilers, new ArrayList<MultipleFileCompiler>());
    }

    protected abstract void removeMarkers(IMarkerManager markerManager);
    protected abstract void flushIndexEntries(SymbolIndex symbolIndex);
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
        int taskCount = (filesToClear * 2) +  (files.size() * (singleFileCompilers.size() + multiFileCompilers.size()));
        
        monitor.beginTask("Building "+project.getName() + "...", taskCount);
        monitor.subTask("Removing Markers...");
        
        removeMarkers(markerManager);
        monitor.worked(filesToClear);
        monitor.subTask("Removing Search Indices...");
        flushIndexEntries(symbolIndex);
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
    }

    private void compileFile(IFile file, IProgressMonitor monitor) throws CoreException {
        for (Iterator<SingleFileCompiler> cIter = singleFileCompilers.iterator(); cIter.hasNext();) {
            SingleFileCompiler fileCompiler = cIter.next();
            fileCompiler.compileFile(file);
            monitor.worked(1);
        }
    }
}
