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
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;

public class TC_SymbolIndex extends TestCase {
    private static final ClassSymbol UNKNOWN_SYMBOL = new ClassSymbol("unknown");
    private static final ClassSymbol FOO_CLASS_SYMBOL = new ClassSymbol("Foo");
    private static final ClassSymbol OTHER_FOO_CLASS_SYMBOL = new ClassSymbol("Foo2");
    private static final Path FOO_PATH = new Path("/foo.rb");
    private static final Path OTHER_FOO_PATH = new Path("/utils/foo.rb");
    
    private static final ISourcePosition FOO_POSITION           = new RdtPosition(10, 3, 7);
    private static final ISourcePosition OTHER_FOO_POSITION     = new RdtPosition(12, 13, 17);
    private static final Location FOO_CLASS_LOCATION            = new Location(FOO_PATH, FOO_POSITION);
    private static final Location OTHER_FOO_CLASS_LOCATION      = new Location(OTHER_FOO_PATH, OTHER_FOO_POSITION);
    private static final IFile OTHER_FOO_FILE                   = new ShamFile(OTHER_FOO_PATH);
    private static final Set EMPTY_SET                          = Collections.EMPTY_SET;
    
    private SymbolIndex index;

    public void setUp() {
        index = new SymbolIndex();
        index.add(FOO_CLASS_SYMBOL, FOO_CLASS_LOCATION);
    }

    public void testSimple() {
        assertEquals(createSet(FOO_CLASS_LOCATION), index.find(FOO_CLASS_SYMBOL));
    }
    
    public void testNotFound() {
        assertEquals(EMPTY_SET, index.find(UNKNOWN_SYMBOL));
    } 
    
    public void testFlush() {    	
    	index.add(FOO_CLASS_SYMBOL, OTHER_FOO_CLASS_LOCATION);
        index.flush(FOO_PATH);
        assertEquals(createSet(OTHER_FOO_CLASS_LOCATION), index.find(FOO_CLASS_SYMBOL));
        index.flush(OTHER_FOO_PATH);
        assertEquals(EMPTY_SET, index.find(FOO_CLASS_SYMBOL));
    }
    
    public void testMultipleLocations() {
        index.add(FOO_CLASS_SYMBOL, OTHER_FOO_FILE, OTHER_FOO_POSITION);
        Set expected = createSet(OTHER_FOO_CLASS_LOCATION, FOO_CLASS_LOCATION);
        Set actual = index.find(FOO_CLASS_SYMBOL);
        assertEquals(expected, actual);
    }

    public void testAlternateAdd() {
        index.add(FOO_CLASS_SYMBOL, OTHER_FOO_CLASS_LOCATION);
        assertEquals(createSet(OTHER_FOO_CLASS_LOCATION, FOO_CLASS_LOCATION), index.find(FOO_CLASS_SYMBOL));
    }
    
    public void testAddingTwice() {
    	index.add(FOO_CLASS_SYMBOL, FOO_CLASS_LOCATION);
    	assertEquals(createSet(FOO_CLASS_LOCATION), index.find(FOO_CLASS_SYMBOL));
    }
    
    public void testRegExpFind() {
    	index.add(OTHER_FOO_CLASS_SYMBOL, OTHER_FOO_CLASS_LOCATION);
    	assertEquals(EMPTY_SET, index.find("^oo$"));
    	assertEquals(createSet(new SearchResult(FOO_CLASS_SYMBOL, FOO_CLASS_LOCATION)), index.find("[o]+$"));
    	assertEquals(createSet(new SearchResult(OTHER_FOO_CLASS_SYMBOL, OTHER_FOO_CLASS_LOCATION)), index.find("o.2"));
    	assertEquals(createSet(new SearchResult(FOO_CLASS_SYMBOL, FOO_CLASS_LOCATION), new SearchResult(OTHER_FOO_CLASS_SYMBOL, OTHER_FOO_CLASS_LOCATION)), index.find("oo"));
    	
    }
    
    private Set createSet(Object obj1) {
        Set set = new HashSet();
        set.add(obj1);
        return set;
    }
    
    private Set createSet(Object obj1, Object obj2) {
        Set set = createSet(obj1);
        set.add(obj2);
        return set;
    } 
}
