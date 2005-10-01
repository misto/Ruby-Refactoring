/**
 * 
 */
package org.rubypeople.rdt.internal.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

interface SingleFileCompiler {
    public void compileFile(IFile file) throws CoreException;
}