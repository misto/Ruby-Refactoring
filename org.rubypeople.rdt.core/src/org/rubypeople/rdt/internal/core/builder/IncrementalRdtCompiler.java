package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.core.util.Util;

public class IncrementalRdtCompiler extends AbstractRdtCompiler {

    private List filesToCompile;
    private List filesToClear;
    private final IResourceDelta rootDelta;

    public IncrementalRdtCompiler(IProject project, IResourceDelta delta, 
            IMarkerManager markerManager, List<SingleFileCompiler> singleCompilers) {
        super(project, markerManager, singleCompilers);
        this.rootDelta = delta;
    }

    public IncrementalRdtCompiler(IProject project, IResourceDelta delta) {
        this(project, delta, new MarkerManager()); 
    }

    private IncrementalRdtCompiler(IProject project, IResourceDelta delta, 
           MarkerManager manager) {
        this(project, delta, manager, singleFileCompilers(manager));
    }

    protected void removeMarkers(IMarkerManager markerManager) {
        for (Iterator iter = filesToClear.iterator(); iter.hasNext();) {
            IFile file  = (IFile) iter.next();
            markerManager.removeProblemsAndTasksFor(file);
        }
    }

    protected List getFilesToCompile() {
        return filesToCompile;
    }

    protected void analyzeFiles() throws CoreException {
        filesToClear = new ArrayList();
        filesToCompile = new ArrayList();
        
        rootDelta.accept(new IResourceDeltaVisitor() {
            public boolean visit(IResourceDelta delta) throws CoreException {
                IResource resource = delta.getResource();
                if (isRubyFile(resource)) {
                    if (delta.getKind() == IResourceDelta.REMOVED) {
                        filesToClear.add(resource);
                    } else if (delta.getKind() == IResourceDelta.ADDED
                            || delta.getKind() == IResourceDelta.CHANGED) {
                        filesToCompile.add(resource);
                    }
                }
                return true;
            }

            private boolean isRubyFile(IResource resource) {
                return resource instanceof IFile && Util.isRubyLikeFileName(resource.getName());
            }});
        filesToClear.addAll(filesToCompile);
    }

	@Override
	protected List getFilesToClear() {
		return filesToClear;
	}

}
