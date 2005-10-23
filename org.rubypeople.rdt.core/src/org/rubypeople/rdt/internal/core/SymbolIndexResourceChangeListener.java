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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.rubypeople.rdt.internal.core.builder.IndexUpdater;
import org.rubypeople.rdt.internal.core.builder.MassIndexUpdater;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;

public final class SymbolIndexResourceChangeListener implements IResourceChangeListener {
    private final MassIndexUpdater updater;

    public static void register(SymbolIndex symbolIndex) { 
        IndexUpdater indexUpdater = new IndexUpdater(symbolIndex);
        MassIndexUpdater massIndexUpdater = new MassIndexUpdater(indexUpdater);
        SymbolIndexResourceChangeListener listener 
            = new SymbolIndexResourceChangeListener(massIndexUpdater);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(listener);
    }

    public SymbolIndexResourceChangeListener(MassIndexUpdater updater) {
        this.updater = updater;
    }

    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.POST_CHANGE) 
            handlePostChangeEvent(event);
    }

    private void handlePostChangeEvent(IResourceChangeEvent event) {
        List projects = new ArrayList();
        IResourceDelta[] deltas = event.getDelta().getAffectedChildren();
        for (int i = 0; i < deltas.length; i++) {
            IResourceDelta delta = deltas[i];
            if (isDeltaOpen(delta)) {
                IResource resource = delta.getResource();
                if (isProject(resource))
                    projects.add(resource.getAdapter(IProject.class));
            }
        }
        if (!projects.isEmpty())
            updater.updateProjects(projects);
    }

    private boolean isProject(IResource resource) {
        return ((IProject) resource.getAdapter(IProject.class)) != null;
    }

    private boolean isDeltaOpen(IResourceDelta delta) {
        return delta.getKind() == IResourceDelta.CHANGED &&
                (delta.getFlags() & IResourceDelta.OPEN) != 0;
    }

}