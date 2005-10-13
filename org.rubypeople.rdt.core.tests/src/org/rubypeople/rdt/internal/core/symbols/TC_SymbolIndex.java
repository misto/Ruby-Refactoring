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
        index.flush(FOO_PATH);
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
