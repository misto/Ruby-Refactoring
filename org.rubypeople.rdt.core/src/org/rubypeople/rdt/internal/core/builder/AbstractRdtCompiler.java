package org.rubypeople.rdt.internal.core.builder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.pmd.CPD;
import org.rubypeople.rdt.internal.core.pmd.Match;
import org.rubypeople.rdt.internal.core.pmd.PMD;
import org.rubypeople.rdt.internal.core.pmd.TokenEntry;
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
    protected abstract List<IFile> getFilesToCompile();
    protected abstract void analyzeFiles() throws CoreException;

    protected static List compilers(MarkerManager markerManager) {
        return ListUtil.create(new RubyCodeAnalyzer(markerManager), 
                new TaskCompiler(markerManager));
    }

    public void compile(IProgressMonitor monitor) throws CoreException {
        analyzeFiles();
        List<IFile> files = getFilesToCompile();
        int fileCount = files.size();
        monitor.beginTask("Building "+project.getName() + "...", fileCount * (compilers.size() + 3));
        monitor.subTask("Removing Markers...");
        
        removeMarkers(markerManager);
        monitor.worked(fileCount);
        monitor.subTask("Removing Search Indices...");
        flushIndexEntries(symbolIndex);
        monitor.worked(fileCount);

        // TODO Refactor out this stuff into a compiler, only visit files we've collected
        monitor.subTask("Finding duplicate code...");
        try {
			Iterator<Match> matches = CPD.findMatches(files);
			while (matches.hasNext()) {
				Match match = matches.next();
				addMarker(match);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		monitor.worked(fileCount);
		
        compileFiles(files, monitor);
        monitor.done();
    }
    
	private void addMarker(Match match) {
		StringBuffer message = new StringBuffer("Found a ");
		message.append(match.getLineCount()).append(" line (").append(match.getTokenCount()).append(" tokens) duplication");
        for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
        	TokenEntry mark = (TokenEntry) occurrences.next();
            // FIXME Make TokenEntry hold an IFile pointer to source file?
            IFile file = RubyCore.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(mark.getTokenSrcID()));
            markerManager.addWarning(file, message.toString(), mark.getBeginLine(), mark.getStartOffset(), mark.getStartOffset() + match.getSourceCodeSlice().length());
        }        
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
