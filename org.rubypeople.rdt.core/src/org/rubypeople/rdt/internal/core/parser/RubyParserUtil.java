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

/**
 * @author Chris
 * 
 * This class is meant to store as a function library for helping in parsing
 * ruby scripts. Primarily used by the RubyParser, other classes may now use
 * the helper functions for analyzing ruby text
 */
public class RubyParserUtil {

	/**
	 * @param index
	 * @param curLine
	 * @return
	 */
	public static boolean inPercentString(char type, int index, String curLine) {
		int end = endIndexOf(curLine, "%" + type);
		if (end == -1) return false;
		if (end > index) return false;
		char c = curLine.charAt(end);
		if (isOpenBracket(c)) c = getMatchingBracket(c);
		for (int i = end + 1; i < index; i++) {
			if (curLine.charAt(i) == c && curLine.charAt(i - 1) != '\\') { return false; }
		}
		return true;
	}

	/**
	 * Returns the index of the last char of <code>token</code> in <code>myLine</code>.
	 * Returns -1 if there are no occurences. (myLine.indexOf(token) +
	 * token.length())
	 * 
	 * @param myLine
	 * @param token
	 * @return
	 */
	public static int endIndexOf(String myLine, String token) {
		if (myLine.indexOf(token) == -1) return -1;
		return myLine.indexOf(token) + token.length();
	}

	/**
	 * Returns the matching bracket for a given character. If it is not a
	 * bracket, returns the newline character
	 * 
	 * @param c
	 * @return
	 */
	public static char getMatchingBracket(char c) {
		switch (c) {
		case '(':
			return ')';
		case '{':
			return '}';
		case '[':
			return ']';
		default:
			return '\n';
		}
	}

	/**
	 * @param c
	 * @return
	 */
	public static boolean isOpenBracket(char c) {
		return contains(new char[] { '(', '{', '['}, c);
	}

	/**
	 * Given a character array of characters and a character, the method
	 * determines if the given character is contained in the array
	 * 
	 * @param chars
	 * @param c
	 * @return a boolean to indicate if this array contains the character
	 */
	public static boolean contains(char[] chars, char c) {
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == c) return true;
		}
		return false;
	}

}
