/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse.
 * You can get copy of the GPL along with further information about RubyPeople
 * and third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_0.4.0/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.internal.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.rubypeople.rdt.internal.core.parser.ast.RubyElement;
import org.rubypeople.rdt.internal.core.parser.ast.RubyScript;
import org.rubypeople.rdt.internal.core.parser.rules.ParseRule;
import org.rubypeople.rdt.internal.core.parser.rules.ParseRuleFactory;

public class RubyParser {

	private static RubyParserStack stack = new RubyParserStack();
	private static RubyScript script;
	private static boolean inDocs;

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
				}
				if (inDocs) {
					offset = 0;
					lineNum++;
					continue;
				}
				findMultiLineDocBeginning(myLine);
				if (inDocs) {
					offset = 0;
					lineNum++;
					continue;
				}

				RubyTokenizer tokenizer = new RubyTokenizer(curLine);
				while (tokenizer.hasMoreTokens()) {
					RubyToken token = tokenizer.nextRubyToken();
					log(token.getText());
					if (token.isBlock()) {
						if (token.isType(RubyToken.WHILE) && parentIsBlockOfType(RubyElement.CASE)) continue;
						pushMultiLineElement(new RubyElement(token.getType(), token.getText(), lineNum, token.getOffset()));
						continue;
					}
					if (token.isDeclarationKeyword()) {
						addElementDeclaration(token, tokenizer, lineNum);
						continue;
					}
					if (token.isVariable()) {
						addVariable(token, lineNum);
						continue;
					}
					if (token.isType(RubyToken.VARIABLE_SUBSTITUTION)) {
						String varString = getVariable(token, lineNum);
						int type = getType(varString);
						if (type == RubyToken.IDENTIFIER) continue; // It's a local variable (hopefully)
						addVariable(new RubyToken(type, varString, token.getOffset() + token.getText().indexOf(varString)), lineNum);
						continue;
					}
					if (token.isType(RubyToken.END)) {
						try {
							stack.closeLastOpenElement(lineNum, token.getOffset());
						} catch (StackEmptyException e) {
							log(e.getMessage());
						}
					}
				}
				findPrivateModifier(myLine, lineNum);
				findAttributeReaderModifier(myLine, lineNum);
				findAttributeWriterModifier(myLine, lineNum);
				findAttributeAccessor(myLine, lineNum);
				offset = 0;
				lineNum++;
			}
		} catch (IOException e) {
			throw new ParseException(e);
		}
		script.setEnd(lineNum, offset);

		stack.clear();
		return script;
	}

	/**
	 * @param varString
	 * @return
	 */
	private static int getType(String varString) {
		if (varString.startsWith("$"))
			return  RubyToken.GLOBAL;
		if (varString.startsWith("@@"))
			return RubyToken.CLASS_VARIABLE;
		if (varString.startsWith("@"))
			return RubyToken.INSTANCE_VARIABLE;
		return RubyToken.IDENTIFIER;
	}

	/**
	 * @param token
	 * @param lineNum
	 * @return
	 */
	private static String getVariable(RubyToken token, int lineNum) {
		String text = token.getText();
		if (text.indexOf('{') == -1) { return text.substring(1); }
		if (text.indexOf('}') == -1) {
			script.addParseError(new ParseError("Incomplete variable substitution", lineNum, token.getOffset(), token.getOffset() + text.length()));
			return text.substring(2);
		}
		return text.substring(2, text.indexOf('}'));
	}

	/**
	 * @param token
	 * @param lineNum
	 */
	private static void addVariable(RubyToken token, int lineNum) {
		RubyElement element = new RubyElement(token.getType(), token.getText(), lineNum, token.getOffset());
		ParseRule rule = ParseRuleFactory.getRule(element, script);
		if (!rule.isAllowed()) {
			log("Failed rule, adding parse error");
			script.addParseError(rule.getError());
			if (rule.getSeverity() == ParseRule.ERROR) return;
			addElement(element);
		} else {
			log("Rule is Allowed!");
			addElement(element);
		}

	}

	private static void addElementDeclaration(RubyToken token, RubyTokenizer tokenizer, int lineNum) {
		if (!tokenizer.hasMoreTokens()) {
			script.addParseError(new ParseError("Incomplete " + token.getType() + " declaration.", lineNum, token.getOffset(), token.getOffset() + token.getText().length()));
			return;
		}
		RubyToken elementName = tokenizer.nextRubyToken();
		String name = elementName.getText();
		RubyElement element = new RubyElement(token.getType(), name, lineNum, elementName.getOffset());
		ParseRule rule = ParseRuleFactory.getRule(element, script);
		if (!rule.isAllowed()) {
			// TODO Add rule that checks require name is a string (in quotes or
			// a method which produces a string)
			log("Failed rule, adding parse error");
			script.addParseError(rule.getError());
			if (rule.getSeverity() == ParseRule.ERROR) return;
			addElement(element);
		} else {
			log("Rule is Allowed!");
			addElement(element);
		}

	}

	/**
	 * @param element
	 */
	private static void addElement(RubyElement element) {
		if (element.isMultiLine()) {
			pushMultiLineElement(element);
			return;
		}
		element.setEnd(element.getStart().getLineNumber(), element.getStart().getOffset() + element.getName().length());
		log("Adding element " + element);
		getParent(element).addElement(element);
	}

	/**
	 * @param element
	 * @return
	 */
	private static RubyElement getParent(RubyElement element) {
		if (element.isVariable()) {
			if (element.isType(RubyElement.GLOBAL)) { return script; }
			RubyElement parent = stack.findParentClassOrModule();
			if (parent == null) {
				parent = script;
			}
			return parent;
		}
		try {
			return stack.peek();
		} catch (StackEmptyException e) {
			script.addParseError(new ParseError("Attempted to add element to empty stack", element.getStart().getLineNumber(), element.getStart().getOffset(), element.getEnd().getOffset()));
			return script;
		}
	}

	/**
	 * @param blockType
	 * @return
	 */
	private static boolean parentIsBlockOfType(int blockType) {
		try {
			return stack.peek().isType(blockType);
		} catch (StackEmptyException e) {
			log(e.getMessage());
		}
		return false;
	}

	/**
	 * Wraps the RubyParserStack's pushAndLink method with a try/throws clause
	 * to log any StackEmptyExceptions
	 * 
	 * @param element
	 *            the RubyElement to be pushed onto the stack and added to the
	 *            last open element
	 */
	private static void pushMultiLineElement(RubyElement element) {
		try {
			stack.pushAndLink(element);
			log("Pushed multi-line element: " + element);
		} catch (StackEmptyException e) {
			log(e.getMessage());
		}
	}

	/**
	 * @param curLine
	 */
	private static String findMultiLineDocEnd(String curLine) {
		final String token = "=end";
		if (curLine.indexOf(token) == -1) return curLine;
		inDocs = false;
		return curLine.substring(endIndexOf(curLine, token));
	}

	/**
	 * @param curLine
	 * @param token
	 * @return
	 */
	private static int endIndexOf(String myLine, String token) {
		if (myLine.indexOf(token, 0) == -1) return -1;
		return myLine.indexOf(token, 0) + token.length();
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
			} else {
				RubyElement var = new RubyElement(RubyElement.INSTANCE_VAR, "@" + elementName, lineNum, myLine.indexOf(elementName));
				var.setAccess(accessRightsToGrant);
				parent.addElement(var);
			}

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
		String copy = myLine.substring(endIndexOf(myLine, prefix));
		StringTokenizer tokenizer = new StringTokenizer(copy, ", ");
		List list = new ArrayList();
		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken().substring(1));
		}
		return list;
	}

	/**
	 * If we are tracing the core plugin, output info the the console
	 * 
	 * @param string
	 */
	private static void log(String string) {
		//		if ( RubyPlugin.getDefault().isDebugging() ) {
		System.out.println(string);
		//		}
	}
}