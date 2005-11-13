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
import org.eclipse.core.resources.IProject;
import org.jruby.lexer.yacc.ISourcePosition;

public class SymbolIndex {


    private Map index = Collections.synchronizedMap(new HashMap());
    private static boolean verbose;
    
    public void add(Symbol symbol, Location location) {
        if (verbose)
            log("Adding " + symbol + " at " + location);
        synchronized(index) {
            Set locations = (Set) index.get(symbol);
            if (locations == null) { 
                locations = new HashSet();
                index.put(symbol, locations);
            }
            locations.add(location);
        }
    }

    public void add(Symbol symbol, IFile file, ISourcePosition position) {
        add(symbol, new Location(file, position));
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

    public void flush(IFile fileToFlush) {
        if (verbose)
            log("Flushing all Symbols with path: " + fileToFlush.getFullPath()) ;

        flush(new PathEqualsPredicate(fileToFlush));
    }

    public static void setVerbose(boolean verbose) {
        SymbolIndex.verbose = verbose;
    }
    
    private static boolean isVerbose() {
        return verbose;
    }
    
    private static void log(String message) {
    	if (!SymbolIndex.isVerbose()) {
    		return ;
    	}
    	System.out.println(message) ;
    }

    public void flush(IProject project) {
        if (verbose)
            log("Flushing all Symbols for project: " + project.getName()) ;

        flush(new ContainedByProject(project));
        
    }

    private void flush(Predicate predicate) {
        synchronized (index) {
            for (Iterator indexIter = index.entrySet().iterator(); indexIter.hasNext();) {
                Map.Entry entry = (Map.Entry) indexIter.next();
                Set locations = (Set) entry.getValue();
                
                for (Iterator locationIter = locations.iterator(); locationIter.hasNext();) {
                    Location location = (Location) locationIter.next();
                    if (predicate.evaluate(location.getSourceFile())) {
                        locationIter.remove();  
                        
                        if (verbose)
                            log("Removing " + location);
                    }                    
                 }
                
                if (locations.isEmpty())
                    indexIter.remove();
            }
        }
    }


    private static class PathEqualsPredicate implements Predicate {
        private final IFile fileToFlush;

        public PathEqualsPredicate(IFile file) {
            this.fileToFlush = file;
        }

        public boolean evaluate(Object object) {
            return object.equals(fileToFlush);
        }
    }

    interface Predicate {
        boolean evaluate(Object object);
    }

    private static class ContainedByProject implements Predicate {

        private final IProject project;

        public ContainedByProject(IProject project) {
            this.project = project;
        }

        public boolean evaluate(Object object) {
            IFile file = (IFile) object;
            return file.getProject().equals(project);
        }
    }
}
