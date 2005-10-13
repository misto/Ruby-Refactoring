/*
?* Author: David Corbin
?*
?* Copyright (c) 2005 RubyPeople.
?*
?* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
?*/

package org.rubypeople.rdt.internal.core.builder;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.core.IRubyModelMarker;
import org.rubypeople.rdt.internal.core.parser.MarkerUtility;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;
import org.rubypeople.rdt.internal.core.parser.Warning;

class MarkerManager implements IMarkerManager {

    public void removeProblemsAndTasksFor(IResource resource) {
    	try {
    		if (resource != null && resource.exists()) {
    			resource.deleteMarkers(IRubyModelMarker.RUBY_MODEL_PROBLEM_MARKER, false, IResource.DEPTH_INFINITE);
    			resource.deleteMarkers(IRubyModelMarker.TASK_MARKER, false, IResource.DEPTH_INFINITE);
    		}
    	} catch (CoreException e) {
    		// assume there were no problems
    	}
    }

    public void createSyntaxError(IFile file, SyntaxException e) {
        MarkerUtility.createSyntaxError(file, e);
    }

    public void createTasks(IFile file, List tasks) throws CoreException {
        MarkerUtility.createTasks(file, tasks);
    }

    public void addWarning(IFile file, String message) {
        addWarning(file,message, 1, 0, 0);
    }

    public void addWarning(IFile file, String message, int startLine, int startOffset, int endOffset) {
        MarkerUtility.createProblemMarker(file, new Warning(new RdtPosition(startLine, startOffset, endOffset), message));
    }

}
