/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_0.4.0/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.internal.core.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Set;

import org.rubypeople.rdt.internal.core.parser.ast.RubyElement;
import org.rubypeople.rdt.internal.core.parser.ast.RubyScript;
import org.rubypeople.rdt.internal.core.parser.rules.ParseRule;
import org.rubypeople.rdt.internal.core.parser.rules.ParseRuleFactory;

public class RubyParser {

	private static RubyParserStack stack = new RubyParserStack();
	private static RubyScript script;
	private static boolean inDocs;
	private static boolean isDebugging = true;
	private static String currentClassName;

	/**
	 * @return
	 */
	public static synchronized RubyScript parse(String string) throws ParseException {
		script = new RubyScript();
		stack.push(script);
		BufferedReader reader = new BufferedReader(new StringReader(string));
		String curLine = null;
		int lineNum = 0;
		int offset = 0;
		boolean multiStringStarted = false;
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
					//log(token.getText());
					if (multiStringStarted &&
							tokenizer.isInMultiString() == false) {
						multiStringStarted = false;
						continue;
					}
					if (tokenizer.isInMultiString()) {
						multiStringStarted = true;
						curLine = reader.readLine();
						if (curLine == null) {
							break;
						}
						offset = 0;
						lineNum++;
						tokenizer.setNewFeed(curLine);
						continue;
					}	
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
						if (type == RubyToken.IDENTIFIER) continue; // It's a
						// local
						// variable
						// (hopefully)
						addVariable(new RubyToken(type, varString, token.getOffset() + token.getText().indexOf(varString)), lineNum);
						continue;
					}
					if (token.isType(RubyToken.END)) {
						try {
							stack.closeLastOpenElement(lineNum, token.getOffset());
							log("Closed last Element");
						} catch (StackEmptyException e) {
							log(e.getMessage());
						}
					}
					if (token.isAttributeModifier()) {
						while (tokenizer.hasMoreTokens()) {
							RubyToken next = tokenizer.nextRubyToken();
							if (!next.isType(RubyToken.SYMBOL)) {
								script.addParseError(new ParseError("Attribute modifier is not followed by one or more symbols", lineNum, next, ParseError.ERROR));
								break;
							}
							String name = "@" + next.getText().substring(1);
							RubyElement element = new RubyElement(RubyElement.INSTANCE_VAR, name, lineNum, next.getOffset());
							if (token.isType(RubyToken.ATTR_READER))
								element.setAccess(RubyElement.READ);
							else if (token.isType(RubyToken.ATTR_WRITER))
								element.setAccess(RubyElement.WRITE);
							else if (token.isType(RubyToken.ATTR_ACCESSOR))
								element.setAccess(RubyElement.PUBLIC);
							applyRules(element);
						}
						continue;
					}
					
