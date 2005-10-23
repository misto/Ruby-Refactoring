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

package org.rubypeople.rdt.internal.core.symbols;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.jruby.lexer.yacc.ISourcePosition;

public class SymbolIndex {

    private Map index = Collections.synchronizedMap(new HashMap());
    private static boolean verbose;
    
    public void add(Symbol symbol, Location location) {
        Set locations = (Set) index.get(symbol);
        if (locations == null) { 
            locations = new HashSet();
            index.put(symbol, locations);
        }
        locations.add(location);
    }

    public void add(Symbol symbol, IFile file, ISourcePosition position) {
    	SymbolIndex.log("Adding Symbol: " + symbol) ;
        add(symbol, new Location(file.getFullPath(), position));
    }

    public Set find(Symbol symbol) {
        Set locations = (Set) index.get(symbol);
        if (locations == null) 
            return Collections.EMPTY_SET;
        return Collections.unmodifiableSet(locations);
    }
    
    /*
     * returns a set of SearchResult instances as opposed to find(symbol), which returns locations
     */
    public Set find(String regExp, int symbolType) throws PatternSyntaxException {
		Pattern pattern = Pattern.compile(regExp);
		Set searchResults = new HashSet() ;
		
        for (Iterator indexIter = index.entrySet().iterator(); indexIter.hasNext();) {
            Map.Entry entry = (Map.Entry) indexIter.next();
            Symbol symbol = (Symbol) entry.getKey() ;
            if (symbol.getType() != symbolType) {
            	continue ;
            }
            if (pattern.matcher(symbol.getName()).find()) {
            	Set foundLocations = (Set)entry.getValue() ;
            	for (Iterator locationIter = foundLocations.iterator(); locationIter.hasNext(); ) {
            		Location location = (Location) locationIter.next() ;            		
            		searchResults.add(new SearchResult(symbol, location)) ;
            	}
            }            
        }
		
		return searchResults ;
    }

    public void flush(IPath foo_path) {
    	SymbolIndex.log("Flushing all Symbols with path: " + foo_path) ;

        synchronized (index) {
            for (Iterator indexIter = index.entrySet().iterator(); indexIter.hasNext();) {
                Map.Entry entry = (Map.Entry) indexIter.next();
                Set locations = (Set) entry.getValue();
                
                for (Iterator locationIter = locations.iterator(); locationIter.hasNext();) {
                    Location location = (Location) locationIter.next();
                    if (location.getSourcePath() == foo_path) {
                    	locationIter.remove();	
                    }                    
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
    
    public static void log(String message) {
    	if (!SymbolIndex.isVerbose()) {
    		return ;
    	}
    	System.out.println(message) ;
    }


}
