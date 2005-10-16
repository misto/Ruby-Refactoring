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

package org.rubypeople.rdt.core;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;

final class SymbolIndexResourceChangeListener implements IResourceChangeListener {
    private final SymbolIndex symbolIndex;

    static void register(SymbolIndex symbolIndex) {
        ResourcesPlugin.getWorkspace().addResourceChangeListener(new SymbolIndexResourceChangeListener(symbolIndex));
    }

    SymbolIndexResourceChangeListener(SymbolIndex symbolIndex) {
        this.symbolIndex = symbolIndex;
    }

    public void resourceChanged(IResourceChangeEvent event) {
    }

}