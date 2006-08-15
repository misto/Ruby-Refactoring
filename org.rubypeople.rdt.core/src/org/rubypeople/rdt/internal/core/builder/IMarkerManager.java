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

public interface IMarkerManager {
    public void removeProblemsAndTasksFor(IResource resource);
    public void createSyntaxError(IFile file, SyntaxException e);
    public void createError(IFile file, String message, int startLine, int startOffset, int endOffset);
    public void createTasks(IFile file, List tasks) throws CoreException;
    public void addWarning(IFile file, String message);
    public void addWarning(IFile file, String message, int startLine, int startOffset, int endOffset);

}