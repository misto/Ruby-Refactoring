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

import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.content.IContentType;

public final class RubySourceFileCollectingVisitor implements IResourceProxyVisitor {

    private static final String RUBY_SOURCE_CONTENT_TYPE_ID = "org.rubypeople.rdt.core.rubySource";
    private final List files;

    public RubySourceFileCollectingVisitor(List files) {
        this.files = files;
    }

    public boolean visit(IResourceProxy proxy) throws CoreException {
        IResource resource = null;
        switch (proxy.getType()) {
        case IResource.FILE:
            if (org.rubypeople.rdt.internal.core.util.Util.isRubyLikeFileName(proxy.getName())) {
                if (resource == null) resource = proxy.requestResource();
                files.add(resource);
            }
            // Check for Ruby Source content type
            resource = proxy.requestResource();
            IFile file = (IFile) resource;
            IContentType type = file.getContentDescription().getContentType();
            if (type == null) return false;
            if (type.getId().equals(RUBY_SOURCE_CONTENT_TYPE_ID)) files.add(resource);
            return false;
        }
        return true;
    }

    public boolean equals(Object obj) {
        return obj.getClass().equals(getClass());
    }

    public int hashCode() {
        return 0;
    }
}