package org.rubypeople.rdt.internal.core.builder;

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @deprecated Please use CompilationParticipant infrastructure
 * @author Chris Williams
 *
 */
public interface MultipleFileCompiler {
	public void compileFile(List<IFile> file, IProgressMonitor monitor) throws CoreException;
}
