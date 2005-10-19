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

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;

public class ShamResourceChangeEvent implements IResourceChangeEvent {

    private final int type;
    private final IResourceDelta delta;

    public ShamResourceChangeEvent(int type, IResourceDelta delta) {
        this.type = type;
        this.delta = delta;
    }

    public IMarkerDelta[] findMarkerDeltas(String type, boolean includeSubtypes) {
        return null;
    }

    public int getBuildKind() {
        return 0;
    }

    public IResourceDelta getDelta() {
        return delta;
    }

    public IResource getResource() {
        return null;
    }

    public Object getSource() {
        return null;
    }

    public int getType() {
        return type;
    }


}
