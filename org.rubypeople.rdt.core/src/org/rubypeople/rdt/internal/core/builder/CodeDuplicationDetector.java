package org.rubypeople.rdt.internal.core.builder;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;
import org.rubypeople.rdt.internal.core.parser.Warning;
import org.rubypeople.rdt.internal.core.pmd.CPD;
import org.rubypeople.rdt.internal.core.pmd.Match;
import org.rubypeople.rdt.internal.core.pmd.TokenEntry;

// XXX Either use this in some way, or remove it!
public class CodeDuplicationDetector implements MultipleFileCompiler {

	private IMarkerManager markerManager;
	
	public CodeDuplicationDetector(IMarkerManager manager) {
		this.markerManager = manager;
	}
	
	public void compileFile(List<IFile> files, IProgressMonitor monitor) throws CoreException {
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
		monitor.worked(files.size());
	}

	private void addMarker(Match match) {
		StringBuffer message = new StringBuffer("Found a ");
		message.append(match.getLineCount()).append(" line (").append(match.getTokenCount()).append(" tokens) duplication");
        for (Iterator occurrences = match.iterator(); occurrences.hasNext();) {
        	TokenEntry mark = (TokenEntry) occurrences.next();
            // FIXME Make TokenEntry hold an IFile pointer to source file?
            IFile file = RubyCore.getWorkspace().getRoot().getFileForLocation(Path.fromOSString(mark.getTokenSrcID()));
            Warning warning = new Warning(new RdtPosition(mark.getBeginLine(), mark.getStartOffset(), mark.getStartOffset() + match.getSourceCodeSlice().length()), message.toString());
            markerManager.addProblem(file, warning);
        }        
	}
	
}
