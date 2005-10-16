/*
?* Author: David Corbin
?*
?* Copyright (c) 2005 RubyPeople.
?*
?* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
?*/

package org.rubypeople.rdt.internal.core.symbols;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.jruby.lexer.yacc.ISourcePosition;

public class SymbolIndex {

    private Map index = Collections.synchronizedMap(new HashMap());
    private static boolean verbose;
    
    public void add(ClassSymbol symbol, Location location) {
        Set locations = (Set) index.get(symbol);
        if (locations == null) { 
            locations = new HashSet();
            index.put(symbol, locations);
        }
        locations.add(location);
    }

    public void add(ClassSymbol symbol, IFile file, ISourcePosition position) {
        add(symbol, new Location(file.getFullPath(), position));
    }

    public Set find(ClassSymbol symbol) {
        Set locations = (Set) index.get(symbol);
        if (locations == null) 
            return Collections.EMPTY_SET;
        return Collections.unmodifiableSet(locations);
    }

    public void flush(IPath foo_path) {
        // flush only relevant bits
        synchronized (index) {
            for (Iterator indexIter = index.entrySet().iterator(); indexIter.hasNext();) {
                Map.Entry entry = (Map.Entry) indexIter.next();
                Set locations = (Set) entry.getValue();
                
                for (Iterator locationIter = locations.iterator(); locationIter.hasNext();) {
                    Location location = (Location) locationIter.next();
                    locationIter.remove();
                }
                
                if (locations.isEmpty())
                    indexIter.remove();
            }
        }
    }

    public static void setVerbose(boolean verbose) {
        SymbolIndex.verbose = verbose;
    }
    
    public static boolean isVerbose() {
        return verbose;
    }


}
