/**
 * 
 */
package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.rubypeople.rdt.core.RubyCore;

class RubyCompiler {
    private int percentPerUnit = 0;
    private List compilers = new ArrayList();
    private final MarkerManager rubyMarkerManager;
    
    public RubyCompiler(int percentPerUnit) {
        this.percentPerUnit = percentPerUnit;
        rubyMarkerManager = new MarkerManager();
        this.compilers = createSingleFileCompilers(rubyMarkerManager);
    }

    private static List createSingleFileCompilers(MarkerManager rubyMarkerManager) {
        List compilers = new ArrayList();
        compilers.add(new RdtCompiler(rubyMarkerManager));
        compilers.add(new TaskCompiler(rubyMarkerManager));
        return compilers;
    }

    public void compile(List units, IProgressMonitor monitor){
        int unitsLength = units.size();
        if (unitsLength == 0) {
            monitor.worked(unitsLength * percentPerUnit);
            return;
        }
        
        // do them all now
        // TODO figure out what to do with exceptions - add an error for the file?
        for (int i = 0; i < unitsLength; i++) {
            try {
                IFile file = (IFile) units.get(i);
                monitoredCompile(monitor, i, unitsLength, file);
            } catch (CoreException e) {
                RubyCore.log(e);
            }
        }
    }

    private void monitoredCompile(IProgressMonitor monitor, int fileNumber, int grandTotal, IFile file) throws CoreException {
        checkCancel(monitor);
        RubyCore.trace("About to compile " + file); //$NON-NLS-1$
        String name = file.getFullPath().makeRelative().toString();
        monitor.subTask(name + ": (" + fileNumber + " of " + grandTotal + ")");              
        rubyMarkerManager.removeProblemsAndTasksFor(file);
        runCompilers(file);
        monitor.worked(percentPerUnit);
    }

    private void runCompilers(IFile file) throws CoreException {
        for (Iterator iter = compilers.iterator(); iter.hasNext();) {
            SingleFileCompiler compiler = (SingleFileCompiler) iter.next();
            compiler.compileFile(file);
        }
    }


    private void checkCancel(IProgressMonitor monitor) {
        if (monitor != null && monitor.isCanceled()) 
            throw new OperationCanceledException();
    }
}