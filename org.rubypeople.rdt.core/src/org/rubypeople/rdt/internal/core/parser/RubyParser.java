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

/**
 * @author Chris
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class RubyParser {

	private static List openElements = new ArrayList();
	private static RubyScript script;
	private static final char[] VARIABLE_END_CHARS = { ' ', '.', '[', '(', ')', ']', ',', '}', '{'};

	/**
	 * @return
	 */
	public static RubyScript parse(String string) throws ParseException {
		script = new RubyScript();
		openElements.add(script);
		BufferedReader reader = new BufferedReader(new StringReader(string));
		String curLine = null;
		int lineNum = 0;
		int offset = 0;
		try {
			while ((curLine = reader.readLine()) != null) {
				String myLine = removeAfterPoundSymbol(curLine);
				findBegin(myLine, lineNum);
				findIf(myLine, lineNum);
				findCase(myLine, lineNum);
				findUnless(myLine, lineNum);
				findUntil(myLine, lineNum);
				findWhile(myLine, lineNum);
				findFor(myLine, lineNum);
				findRequires(myLine, lineNum);
				findModule(myLine, lineNum);
				findClass(myLine, lineNum);
				findMethod(myLine, lineNum);
				findClassVariable(myLine, lineNum);
				findInstanceVariable(myLine, lineNum);
				findGlobal(myLine, lineNum);
				findPrivateModifier(myLine);
				findAttributeReaderModifier(myLine);
				findAttributeWriterModifier(myLine);
				findAttributeAccessor(myLine);
				findEnd(myLine, lineNum);
				offset = 0;
				lineNum++;
			}
		} catch (IOException e) {
			throw new ParseException(e);
		}
		script.setEnd(new Position(lineNum, offset));

		cleanUp();
		return script;
	}

	/**
	 * @param myLine
	 */
	private static void findAttributeAccessor(String myLine) {
		findAccessModifier(myLine, RubyElement.PUBLIC, "attr_accessor ", "@");
	}

	/**
	 * @param myLine
	 */
	private static void findAttributeWriterModifier(String myLine) {
		// TODO Auto-generated method stub
		findAccessModifier(myLine, RubyElement.WRITE, "attr_writer ", "@");
	}

	/**
	 * @param myLine
	 */
	private static void findAttributeReaderModifier(String myLine) {
		findAccessModifier(myLine, RubyElement.READ, "attr_reader ", "@");
	}

	/**
	 * @param myLine
	 */
	private static void findAccessModifier(String myLine, String accessRightsToGrant, String accessModifierTag, String symbolPrefix) {
		if (myLine.indexOf(accessModifierTag) == -1) return;

		List tokens = getSymbols(myLine, accessModifierTag);
		RubyElement parent = findParentClassOrModule();
		if (parent == null) return;

		for (Iterator iter = tokens.iterator(); iter.hasNext();) {
			String elementName = (String) iter.next();
			RubyElement element = parent.getElement(symbolPrefix + elementName);
			if (element != null) {
				element.setAccess(accessRightsToGrant);
			} else {
				String error = "Attempting to set access rights " + accessRightsToGrant + " to an unknown element " + elementName;
				log(error);
				script.addParseError(new ParseException(error));
			}
		}
	}

	/**
	 * @param myLine
	 */
	private static void findPrivateModifier(String myLine) {
		findAccessModifier(myLine, RubyElement.PRIVATE, "private ", "");
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
	 *  
	 */
	private static void cleanUp() {
		openElements.clear();
	}

	/**
	 * @param lineNum
	 * @param myLine
	 */
	private static void findBegin(String myLine, int lineNum) {
		int beginIndex = myLine.indexOf("begin");
		if (beginIndex != -1) {
			RubyBegin begin = new RubyBegin(new Position(lineNum, beginIndex));
			RubyElement element = peek();
			element.addElement(begin);
			openElements.add(begin);
		}
	}

	/**
	 * @param lineNum
	 * @param myLine
	 */
	private static void findFor(String myLine, int lineNum) {
		int forIndex = myLine.indexOf("for ");
		if ((forIndex != -1) && (!inQuotes(forIndex, myLine))) {
			RubyFor rubyFor = new RubyFor(new Position(lineNum, forIndex));
			RubyElement element = peek();
			element.addElement(rubyFor);
			openElements.add(rubyFor);
		}
	}

	/**
	 * @param lineNum
	 * @param myLine
	 */
	private static void findWhile(String myLine, int lineNum) {
		Matcher whileMatcher = Pattern.compile("^(;\\s+)?\\s*while ").matcher(myLine);
		if (whileMatcher.find()) {
			int start = whileMatcher.end() - 6;
			RubyWhile rubyWhile = new RubyWhile(new Position(lineNum, start));
			RubyElement element = peek();
			if (!(element instanceof RubyCase)) {
				element.addElement(rubyWhile);
				openElements.add(rubyWhile);
			}
		}
	}

	/**
	 * @param curLine
	 * @return
	 */
	private static String removeAfterPoundSymbol(String curLine) {
		String myLine = curLine;
		int poundStart = curLine.indexOf("#");
		if (poundStart != -1) {
			if (!inQuotes(poundStart, curLine)) myLine = curLine.substring(0, poundStart);
		}
		return myLine;
	}

	/**
	 * @param poundStart
	 * @return
	 */
	private static boolean inQuotes(int poundStart, String curLine) {
		List openQuotes = new ArrayList();
		for (int index = 0; index < poundStart; index++) {
			char c = curLine.charAt(index);
			if ((c == '\'') || (c == '"')) {
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
		Matcher untilMatcher = Pattern.compile("^(;\\s+)?\\s*until ").matcher(curLine);
		if (untilMatcher.find()) {
			int start = untilMatcher.end() - 6;
			RubyUntil until = new RubyUntil(new Position(lineNum, start));
			RubyElement element = peek();
			element.addElement(until);
			openElements.add(until);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findCase(String curLine, int lineNum) {
		Matcher caseMatcher = Pattern.compile("^(;\\s+)?\\s*case ").matcher(curLine);
		if (caseMatcher.find()) {
			int start = caseMatcher.end() - 5;
			RubyCase rubyCase = new RubyCase(new Position(lineNum, start));
			RubyElement element = peek();
			element.addElement(rubyCase);
			openElements.add(rubyCase);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findUnless(String curLine, int lineNum) {
		Matcher unlessMatcher = Pattern.compile("^(;\\s+)?\\s*unless ").matcher(curLine);
		if (unlessMatcher.find()) {
			int unlessIndex = unlessMatcher.end() - 7;
			RubyUnless unless = new RubyUnless(new Position(lineNum, unlessIndex));
			RubyElement element = peek();
			element.addElement(unless);
			openElements.add(unless);
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
			RubyElement element = peek();
			log("Adding if block to open element: " + element);
			element.addElement(rubyIf);
			openElements.add(rubyIf);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findGlobal(String curLine, int lineNum) {
		int globalIndex = curLine.indexOf("$");
		if (globalIndex != -1) {
			String name = getToken("$", VARIABLE_END_CHARS, curLine);
			RubyGlobal global = new RubyGlobal(name, new Position(lineNum, globalIndex + 1));
			script.addElement(global);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findClassVariable(String curLine, int lineNum) {
		int instanceIndex = curLine.indexOf("@@");
		if (instanceIndex != -1) {
			String name = getToken("@@", VARIABLE_END_CHARS, curLine);
			RubyClassVariable variable = new RubyClassVariable(name, new Position(lineNum, instanceIndex + "@@".length()));
			RubyElement element = peek();
			element.addElement(variable);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findInstanceVariable(String curLine, int lineNum) {
		int instanceIndex = curLine.indexOf("@");
		if (instanceIndex != -1) {
			if (curLine.indexOf("@@") != -1) return;
			String name = getToken("@", VARIABLE_END_CHARS, curLine);
			log("Found instance variable: " + name);
			RubyInstanceVariable variable = new RubyInstanceVariable(name, new Position(lineNum, instanceIndex + "@".length()));
			RubyElement element = findParentClassOrModule();
			if (element != null) {
				element.addElement(variable);
				log("Added instance variable to open element: " + element);
			} else {
				script.addElement(variable);
			}
		}
	}

	/**
	 * @return
	 */
	private static RubyElement findParentClassOrModule() {
		for (int i = 1; i < openElements.size(); i++) {
			RubyElement element = (RubyElement) openElements.get(openElements.size() - i);

			if ((element instanceof RubyModule) || (element instanceof RubyClass)) { return element; }
		}
		return null;
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findEnd(String curLine, int lineNum) {
		int endIndex = curLine.indexOf("end");
		if (endIndex != -1) {
			log("Found end: " + curLine);
			closeLastOpenElement(lineNum, endIndex);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findRequires(String curLine, int lineNum) {
		int requiresIndex = curLine.indexOf("require ");
		if (requiresIndex != -1) {
			log("Found requires: " + curLine);
			int start = requiresIndex + "require \"".length();
			if (start == curLine.length() ) return;			
			String name = curLine.substring(start, curLine.length() - 1);
			RubyRequires requires = new RubyRequires(name, new Position(lineNum, start));
			if (!script.contains(requires)) {
				script.addRequires(requires);
			} else {
				script.addParseError(new ParseException("Duplicate requires statement unnecessary."));
			}
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findClass(String curLine, int lineNum) {
		int classIndex = curLine.indexOf("class");
		if (classIndex != -1) {
			log("Found class start: " + curLine);
			int start = classIndex + "class ".length();
			String name = curLine.substring(start);
			if (Character.isLowerCase(name.charAt(0))) script.addParseError(new ParseException("Class names should begin with an uppercase letter."));
			RubyClass rubyClass = new RubyClass(name, new Position(lineNum, start));
			RubyElement element = peek();
			log("Adding class to open element: " + element);
			element.addElement(rubyClass);
			openElements.add(rubyClass);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findModule(String curLine, int lineNum) {
		int moduleIndex = curLine.indexOf("module");
		if (moduleIndex != -1) {
			log("Found module start: " + curLine);
			int start = moduleIndex + "module ".length();
			String name = curLine.substring(start);
			if (Character.isLowerCase(name.charAt(0))) script.addParseError(new ParseException("Module names should begin with an uppercase letter."));
			RubyModule module = new RubyModule(name, new Position(lineNum, start));
			script.addModule(module);
			openElements.add(module);
		}
	}

	/**
	 * @param curLine
	 * @param lineNum
	 */
	private static void findMethod(String curLine, int lineNum) {
		int methodIndex = curLine.indexOf("def");
		if (methodIndex != -1) {
			log("Found method start: " + curLine);
			int start = methodIndex + "def ".length();
			String name = getMethodName(curLine);
			RubyMethod method = new RubyMethod(name, new Position(lineNum, start));
			log("method = " + method);
			RubyElement element = peek();
			log("Adding method to open element: " + element);
			element.addElement(method);
			openElements.add(method);
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
	 * @param string
	 * @param tokens
	 * @param line
	 * @return
	 */
	private static String getToken(String prefix, char[] tokens, String line) {
		int endOfPrefix = line.indexOf(prefix) + prefix.length();
		for (int i = endOfPrefix; i < line.length(); i++) {
			char c = line.charAt(i);
			if (contains(tokens, c)) { return line.substring(endOfPrefix, i); }
		}
		return line.substring(endOfPrefix);
	}

	/**
	 * @param tokens
	 * @param c
	 * @return
	 */
	private static boolean contains(char[] tokens, char c) {
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i] == c) return true;
		}
		return false;
	}

	/**
	 * @return
	 */
	private static RubyElement peek() {
		return (RubyElement) openElements.get(openElements.size() - 1);
	}

	/**
	 * @param string
	 */
	private static void log(String string) {
		System.out.println(string);
	}

	/**
	 *  
	 */
	private static void closeLastOpenElement(int endLine, int offset) {
		RubyElement elem = (RubyElement) openElements.remove(openElements.size() - 1);
		elem.setEnd(new Position(endLine, offset));
		log("Closed element:" + elem);
	}
}
