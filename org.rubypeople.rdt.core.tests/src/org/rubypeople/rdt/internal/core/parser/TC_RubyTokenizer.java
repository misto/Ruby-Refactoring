/*
 * Author: C.Williams
 *
 *  Copyright (c) 2004 RubyPeople. 
 *
 *  This file is part of the Ruby Development Tools (RDT) plugin for eclipse.
 *  You can get copy of the GPL along with further information about RubyPeople 
 *  and third party software bundled with RDT in the file 
 *  org.rubypeople.rdt.core_0.4.0/RDT.license or otherwise at 
 *  http://www.rubypeople.org/RDT.license.
 *
 *  RDT is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  RDT is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of 
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with RDT; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.rubypeople.rdt.internal.core.parser;

import java.util.NoSuchElementException;

import junit.framework.TestCase;

public class TC_RubyTokenizer extends TestCase {

	public void testDoesntCountPoundSymbolAndSubsequent() {
		RubyTokenizer tokenizer = new RubyTokenizer("some token # more tokens");
		assertEquals(2, tokenizer.countTokens());
	}
	
	public void testDoesCountSubsequentWhenPoundSymbolIsInString() {
		RubyTokenizer tokenizer = new RubyTokenizer("some token \"# more\" tokens");
		assertEquals(5, tokenizer.countTokens());
	}
	
	public void testDoesntReportMoreTokensForPoundSymbolAndSubsequent() {
		RubyTokenizer tokenizer = new RubyTokenizer("some token # more tokens");
		assertEquals("some", tokenizer.nextRubyToken().getText());
		assertEquals("token", tokenizer.nextRubyToken().getText());
		assertFalse(tokenizer.hasMoreTokens());
	}
	
	public void testDoesReportMoreTokensForSubsequentWhenPoundSymbolIsInString() {
		RubyTokenizer tokenizer = new RubyTokenizer("some token \"# more\" tokens");
		assertEquals("some", tokenizer.nextRubyToken().getText());
		assertEquals("token", tokenizer.nextRubyToken().getText());
		assertTrue(tokenizer.hasMoreTokens());
		assertEquals("#", tokenizer.nextRubyToken().getText());
		assertEquals("more", tokenizer.nextRubyToken().getText());
		assertEquals("tokens", tokenizer.nextRubyToken().getText());
		assertFalse(tokenizer.hasMoreTokens());
	}
	
	public void testDoesntReportMoreTokensForPoundSymbolAsFirstToken() {
		RubyTokenizer tokenizer = new RubyTokenizer(" # more tokens");
		assertFalse(tokenizer.hasMoreTokens());
		
		RubyTokenizer tokenizer2 = new RubyTokenizer("# more tokens");
		assertFalse(tokenizer2.hasMoreTokens());
	}
	
	public void testDoesntReturnMoreTokensForPoundSymbolAndSubsequent() {
		RubyTokenizer tokenizer = new RubyTokenizer("some token # more tokens");
		assertEquals("some", tokenizer.nextRubyToken().getText());
		assertEquals("token", tokenizer.nextRubyToken().getText());
		try {
			tokenizer.nextRubyToken();
			fail("Did not throw NoSuchElementException");
		}
		catch(NoSuchElementException e) {
			// ignore expected
		}
	}
	
	public void testDoesReturnMoreTokensForSubsequentWhenPoundSymbolIsInString() {
		RubyTokenizer tokenizer = new RubyTokenizer("some token \"# more\" tokens");
		assertEquals("some", tokenizer.nextRubyToken().getText());
		assertEquals("token", tokenizer.nextRubyToken().getText());
		assertEquals("#", tokenizer.nextRubyToken().getText());
		assertEquals("more", tokenizer.nextRubyToken().getText());
		assertEquals("tokens", tokenizer.nextRubyToken().getText());
	}
	
	public void testDoesntCountPercentStringSyntax() {
		RubyTokenizer tokenizer = new RubyTokenizer("%r| #{@@var} yeah |");
		assertEquals(2, tokenizer.countTokens());
	}
	
	public void testDoesntReturnPercentStringSyntax() {
		RubyTokenizer tokenizer = new RubyTokenizer("%r| #{@@var} yeah |");
		assertEquals("#{@@var}", tokenizer.nextRubyToken().getText());
		assertEquals("yeah", tokenizer.nextRubyToken().getText());
		assertFalse(tokenizer.hasMoreTokens());
	}
	
	public void testDoesntReturnMoreTokensForEndOfPercentString() {
		RubyTokenizer tokenizer = new RubyTokenizer("%r| #{@@var} yeah |");
		assertEquals("#{@@var}", tokenizer.nextRubyToken().getText());
		assertEquals("yeah", tokenizer.nextRubyToken().getText());
		try {
			tokenizer.nextRubyToken();
			fail("Did not throw NoSuchElementException");
		}
		catch(NoSuchElementException e) {
			// ignore expected
		}
	}
	
	public void testDoesntReturnMoreTokensForEndOfPercentStringWithMatchedBracket() {
		RubyTokenizer tokenizer = new RubyTokenizer("%r( #{@@var} yeah )");
		assertEquals("#{@@var}", tokenizer.nextRubyToken().getText());
		assertEquals("yeah", tokenizer.nextRubyToken().getText());
		try {
			tokenizer.nextRubyToken();
			fail("Did not throw NoSuchElementException");
		}
		catch(NoSuchElementException e) {
			// ignore expected
		}
	}
	
	public void testDoesntReturnPercentStringSyntaxButReturnsSubsequent() {
		RubyTokenizer tokenizer = new RubyTokenizer("%r| #{@@var} yeah | more tokens");
		assertEquals("#{@@var}", tokenizer.nextRubyToken().getText());
		assertEquals("yeah", tokenizer.nextRubyToken().getText());
		assertTrue(tokenizer.hasMoreTokens());
		assertEquals("more", tokenizer.nextRubyToken().getText());
		assertEquals("tokens", tokenizer.nextRubyToken().getText());
		assertFalse(tokenizer.hasMoreTokens());
	}
	
	public void testDoesntReturnPercentStringWithNoSpacesSyntax() {
		RubyTokenizer tokenizer = new RubyTokenizer("%r|#{@@var} yeah|");
		assertEquals("#{@@var}", tokenizer.nextRubyToken().getText());
		assertEquals("yeah", tokenizer.nextRubyToken().getText());
		assertFalse(tokenizer.hasMoreTokens());
	}
	
	public void testStringAsSingleToken() {
		RubyTokenizer tokenizer = new RubyTokenizer("require \"tk\"");
		assertEquals("require", tokenizer.nextRubyToken().getText());
		assertEquals("tk", tokenizer.nextRubyToken().getText());				
	}
	
	public void testNoSpaceBeforeVariableSubstitution() {
		RubyTokenizer tokenizer = new RubyTokenizer("%Q(blah#{@@var})");
		assertEquals("blah", tokenizer.nextRubyToken().getText());
		assertEquals("#{@@var}", tokenizer.nextRubyToken().getText());
	}
	
	// TODO Handle this case!
	public void testSpaceBetweenVariableSubstitution() {
		RubyTokenizer tokenizer = new RubyTokenizer("%Q(blah #{ @@var })");
		assertEquals("blah", tokenizer.nextRubyToken().getText());
		assertEquals("#{", tokenizer.nextRubyToken().getText());
		RubyToken token = tokenizer.nextRubyToken();
		assertEquals("@@var", token.getText());
		assertEquals(RubyToken.CLASS_VARIABLE, token.getType() );
		assertEquals("}", tokenizer.nextRubyToken().getText());
	}
	
	public void testWhile() {
		RubyTokenizer tokenizer = new RubyTokenizer("while sunshine\nwork()\nend");
		assertEquals(RubyToken.WHILE, tokenizer.nextRubyToken().getType() );
	}
	
	public void testWhileModifier() {
		RubyTokenizer tokenizer = new RubyTokenizer("sleep while idle");
		tokenizer.nextRubyToken();
		assertEquals(RubyToken.WHILE_MODIFIER, tokenizer.nextRubyToken().getType() );
	}
	
	public void testUntil() {
		RubyTokenizer tokenizer = new RubyTokenizer("until sunride\nsleep\nend");
		assertEquals(RubyToken.UNTIL, tokenizer.nextRubyToken().getType() );
	}
	
	public void testUntilModifier() {
		RubyTokenizer tokenizer = new RubyTokenizer("work until tired");
		tokenizer.nextRubyToken();
		assertEquals(RubyToken.UNTIL_MODIFIER, tokenizer.nextRubyToken().getType() );
	}
	
	public void testUnless() {
		RubyTokenizer tokenizer = new RubyTokenizer("unless sunride\nsleep\nend");
		assertEquals(RubyToken.UNLESS, tokenizer.nextRubyToken().getType() );
	}
	
	public void testUnlessModifier() {
		RubyTokenizer tokenizer = new RubyTokenizer("work unless tired");
		tokenizer.nextRubyToken();
		assertEquals(RubyToken.UNLESS_MODIFIER, tokenizer.nextRubyToken().getType() );
	}
	
	public void testIf() {
		RubyTokenizer tokenizer = new RubyTokenizer("if true\nsleep\nend");
		assertEquals(RubyToken.IF, tokenizer.nextRubyToken().getType() );
	}
	
	public void testIfModifier() {
		RubyTokenizer tokenizer = new RubyTokenizer("work if notTired");
		tokenizer.nextRubyToken();
		assertEquals(RubyToken.IF_MODIFIER, tokenizer.nextRubyToken().getType() );
	}
	
	public void testConstant() {
		RubyTokenizer tokenizer = new RubyTokenizer("Tired");
		assertEquals(RubyToken.CONSTANT, tokenizer.nextRubyToken().getType() );
	}
	
	public void testTokenWithPrecedingTab() {
		RubyTokenizer tokenizer = new RubyTokenizer("\tTired");
		assertEquals(RubyToken.CONSTANT, tokenizer.nextRubyToken().getType() );
	}
	
	public void testPercentAsIdentifier() {
		RubyTokenizer tokenizer = new RubyTokenizer("def %");
		tokenizer.nextRubyToken();
		assertEquals(RubyToken.IDENTIFIER, tokenizer.nextRubyToken().getType() );
	}
	
	public void testCommaAtEndOfVariableIsIgnored() {
		RubyTokenizer tokenizer = new RubyTokenizer("return [ @version, @status, @reason ]");
		assertEquals(6, tokenizer.countTokens() );
		assertEquals("return", tokenizer.nextRubyToken().getText());
		assertEquals("[", tokenizer.nextRubyToken().getText());
		assertEquals("@version", tokenizer.nextRubyToken().getText());
	}
	
//	public void testInPercentString() {
//		RubyTokenizer tokenizer = new RubyTokenizer("");
//		assertFalse(tokenizer.inPercentString('q', 1, "%q(blah) end"));
//		assertTrue(tokenizer.inPercentString('q', 4, "%q(blah) end"));
//		assertTrue(tokenizer.inPercentString('q', 8, "%q(bla\\)h) end"));
//		assertFalse(tokenizer.inPercentString('q', 9, "%q(blah) end"));
//		assertFalse(tokenizer.inPercentString('q', 8, "%q(blah) end"));
//	}
//
//	public void testIsOpenBracket() {
//		RubyTokenizer tokenizer = new RubyTokenizer("");
//		assertTrue(tokenizer.isOpenBracket('('));
//		assertTrue(tokenizer.isOpenBracket('{'));
//		assertTrue(tokenizer.isOpenBracket('['));
//		assertFalse(tokenizer.isOpenBracket('1'));
//	}
//
//	public void testGetMatchingBracket() {
//		RubyTokenizer tokenizer = new RubyTokenizer("");
//		assertEquals(']', tokenizer.getMatchingBracket('['));
//		assertEquals(')', tokenizer.getMatchingBracket('('));
//		assertEquals('}', tokenizer.getMatchingBracket('{'));
//		assertEquals('1', tokenizer.getMatchingBracket('1'));
//	}
	
}