					if (token.isMethodAccessModifier()) {
						while (tokenizer.hasMoreTokens()) {
							RubyToken next = tokenizer.nextRubyToken();
							if (!next.isType(RubyToken.SYMBOL)) {
								script.addParseError(new ParseError("Method access modifier is not followed by one or more symbols", lineNum, next, ParseError.ERROR));
								break;
							}
							String name = next.getText().substring(1);
							RubyElement element = new RubyElement(RubyElement.METHOD, name, lineNum, next.getOffset());
							if (token.isType(RubyToken.PRIVATE))
								element.setAccess(RubyElement.PRIVATE);
							else if (token.isType(RubyToken.PROTECTED))
								element.setAccess(RubyElement.PROTECTED);
							applyRules(element, false);
						}
						continue;
					}
				}
				// TODO Fold these into Tokenizer as well!
				//findPrivateModifier(myLine, lineNum);
				
				// tried to read multiline tokens and got end of stream 
				if (curLine == null) {
					break;
				}
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

	private static boolean isMethodOfCurrentClassOrModule(String methodName) {
		if (methodName.compareTo("self") == 0) {
			return true;
		}
		if (currentClassName != null && methodName.compareTo(currentClassName) == 0) {
			return true;
		}
		return false;
	}
	
	/**
	 * @param element
	 * @param isDeclaration
	 */
	private static void applyRules(RubyElement element, boolean isDeclaration) {
		Set rules = ParseRuleFactory.getRules(element, getParent(element), isDeclaration);
		boolean addElement = true;
		for (Iterator iter = rules.iterator(); iter.hasNext();) {
			ParseRule rule = (ParseRule) iter.next();
			rule.run();
			if (!rule.isAllowed()) {
				log("Failed rule");
				if (rule.addError()) {
					log("Adding parse error");
					script.addParseError(rule.getError());
				}
				if (!rule.addOnFailure()) addElement = false;
			}
		}
		if (addElement) addElement(element);
		
	}

	/**
	 * @param varString
	 * @return
	 */
	private static int getType(String varString) {
		if (varString.startsWith("$")) return RubyToken.GLOBAL;
		if (varString.startsWith("@@")) return RubyToken.CLASS_VARIABLE;
		if (varString.startsWith("@")) return RubyToken.INSTANCE_VARIABLE;
		return RubyToken.IDENTIFIER;
	}

	/**
	 * @param token
	 * @param lineNum
	 * @return
	 */
	private static String getVariable(RubyToken token, int lineNum) {
		// TODO Make this logic be in Tokenizer, return the variable inside as a
		// separate token!
		int tmpIndex = 0;
		int closeBraceIndex;
		String text = token.getText();
		closeBraceIndex = text.indexOf('}');
		if (text.indexOf('{') == -1) { return text.substring(1); }
		if (closeBraceIndex == -1) {
			script.addParseError(new ParseError("Incomplete variable substitution", lineNum, token.getOffset(), token.getOffset() + text.length(), ParseError.ERROR));
			return text.substring(2);
		}
		tmpIndex = text.indexOf('[');
		if (tmpIndex != -1 && tmpIndex <= closeBraceIndex) {
			return text.substring(2, tmpIndex);
		}
		tmpIndex = text.indexOf('.');
		if (tmpIndex != -1 && tmpIndex <= closeBraceIndex) {
			return text.substring(2, tmpIndex);
		}
		return text.substring(2, text.indexOf('}'));
	}

	/**
	 * @param token
	 * @param lineNum
	 */
	private static void addVariable(RubyToken token, int lineNum) {
		RubyElement element = new RubyElement(token.getType(), token.getText(), lineNum, token.getOffset());
		applyRules(element);
	}

	/**
	 * @param element
	 */
	private static void applyRules(RubyElement element) {
		applyRules(element, true);
	}

	private static void addElementDeclaration(RubyToken token, RubyTokenizer tokenizer, int lineNum) {
		if (!tokenizer.hasMoreTokens()) {
			script.addParseError(new ParseError("Incomplete " + token.getType() + " declaration.", lineNum, token.getOffset(), token.getOffset() + token.getText().length(), ParseError.ERROR));
			return;
		}
		RubyToken elementName = tokenizer.nextRubyToken();
		while (token.getText().compareTo("def") == 0 && 
				isMethodOfCurrentClassOrModule(elementName.getText())) {
			elementName = tokenizer.nextRubyToken();
		}
		
		String name = elementName.getText();
		if (token.getText().compareTo("class") == 0 || 
				token.getText().compareTo("module") == 0) {
			currentClassName = name; 
		}
		
		RubyElement element = new RubyElement(token.getType(), name, lineNum, elementName.getOffset());
		applyRules(element);
	}

	/**
	 * @param element
	 */
	private static void addElement(RubyElement element) {
		if (element.isMultiLine()) {
			pushMultiLineElement(element);
			return;
		}
		if (element.getEnd() == null) {
			element.setEnd(element.getStart().getLineNumber(), 
					element.getStart().getOffset() +
					element.getName().length()
					);
		}
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
			script.addParseError(new ParseError("Attempted to add element to empty stack", element, ParseError.INFO));
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
	 * If we are tracing the core plugin, output info the the console
	 * 
	 * @param string
	 */
	private static void log(String string) {
		if ( RubyParser.isDebugging) {
			System.out.println(string);
		}
	}
	
	public static void setDebugging(boolean newValue) {
		RubyParser.isDebugging = newValue ;	
	}
}