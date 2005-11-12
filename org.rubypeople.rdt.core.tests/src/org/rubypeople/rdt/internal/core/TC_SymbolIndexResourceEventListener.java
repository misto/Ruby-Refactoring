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
package org.rubypeople.rdt.internal.core;

import junit.framework.TestCase;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.Path;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.eclipse.shams.resources.ShamResource;
import org.rubypeople.rdt.internal.core.util.ListUtil;

public class TC_SymbolIndexResourceEventListener extends TestCase {
    private ShamMassIndexUpdater massIndexUpdater;
    private SymbolIndexResourceChangeListener listener;
    private ShamProject project;
    private ShamResourceDelta parentDelta;
    private ShamResource resource;

    public void setUp() {
        massIndexUpdater = new ShamMassIndexUpdater();
        listener = new SymbolIndexResourceChangeListener(massIndexUpdater);
        project = new ShamProject("test1");
        resource = new ShamResource(new Path("test1/file1"));
        parentDelta = new ShamResourceDelta();
    }

    public void testSimpleProjectOpen() throws Exception {
        parentDelta.addChildren(createDelta(project));

        ShamResourceChangeEvent event = 
            new ShamResourceChangeEvent(IResourceChangeEvent.POST_CHANGE, parentDelta);
        
        listener.resourceChanged(event);
        
        massIndexUpdater.assertProjectsUpdated(ListUtil.create(project));
    }

    public void testMultipleProjectOpen() throws Exception {
        ShamProject project2 = new ShamProject("test2");
        parentDelta.addChildren(createDelta(project));
        parentDelta.addChildren(createDelta(project2));

        ShamResourceChangeEvent event = 
            new ShamResourceChangeEvent(IResourceChangeEvent.POST_CHANGE, parentDelta);
        
        listener.resourceChanged(event);
        
        massIndexUpdater.assertProjectsUpdated(ListUtil.create(project, project2));
    }
    
    public void testWithoutProject() throws Exception {
        parentDelta.addChildren(createDelta(resource));

        ShamResourceChangeEvent event = 
            new ShamResourceChangeEvent(IResourceChangeEvent.POST_CHANGE, parentDelta);
        
        listener.resourceChanged(event);
        
        massIndexUpdater.assertUpdateProjectsNotCalled();
    }

    public void testNotAPostChange() throws Exception {
        parentDelta.addChildren(createDelta(project));

        ShamResourceChangeEvent event = 
            new ShamResourceChangeEvent(IResourceChangeEvent.POST_BUILD, parentDelta);
        
        listener.resourceChanged(event);
        
        massIndexUpdater.assertUpdateProjectsNotCalled();
    }

    public void testNotADeltaChange() throws Exception {
        ShamResourceDelta projectDelta = createDelta(project);
        projectDelta.setKind(IResourceDelta.MOVED_FROM);
        parentDelta.addChildren(projectDelta);

        ShamResourceChangeEvent event = 
            new ShamResourceChangeEvent(IResourceChangeEvent.POST_CHANGE, parentDelta);
        
        listener.resourceChanged(event);
        
        massIndexUpdater.assertUpdateProjectsNotCalled();
    }

    public void testNotAOpen() throws Exception {
        ShamResourceDelta projectDelta = createDelta(project);
        projectDelta.setFlags(IResourceDelta.MARKERS);
        parentDelta.addChildren(projectDelta);

        ShamResourceChangeEvent event = 
            new ShamResourceChangeEvent(IResourceChangeEvent.POST_CHANGE, parentDelta);
        
        listener.resourceChanged(event);
        
        massIndexUpdater.assertUpdateProjectsNotCalled();
    }

    private ShamResourceDelta createDelta(IResource resource) {
        ShamResourceDelta delta = new ShamResourceDelta();
        delta.setResource(resource);
        delta.setKind(IResourceDelta.CHANGED);
        delta.setFlags(IResourceDelta.OPEN | IResourceDelta.MARKERS);
        return delta;
    }
}
