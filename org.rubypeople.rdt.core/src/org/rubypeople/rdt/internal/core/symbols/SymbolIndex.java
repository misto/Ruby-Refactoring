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

    private Map index = new HashMap();
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
