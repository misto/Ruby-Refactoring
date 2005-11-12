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

import junit.framework.TestCase;

import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.core.parser.RdtPosition;

public class TC_Location extends TestCase {
    public void testForSource() {
        ShamFile fooFile = new ShamFile("foo");
        Location location = new Location(fooFile, new RdtPosition(1, 2, 3));
        assertEquals(true, location.forSource(fooFile));
        assertEquals(false, location.forSource(new ShamFile("Foo")));
    }
}
