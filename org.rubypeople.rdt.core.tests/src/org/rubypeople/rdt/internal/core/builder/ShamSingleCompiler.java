/**
 * 
 */
package org.rubypeople.rdt.internal.core.builder;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;

class ShamSingleCompiler implements SingleFileCompiler {

    private Set compiledFiles = new HashSet();

    public void compileFile(IFile file) throws CoreException {
        compiledFiles.add(file);
    }

    public void assertCompiled(Set expectedFiles) {
        TC_CleanRdtCompiler.assertEquals(expectedFiles, compiledFiles);
    }

}