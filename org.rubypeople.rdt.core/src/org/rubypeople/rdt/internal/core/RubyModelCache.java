/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.core.buffer.OverflowingLRUCache;

/**
 * The cache of java elements to their respective info.
 */
public class RubyModelCache {

    public static final int CACHE_RATIO = 20;

    /**
     * Active Ruby Model Info
     */
    protected RubyModelInfo modelInfo;

    /**
     * Cache of open projects.
     */
    protected HashMap projectCache;

    /**
     * Cache of open compilation unit and class files
     */
    protected OverflowingLRUCache openableCache;

    /**
     * Cache of open children of openable Ruby Model Ruby elements
     */
    protected Map childrenCache;

    public RubyModelCache() {
        this.projectCache = new HashMap(5); // average 25552 bytes per project.
        // bytes per pkg
        // -> maximum
        // size :
        // 178200*CACHE_RATIO
        // bytes
        this.openableCache = new ElementCache(CACHE_RATIO * 100); // average
        // 6629
        // bytes per
        // openable
        // (includes
        // children)
        // ->
        // maximum
        // size :
        // 662900*CACHE_RATIO
        // bytes
        this.childrenCache = new HashMap(CACHE_RATIO * 10 * 20); // average
        // 20
        // children
        // per
        // openable
    }

    /**
     * Returns the info for the element.
     */
    public Object getInfo(IRubyElement element) {
        switch (element.getElementType()) {
        case IRubyElement.RUBY_MODEL:
            return this.modelInfo;
        case IRubyElement.PROJECT:
            return this.projectCache.get(element);
        case IRubyElement.SCRIPT:
            return this.openableCache.get(element);
        default:
            return this.childrenCache.get(element);
        }
    }

    /**
     * Returns the info for this element without disturbing the cache ordering.
     */
    protected Object peekAtInfo(IRubyElement element) {
        switch (element.getElementType()) {
        case IRubyElement.RUBY_MODEL:
            return this.modelInfo;
        case IRubyElement.PROJECT:
            return this.projectCache.get(element);
        case IRubyElement.SCRIPT:
            return this.openableCache.peek(element);
        default:
            return this.childrenCache.get(element);
        }
    }

    /**
     * Remember the info for the element.
     */
    protected void putInfo(IRubyElement element, Object info) {
        switch (element.getElementType()) {
        case IRubyElement.RUBY_MODEL:
            this.modelInfo = (RubyModelInfo) info;
            break;
        case IRubyElement.PROJECT:
            this.projectCache.put(element, info);
            break;
        case IRubyElement.SCRIPT:
            this.openableCache.put(element, info);
            break;
        default:
            this.childrenCache.put(element, info);
        }
    }

    /**
     * Removes the info of the element from the cache.
     */
    protected void removeInfo(IRubyElement element) {
        switch (element.getElementType()) {
        case IRubyElement.RUBY_MODEL:
            this.modelInfo = null;
            break;
        case IRubyElement.PROJECT:
            this.projectCache.remove(element);
            break;
        case IRubyElement.SCRIPT:
            this.openableCache.remove(element);
            break;
        default:
            this.childrenCache.remove(element);
        }
    }

    public String toStringFillingRation(String prefix) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(prefix);
        buffer.append("Project cache: "); //$NON-NLS-1$
        buffer.append(this.projectCache.size());
        buffer.append(" projects\n"); //$NON-NLS-1$
        buffer.append(prefix);
        buffer.append("Openable cache: "); //$NON-NLS-1$
        buffer.append(NumberFormat.getInstance().format(this.openableCache.fillingRatio()));
        buffer.append("%\n"); //$NON-NLS-1$
        return buffer.toString();
    }
}
