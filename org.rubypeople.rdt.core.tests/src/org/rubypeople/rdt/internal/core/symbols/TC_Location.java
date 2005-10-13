package org.rubypeople.rdt.internal.core.symbols;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;

public class TC_Location extends TestCase {
    public void testForSource() {
        Location location = new Location(new Path("foo"), new RdtPosition(1, 2, 3));
        assertEquals(true, location.forSource(new Path("foo")));
        assertEquals(false, location.forSource(new Path("Foo")));
    }
}
