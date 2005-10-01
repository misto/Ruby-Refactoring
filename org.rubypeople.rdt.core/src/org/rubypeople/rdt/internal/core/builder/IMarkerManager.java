package org.rubypeople.rdt.internal.core.builder;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jruby.lexer.yacc.SyntaxException;

public interface IMarkerManager {
    public void removeProblemsAndTasksFor(IResource resource);
    public void createSyntaxError(IFile file, SyntaxException e);
    public void createTasks(IFile file, List tasks) throws CoreException;
    public void addWarning(IFile file, String message);
    public void addWarning(IFile file, String message, int startLine, int startOffset, int endOffset);

}