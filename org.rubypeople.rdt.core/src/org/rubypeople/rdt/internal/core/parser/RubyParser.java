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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RubyParser {

	private static RubyParserStack stack = new RubyParserStack();
	private static RubyScript script;
	private static final char[] VARIABLE_END_CHARS = { ' ', '.', '[', '(', ')', ']', ',', '}', '{'};
	private static boolean inDocs;

	private static final String START_OF_STATEMENT_REGEX = "^(;\\s+)?\\s*";
	private static final Pattern WHILE_PATTERN = Pattern.compile(START_OF_STATEMENT_REGEX + "while ");
	private static final Pattern UNLESS_PATTERN = Pattern.compile(START_OF_STATEMENT_REGEX + "unless ");
	private static final Pattern UNTIL_PATTERN = Pattern.compile(START_OF_STATEMENT_REGEX + "until ");
	private static final Pattern CASE_PATTERN = Pattern.compile(START_OF_STATEMENT_REGEX + "case ");
	private static final Pattern END_PATTERN = Pattern.compile("^\\s*(.+\\s+)?end(\\s+.+)?$");
	private static final Pattern DO_PATTERN = Pattern.compile(".+\\s+do\\s*$");

	/**
	 * @return
	 */
	public static RubyScript parse(String string) throws ParseException {
		script = new RubyScript();
		stack.push(script);
		BufferedReader reader = new BufferedReader(new StringReader(string));
		String curLine = null;
		int lineNum = 0;
		int offset = 0;
		try {
			while ((curLine = reader.readLine()) != null) {
				String myLine = curLine;
				if (inDocs) {
					myLine = findMultiLineDocEnd(curLine);
					if (inDocs) {
						offset = 0;
						lineNum++;
						continue;
					}
				}
				myLine = removeAfterPoundSymbol(myLine);
				findMultiLineDocBeginning(myLine);
				if (inDocs) {
					offset = 0;
					lineNum++;
					continue;
				}
				findBegin(myLine, lineNum);
				findIf(myLine, lineNum);
				findCase(myLine, lineNum);
				findUnless(myLine, lineNum);
				findUntil(myLine, lineNum);
				findWhile(myLine, lineNum);
				findFor(myLine, lineNum);
				findDo(myLine, lineNum);
				findRequires(myLine, lineNum);
				findModule(myLine, lineNum);
				findClass(myLine, lineNum);
				findMethod(myLine, lineNum);
				findClassVariable(myLine, lineNum);
				findInstanceVariable(myLine, lineNum);
				findGlobal(myLine, lineNum);
				findPrivateModifier(myLine, lineNum);
				findAttributeReaderModifier(myLine, lineNum);
				findAttributeWriterModifier(myLine, lineNum);
				findAttributeAccessor(myLine, lineNum);
				findEnd(myLine, lineNum);
				offset = 0;
				lineNum++;
			}
		} catch (IOException e) {
			throw new ParseException(e);
		}
		script.setEnd(new Position(lineNum, offset));

		stack.clear();
		return script;
	}

	/**
	 * @param myLine
	 * @param lineNum
	 */
	private static void findDo(String myLine, int lineNum) {
		Matcher doMatcher = DO_PATTERN.matcher(myLine);
		if (doMatcher.find()) {
			int start = myLine.indexOf("do");
			RubyDo rubyDo = new RubyDo(new Position(lineNum, start));
			addBlock(rubyDo);
		}
	}

	/**
	 * @param rubyBlock
	 */
	private static void addBlock(RubyBlock rubyBlock) {
		try {
			RubyElement element = stack.peek();
			log("Adding " + rubyBlock + " to open element: " + element);
			element.addElement(rubyBlock);
			stack.push(rubyBlock);
		} catch (StackEmptyException e) {
			log("Tried to add a block to the element of on top of stack, but stack is empty");
		}
	}

	/**
	 * @param curLine
	 */
	private static String findMultiLineDocEnd(String curLine) {
		final String token = "=end";
		if (curLine.indexOf(token) == -1) return curLine;
		inDocs = false;
		return curLine.substring(curLine.indexOf(token) + token.length());
	}

	/**
	 * @param myLine
	 */
	private static void findMultiLineDocBeginning(String myLine) {
		if (myLine.indexOf("=begin") != -1) {
			inDocs = true;
		}
	}

	/**
	 * @param myLine
	 */
	private static void findAttributeAccessor(String myLine, int lineNum) {
		findAccessModifier(myLine, RubyElement.PUBLIC, "attr_accessor ", "@", lineNum);
	}

	/**
	 * @param myLine
	 */
	private static void findAttributeWriterModifier(String myLine, int lineNum) {
		findAccessModifier(myLine, RubyElement.WRITE, "attr_writer ", "@", lineNum);
	}

	/**
	 * @param myLine
	 */
	private static void findAttributeReaderModifier(String myLine, int lineNum) {
		findAccessModifier(myLine, RubyElement.READ, "attr_reader ", "@", lineNum);
	}

	/**
	 * @param myLine
	 */
	private static void findAccessModifier(String myLine, String accessRightsToGrant, String accessModifierTag, String symbolPrefix, int lineNum) {
		if (myLine.indexOf(accessModifierTag) == -1) return;

		List tokens = getSymbols(myLine, accessModifierTag);
		RubyElement parent = stack.findParentClassOrModule();
		if (parent == null) return;

		for (Iterator iter = tokens.iterator(); iter.hasNext();) {
			String elementName = (String) iter.next();
			RubyElement element = parent.getElement(symbolPrefix + elementName);
			if (element != null) {
				element.setAccess(accessRightsToGrant);
				continue;
			}
			String error = "Attempting to set access rights " + accessRightsToGrant + " to an unknown element " + elementName;
			log(error);
			script.addParseError(new ParseError(error, lineNum, myLine.indexOf(elementName), elementName.length()));
		}
	}

	/**
	 * @param myLine
	 */
	private static void findPrivateModifier(String myLine, int lineNum) {
		findAccessModifier(myLine, RubyElement.PRIVATE, "private ", "", lineNum);
	}

	/**
	 * Returns a list of symbol names given string of the format: "prefix
	 * :symbol, :symbolTwo"
	 * 
	 * @param myLine
	 * @return
	 */
	private static List getSymbols(String myLine, String prefix) {
		String copy = myLine.substring(myLine.indexOf(prefix) + prefix.length());
		StringTokenizer tokenizer = new StringTokenizer(copy, ", ");
		List list = new ArrayList();
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			list.add(token.substring(1));
		}
		return list;
	}

	/**
	 * @param lineNum
	 * @param myLine
	 */
	private static void findBegin(String myLine, int lineNum) {
		int beginIndex = myLine.indexOf("begin");
		if (beginIndex == -1) return;
		RubyBegin begin = new RubyBegin(new Position(lineNum, beginIndex));
		addBlock(begin);
	}

	/**
	 * @param lineNum
	 * @param myLine
	 */
	private static void findFor(String myLine, int lineNum) {
		int forIndex = myLine.indexOf("for ");
		if ((forIndex != -1) && (!inQuotes(forIndex, myLine))) {
			RubyFor rubyFor = new RubyFor(new Position(lineNum, forIndex));
			addBlock(rubyFor);
		}
	}

	/**
	 * @param lineNum
	 * @param myLine
	 */
	private static void findWhile(String myLine, int lineNum) {
		Matcher whileMatcher = WHILE_PATTERN.matcher(myLine);
		if (whileMatcher.find()) {
			int start = whileMatcher.end() - 6;
			RubyWhile rubyWhile = new RubyWhile(new Position(lineNum, start));
			try {
				RubyElement element = stack.peek();
				if (!(element instanceof RubyCase)) {
					element.addElement(rubyWhile);
					stack.push(rubyWhile);
				}
			} catch (StackEmptyException e) {
				log("Tried to add a block to the element of on top of stack, but stack is empty");
			}
		}
	}

	/**
	 * @param curLine
	 * @return
	 */
	private static String removeAfterPoundSymbol(String curLine) {
		int poundStart = curLine.indexOf("#");
		if ((poundStart != -1) && (!inQuotes(poundStart, curLine))) { return curLine.substring(0, poundStart); }
		return curLine;
	}

	/**
	 * @param poundStart
	 * @return
	 */
	private static boolean inQuotes(int poundStart, String curLine) {
		List openQuotes = new ArrayList();
		for (int index = 0; index < poundStart; index++) {
			char c = curLine.charAt(index);
			if (isQuoteChar(c)) {
				Character newChar = new Character(c);
				if (!openQuotes.isEmpty()) {
					Character open = (Character) openQuotes.get(openQuotes.size() - 1);
					if (newChar.equals(open)) {
						openQuotes.remove(openQuotes.size() - 1);
						continue;
					}
				}
				openQuotes.add(newChar);
			}
		}
		return !openQuotes.isEmpty();
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findUntil(String curLine, int lineNum) {
		Matcher untilMatcher = UNTIL_PATTERN.matcher(curLine);
		if (untilMatcher.find()) {
			int start = untilMatcher.end() - 6;
			RubyUntil until = new RubyUntil(new Position(lineNum, start));
			addBlock(until);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findCase(String curLine, int lineNum) {
		Matcher caseMatcher = CASE_PATTERN.matcher(curLine);
		if (caseMatcher.find()) {
			int start = caseMatcher.end() - 5;
			RubyCase rubyCase = new RubyCase(new Position(lineNum, start));
			addBlock(rubyCase);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findUnless(String curLine, int lineNum) {
		Matcher unlessMatcher = UNLESS_PATTERN.matcher(curLine);
		if (unlessMatcher.find()) {
			int unlessIndex = unlessMatcher.end() - 7;
			RubyUnless unless = new RubyUnless(new Position(lineNum, unlessIndex));
			addBlock(unless);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findIf(String curLine, int lineNum) {
		Matcher ifMatcher = Pattern.compile("^(;\\s+)?\\s*if ").matcher(curLine);
		if (ifMatcher.find()) {
			int ifIndex = ifMatcher.end() - 3;
			RubyIf rubyIf = new RubyIf(new Position(lineNum, ifIndex));
			addBlock(rubyIf);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findGlobal(String curLine, int lineNum) {
		int globalIndex = curLine.indexOf("$");
		if (globalIndex == -1) return;
		String name = getToken("$", VARIABLE_END_CHARS, curLine);
		RubyGlobal global = new RubyGlobal(name, new Position(lineNum, globalIndex + 1));
		script.addElement(global);
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findClassVariable(String curLine, int lineNum) {
		int instanceIndex = curLine.indexOf("@@");
		if (instanceIndex == -1) return;
		String name = getToken("@@", VARIABLE_END_CHARS, curLine);
		RubyClassVariable variable = new RubyClassVariable(name, new Position(lineNum, instanceIndex + "@@".length()));
		try {
			RubyElement element = stack.peek();
			element.addElement(variable);
		} catch (StackEmptyException e) {
			log("Tried to add a class variable to the element on top of stack, but stack is empty");
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findInstanceVariable(String curLine, int lineNum) {
		int instanceIndex = curLine.indexOf("@");
		if (instanceIndex == -1) return;
		if (curLine.indexOf("@@") != -1) return;
		String name = getToken("@", VARIABLE_END_CHARS, curLine);
		log("Found instance variable: " + name);
		RubyInstanceVariable variable = new RubyInstanceVariable(name, new Position(lineNum, instanceIndex + "@".length()));
		RubyElement element = stack.findParentClassOrModule();
		if (element != null) {
			element.addElement(variable);
			log("Added instance variable to open element: " + element);
		} else {
			script.addElement(variable);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findEnd(String curLine, int lineNum) {
		Matcher match = END_PATTERN.matcher(curLine);
		if (match.find()) {
			log("Found end: " + curLine);
			try {
				stack.closeLastOpenElement(lineNum, curLine.indexOf("end"));
			} catch (StackEmptyException e) {
				log(e.getMessage());
			}
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findRequires(String curLine, int lineNum) {
		final String token = "require ";
		if (curLine.indexOf(token) == -1) return;

		int start = curLine.indexOf(token) + token.length();
		String leftOver = curLine.substring(start);
		for (int i = 0; i < leftOver.length(); i++) {
			char c = leftOver.charAt(i);
			if (!isQuoteChar(c)) continue;
			String name = getToken(token + c, new char[] { c}, curLine);
			RubyRequires requires = new RubyRequires(name, new Position(lineNum, start + 1));
			if (!script.contains(requires)) {
				script.addRequires(requires);
			} else {
				script.addParseError(new ParseError("Duplicate require statement unnecessary.", lineNum, requires.getStart().getOffset(), requires.getEnd().getOffset()));
			}
			return;
		}
	}

	/**
	 * @param c
	 * @return
	 */
	private static boolean isQuoteChar(char c) {
		return (c == '\'') || (c == '"');
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findClass(String curLine, int lineNum) {
		char[] tokens = { ' ', ';'};
		int location = findElement("class ", tokens, curLine);
		if (location == -1) return;
		String name = getToken("class ", tokens, curLine);
		if (Character.isLowerCase(name.charAt(0))) script.addParseError(new ParseError("Class names should begin with an uppercase letter.", lineNum, location, location + name.length()));
		RubyClass rubyClass = new RubyClass(name, new Position(lineNum, location));
		try {
			RubyElement element = stack.peek();
			log("Adding class to open element: " + element);
			element.addElement(rubyClass);
			stack.push(rubyClass);
		} catch (StackEmptyException e) {
			log("Tried to add a class to the element on top of stack, but stack is empty");
		}
	}

	/**
	 * Returns the startIndex of the element (given a tokenIdentifiere prefix). -1
	 * if not found, or has an empty name
	 * 
	 * @return
	 */
	private static int findElement(String tokenIdentifier, char[] tokens, String curLine) {
		int tokenIndex = curLine.indexOf(tokenIdentifier);
		if (tokenIndex == -1) return -1;
		int start = tokenIndex + tokenIdentifier.length();
		String name = getToken(tokenIdentifier, tokens, curLine);
		if (name.length() == 0) return -1;
		log("Found start of element: " + curLine);
		return start;
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findModule(String curLine, int lineNum) {
		char[] tokens = { ' ', ';'};
		int start = findElement("module ", tokens, curLine);
		if (start == -1) return;
		String name = getToken("module ", tokens, curLine);
		if (Character.isLowerCase(name.charAt(0))) script.addParseError(new ParseError("Module names should begin with an uppercase letter.", lineNum, start, start + name.length()));
		RubyModule module = new RubyModule(name, new Position(lineNum, start));
		script.addModule(module);
		stack.push(module);
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findMethod(String curLine, int lineNum) {
		int methodIndex = curLine.indexOf("def");
		if (methodIndex == -1) return;
		log("Found method start: " + curLine);
		int start = methodIndex + "def ".length();
		String name = getMethodName(curLine);
		RubyMethod method = new RubyMethod(name, new Position(lineNum, start));
		log("method = " + method);
		try {
			RubyElement element = stack.peek();
			log("Adding method to open element: " + element);
			element.addElement(method);
			stack.push(method);
		} catch (StackEmptyException e) {
			log("Tried to add a method to the element on top of stack, but stack is empty");
		}
	}

	/**
	 * @param curLine
	 * @return
	 */
	private static String getMethodName(String curLine) {
		char[] tokens = { '(', ' '};
		return getToken("def ", tokens, curLine);
	}

	/**
	 * Given a string prefix, this method returns the next token (name) of the
	 * line delimited by any character within the delimiters array. If the next
	 * token is not ended by a member of the delimiters array, then the rest of
	 * the string is returned.
	 * 
	 * @param prefix
	 * @param delimiters
	 * @param line
	 * @return
	 */
	private static String getToken(String prefix, char[] delimiters, String line) {
		int endOfPrefix = line.indexOf(prefix) + prefix.length();
		for (int i = endOfPrefix; i < line.length(); i++) {
			char c = line.charAt(i);
			if (contains(delimiters, c)) { return line.substring(endOfPrefix, i); }
		}
		return line.substring(endOfPrefix);
	}

	/**
	 * Given a character array of characters and a character, the method
	 * determines if the given character is contained in the array
	 * 
	 * @param chars
	 * @param c
	 * @return a boolean to indicate if this array contains the character
	 */
	private static boolean contains(char[] chars, char c) {
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == c) return true;
		}
		return false;
	}

	/**
	 * @param string
	 */
	private static void log(String string) {
		System.out.println(string);
		//RubyPlugin.log(new Exception(string));
	}
}