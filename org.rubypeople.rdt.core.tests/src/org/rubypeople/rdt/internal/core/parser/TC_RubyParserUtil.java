/*
 * Created on Mar 28, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.internal.core.parser;

import junit.framework.TestCase;

/**
 * @author Chris
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class TC_RubyParserUtil extends TestCase {

	public void testInPercentString() {
		assertFalse(RubyParserUtil.inPercentString('q', 1, "%q(blah) end"));
		assertTrue(RubyParserUtil.inPercentString('q', 4, "%q(blah) end"));
		assertTrue(RubyParserUtil.inPercentString('q', 8, "%q(bla\\)h) end"));
		assertFalse(RubyParserUtil.inPercentString('q', 9, "%q(blah) end"));
		assertFalse(RubyParserUtil.inPercentString('q', 8, "%q(blah) end"));
	}

	public void testIsOpenBracket() {
		assertTrue(RubyParserUtil.isOpenBracket('('));
		assertTrue(RubyParserUtil.isOpenBracket('{'));
		assertTrue(RubyParserUtil.isOpenBracket('['));
		assertFalse(RubyParserUtil.isOpenBracket('1'));
	}

	public void testGetMatchingBracket() {
		assertEquals(']', RubyParserUtil.getMatchingBracket('['));
		assertEquals(')', RubyParserUtil.getMatchingBracket('('));
		assertEquals('}', RubyParserUtil.getMatchingBracket('{'));
		assertEquals('\n', RubyParserUtil.getMatchingBracket('1'));
	}
}
