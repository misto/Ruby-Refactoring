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
