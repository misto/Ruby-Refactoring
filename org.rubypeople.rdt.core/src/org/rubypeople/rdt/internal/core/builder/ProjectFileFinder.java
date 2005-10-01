/**
 * 
 */
package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;

final class ProjectFileFinder implements IFileFinder {
    private final IProject project;

    public ProjectFileFinder(IProject project) {
        this.project = project;
    }

    public List findFiles() throws CoreException {
        List files = new ArrayList();
        addAllSourceFiles(files);
        return files;
    }

    protected void addAllSourceFiles(final List sourceFiles) throws CoreException {
        project.accept(new IResourceProxyVisitor() {

            public boolean visit(IResourceProxy proxy) throws CoreException {
                IResource resource = null;
                switch (proxy.getType()) {
                case IResource.FILE:
                    if (org.rubypeople.rdt.internal.core.util.Util.isRubyLikeFileName(proxy.getName())) {
                        if (resource == null) resource = proxy.requestResource();
                        sourceFiles.add(resource);
                    }
                    return false;
                }
                return true;
            }
        }, IResource.NONE);
    }

}