package org.rubypeople.rdt.internal.core.symbols;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;

public class TC_SymbolIndex extends TestCase {
    private static final ClassSymbol UNKNOWN_SYMBOL = new ClassSymbol("unknown");
    private static final ClassSymbol FOO_CLASS_SYMBOL = new ClassSymbol("Foo");
    private static final Path FOO_PATH = new Path("/foo.rb");
    private static final Path OTHER_FOO_PATH = new Path("/utils/foo.rb");
    
    private static final Location FOO_CLASS_LOCATION = new Location(FOO_PATH, 12, 15);
    private static final Location OTHER_FOO_CLASS_LOCATION = new Location(OTHER_FOO_PATH, 10, 13);
    private static final Set EMPTY_SET= Collections.EMPTY_SET;
    
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
        index.flush(FOO_PATH);
        assertEquals(EMPTY_SET, index.find(FOO_CLASS_SYMBOL));
    }
    
    public void testMultipleLocations() {
        index.add(FOO_CLASS_SYMBOL, OTHER_FOO_CLASS_LOCATION);
        assertEquals(createSet(OTHER_FOO_CLASS_LOCATION, FOO_CLASS_LOCATION), index.find(FOO_CLASS_SYMBOL));
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
