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
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.jruby.ast.Node;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class MassIndexUpdater {

    private final IndexUpdater updater;
    private final RubyParser parser;

    public MassIndexUpdater(IndexUpdater updater, RubyParser parser) {
        this.updater = updater;
        this.parser = parser;
    }

    public MassIndexUpdater(IndexUpdater indexUpdater) {
        this(indexUpdater, new RubyParser());
    }

    public void update(List projects) {
        try {
            List files = findFiles(projects);
            processFiles(files);
        } catch (CoreException e) {
            RubyCore.log(e);
        }
    }

    private void processFiles(List files) {
        for (Iterator iter = files.iterator(); iter.hasNext();) {
            IFile file = (IFile) iter.next();
            processFile(file);
        }
    }

    private void processFile(IFile file) {
        try {
            Node node = parser.parse(file);
            updater.update(file, node);
        } catch (CoreException e) {
            RubyCore.log(e);
        }
    }

    private List findFiles(List projects) throws CoreException {
        List files = new ArrayList();
        RubySourceFileCollectingVisitor visitor = new RubySourceFileCollectingVisitor(files);
        for (Iterator iter = projects.iterator(); iter.hasNext();) {
            IProject project = (IProject) iter.next();
            project.accept(visitor, 0);
        }
        return files;
    }
}
