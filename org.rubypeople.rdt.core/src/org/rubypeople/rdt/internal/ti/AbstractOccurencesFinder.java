package org.rubypeople.rdt.internal.ti;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.rubypeople.rdt.core.IRubyElement;

public class AbstractOccurencesFinder implements IOccurrencesFinder {
	
	protected boolean fMarkOccurrenceAnnotations;
	protected boolean fStickyOccurrenceAnnotations;
	protected boolean fMarkTypeOccurrences;
	protected boolean fMarkMethodOccurrences;
	protected boolean fMarkConstantOccurrences;
	protected boolean fMarkFieldOccurrences;
	protected boolean fMarkLocalVariableOccurrences;
	protected boolean fMarkMethodExitPoints;

	public void collectOccurrenceMatches(IRubyElement element,
			IDocument document, Collection resultingMatches) {
		// TODO Auto-generated method stub
	}

	public String getElementName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getJobLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUnformattedPluralLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getUnformattedSingularLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	public String initialize(String source, int offset, int length) {
		// TODO Auto-generated method stub
		return null;
	}

	public List perform() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFMarkConstantOccurrences(boolean markConstantOccurrences) {
		fMarkConstantOccurrences = markConstantOccurrences;
	}

	public void setFMarkFieldOccurrences(boolean markFieldOccurrences) {
		fMarkFieldOccurrences = markFieldOccurrences;
	}

	public void setFMarkLocalVariableOccurrences(boolean markLocalVariableOccurrences) {
		fMarkLocalVariableOccurrences = markLocalVariableOccurrences;		
	}

	public void setFMarkMethodExitPoints(boolean markMethodExitPoints) {
		fMarkMethodExitPoints = markMethodExitPoints;		
	}

	public void setFMarkMethodOccurrences(boolean markMethodOccurrences) {
		fMarkMethodOccurrences = markMethodOccurrences;		
	}

	public void setFMarkOccurrenceAnnotations(boolean markOccurrenceAnnotations) {
		fMarkOccurrenceAnnotations = markOccurrenceAnnotations;
	}

	public void setFMarkTypeOccurrences(boolean markTypeOccurrences) {
		fMarkTypeOccurrences = markTypeOccurrences;		
	}

	public void setFStickyOccurrenceAnnotations(boolean stickyOccurrenceAnnotations) {
		fStickyOccurrenceAnnotations = stickyOccurrenceAnnotations;		
	}

}
