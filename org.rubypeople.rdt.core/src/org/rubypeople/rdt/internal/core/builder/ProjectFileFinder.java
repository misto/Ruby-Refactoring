/*
 * Author: David Corbin
 *
 * Copyright (c) 2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */


package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public final class ProjectFileFinder implements IFileProvider {
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
        project.accept(new RubySourceFileCollectingVisitor(sourceFiles), IResource.NONE);
    }

}