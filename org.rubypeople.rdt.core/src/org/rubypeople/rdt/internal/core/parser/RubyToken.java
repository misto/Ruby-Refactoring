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

/**
 * RubyToken
 * 
 * @author CAWilliams
 *  
 */
public class RubyToken {

	public static final int UNKNOWN = -1;
	public static final int WHILE = 0;
	public static final int IF = 1;
	public static final int UNTIL = 2;
	public static final int CASE = 3;
	public static final int DO = 4;
	public static final int FOR = 5;
	public static final int UNLESS = 6;
	public static final int BEGIN = 7;
	public static final int GLOBAL = 8;
	public static final int INSTANCE_VARIABLE = 9;
	public static final int CLASS_VARIABLE = 10;
	public static final int METHOD = 11;
	public static final int VARIABLE_SUBSTITUTION = 12;
	public static final int STRING_TEXT = 13;
	public static final int IDENTIFIER = 14;
	public static final int CLASS = 15;
	public static final int MODULE = 16;
	public static final int REQUIRES = 17;
	public static final int END = 18;
	public static final int WHILE_MODIFIER = 19;
	public static final int UNTIL_MODIFIER = 20;
	public static final int UNLESS_MODIFIER = 21;
	public static final int IF_MODIFIER = 22;
	public static final int CONSTANT = 23;
	public static final int MAX_TOKEN_NUM = 23;

	private int offset;
	private String text;
	private int type;
	
	/**
	 * @param type
	 * @param text
	 * @param start
	 */
	public RubyToken(int type, String text, int start) {
		this.type = type;
		this.text = text;
		this.offset = start;
	}

	/**
	 * @return
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * @return
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return
	 */
	public boolean isBlock() {
		return (this.getType() == (BEGIN) || this.getType() == (CASE) || this.getType() == (IF) || this.getType() == (UNLESS) || this.getType() == (UNTIL) || this.getType() == (WHILE) || this.getType() == (FOR) || this.getType() == (DO));
	}

	/**
	 * @param string
	 * @return
	 */
	public boolean isType(int type) {
		return getType() == type;
	}

	/**
	 * @return
	 */
	public boolean isDeclarationKeyword() {
		return this.getType() == CLASS || this.getType() == MODULE || this.getType() == METHOD || this.getType() == REQUIRES;
	}

	/**
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * @return
	 */
	public boolean isVariable() {
		return isType(RubyToken.INSTANCE_VARIABLE) || isType(GLOBAL) || isType(CLASS_VARIABLE);
	}

}