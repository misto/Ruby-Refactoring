package org.rubypeople.rdt.internal.core.symbols;

import junit.framework.TestCase;

public class TC_ClassSymbol extends TestCase {

    private ClassSymbol fooClass;
    private ClassSymbol fooClass2;
    private ClassSymbol barClass;

    public void setUp() {
        fooClass = new ClassSymbol("Foo");
        fooClass2 = new ClassSymbol("Foo");
        barClass = new ClassSymbol("Bar");
    }

    public void testEquals() {
        assertEquals(true,  fooClass.equals(fooClass2));
        assertEquals(false, fooClass.equals(barClass));
        assertEquals(false, barClass.equals(fooClass));
        assertEquals(false, barClass.equals("Bar"));
    }
    
    public void testHashCode() {
        assertEquals(fooClass.hashCode(), fooClass2.hashCode());
    }
    
}
