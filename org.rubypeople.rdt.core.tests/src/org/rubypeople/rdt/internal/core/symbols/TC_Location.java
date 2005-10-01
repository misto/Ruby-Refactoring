package org.rubypeople.rdt.internal.core.symbols;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;

public class TC_Location extends TestCase {
    public void testForSource() {
        Location location = new Location(new Path("foo"), 1, 2);
        assertEquals(true, location.forSource(new Path("foo")));
        assertEquals(false, location.forSource(new Path("Foo")));
    }
}
