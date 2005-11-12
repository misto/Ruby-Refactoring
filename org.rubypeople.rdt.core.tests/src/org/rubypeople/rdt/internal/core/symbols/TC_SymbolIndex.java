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

import org.eclipse.core.resources.IProject;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;

public class TC_SymbolIndex extends TestCase implements ISymbolTypes {
    private static final ClassSymbol UNKNOWN_SYMBOL = new ClassSymbol("unknown");
    private static final ClassSymbol FOO_CLASS_SYMBOL = new ClassSymbol("Foo");
    private static final ClassSymbol OTHER_FOO_CLASS_SYMBOL = new ClassSymbol("Foo2");
    private static final IProject PROJECT1                      = new ShamProject("project1");
    private static final ShamFile FOO_FILE          = new ShamFile("/project1/foo.rb");
    private static final ShamFile FOO_FILE2         = new ShamFile("/project1/foo.rb");
    private static final ShamFile OTHER_FOO_FILE    = new ShamFile("/project1/utils/foo.rb");
    private static final ShamFile PROJECT2_FILE     = new ShamFile("/project2/bar.rb");
    
    private static final ISourcePosition FOO_POSITION           = new RdtPosition(10, 3, 7);
    private static final ISourcePosition OTHER_FOO_POSITION     = new RdtPosition(12, 13, 17);
    private static final Location FOO_CLASS_LOCATION            = new Location(FOO_FILE, FOO_POSITION);
    private static final Location OTHER_FOO_CLASS_LOCATION      = new Location(OTHER_FOO_FILE, OTHER_FOO_POSITION);
    private static final Location PROJECT2_CLASS_LOCATION       = new Location(PROJECT2_FILE, OTHER_FOO_POSITION);
    private static final Set EMPTY_SET                          = Collections.EMPTY_SET;
    
    static {
        FOO_FILE.setProject(PROJECT1);
    }
    
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
        index.flush(FOO_FILE);
        assertEquals(createSet(OTHER_FOO_CLASS_LOCATION), index.find(FOO_CLASS_SYMBOL));
        index.flush(OTHER_FOO_FILE);
        assertEquals(EMPTY_SET, index.find(FOO_CLASS_SYMBOL));
    }

    public void testFlushUsesEquals() {       
        index.add(FOO_CLASS_SYMBOL, OTHER_FOO_CLASS_LOCATION);
        index.flush(FOO_FILE2);
        assertEquals(createSet(OTHER_FOO_CLASS_LOCATION), index.find(FOO_CLASS_SYMBOL));
        index.flush(OTHER_FOO_FILE);
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
    
    public void testClassRegExpFind() {
    	index.add(OTHER_FOO_CLASS_SYMBOL, OTHER_FOO_CLASS_LOCATION);
    	assertEquals(EMPTY_SET, index.find("^oo$", CLASS_SYMBOL));
    	assertEquals(createSet(new SearchResult(FOO_CLASS_SYMBOL, FOO_CLASS_LOCATION)), index.find("[o]+$", CLASS_SYMBOL));
    	assertEquals(createSet(new SearchResult(OTHER_FOO_CLASS_SYMBOL, OTHER_FOO_CLASS_LOCATION)), index.find("o.2", CLASS_SYMBOL));
    	assertEquals(createSet(new SearchResult(FOO_CLASS_SYMBOL, FOO_CLASS_LOCATION), new SearchResult(OTHER_FOO_CLASS_SYMBOL, OTHER_FOO_CLASS_LOCATION)), index.find("oo", CLASS_SYMBOL));    	
    }
    
    public void testAddMethodWithSameNameAsClass() {
    	MethodSymbol symbol = new MethodSymbol("Foo") ;
    	index.add(symbol, OTHER_FOO_CLASS_LOCATION);
    	assertEquals(createSet(OTHER_FOO_CLASS_LOCATION), index.find(symbol)) ;
    	assertEquals(createSet(FOO_CLASS_LOCATION), index.find(FOO_CLASS_SYMBOL)) ;
    }
    
    public void testMethodRegExpFind() {
    	MethodSymbol symbol = new MethodSymbol("aMethod") ;
    	index.add(symbol, OTHER_FOO_CLASS_LOCATION);
    	assertEquals(createSet(new SearchResult(symbol, OTHER_FOO_CLASS_LOCATION)), index.find("^a", METHOD_SYMBOL)) ;
    	assertEquals(EMPTY_SET, index.find("^a", CLASS_SYMBOL)) ;
    }
    
    public void testFlushByProject() {
        index.add(FOO_CLASS_SYMBOL, FOO_CLASS_LOCATION);
        ClassSymbol barClassSymbol = new ClassSymbol("Bar");
        index.add(barClassSymbol, PROJECT2_CLASS_LOCATION);
        index.flush(PROJECT1);
        assertEquals(EMPTY_SET, index.find(FOO_CLASS_SYMBOL));
        assertEquals(createSet(PROJECT2_CLASS_LOCATION), index.find(barClassSymbol));
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
