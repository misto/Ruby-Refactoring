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
package org.rubypeople.rdt.internal.core.parser.ast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.rubypeople.rdt.internal.core.parser.Position;
import org.rubypeople.rdt.internal.core.parser.RubyToken;

/**
 * @author Chris
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class RubyElement implements IRubyElement {

	protected String access;
	protected String name;
	protected Position start;
	protected Position end;
	protected int type;
	protected Set elements = new HashSet();

	public static final int CASE = RubyToken.CASE;
	public static final int WHILE = RubyToken.WHILE;
	public static final int IF = RubyToken.IF;
	public static final int DO = RubyToken.DO;
	public static final int FOR = RubyToken.FOR;
	public static final int BEGIN = RubyToken.BEGIN;
	public static final int UNLESS = RubyToken.UNLESS;
	public static final int UNTIL = RubyToken.UNTIL;
	public static final int CLASS_VAR = RubyToken.CLASS_VARIABLE;
	public static final int GLOBAL = RubyToken.GLOBAL;
	public static final int SCRIPT = -1;
	public static final int INSTANCE_VAR = RubyToken.INSTANCE_VARIABLE;
	public static final int REQUIRES = RubyToken.REQUIRES;
	public static final int MODULE = RubyToken.MODULE;
	public static final int CLASS = RubyToken.CLASS;
	public static final int METHOD = RubyToken.METHOD;

	public static final String PUBLIC = "public";
	public static final String PRIVATE = "private";
	public static final String READ = "read";
	public static final String WRITE = "write";
	public static final String PROTECTED = "protected";

	/**
	 * @param string
	 * @param name
	 * @param lineNum
	 * @param offset
	 */
	public RubyElement(int type, String name, int lineNum, int offset) {
		this.type = type;
		if (type == METHOD) {
			access = PUBLIC;
		} else if (type == INSTANCE_VAR) {
			access = PRIVATE;
		}
		this.name = name;
		this.start = new Position(lineNum, offset);
	}

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public Position getStart() {
		return start;
	}

	/**
	 * @return
	 */
	public Position getEnd() {
		return end;
	}

	/**
	 * @return
	 */
	public String getAccess() {
		return access;
	}

	public void setAccess(String newAccess) {
		access = newAccess;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.toString().hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		if (arg0 instanceof RubyElement) {
			RubyElement element = (RubyElement) arg0;
			return element.isType(this.type) && element.name.equals(this.name) && element.start.equals(this.start);
		}
		return false;
	}

	/**
	 * @return
	 */
	public int getElementCount() {
		return elements.size();
	}

	/**
	 * @param method
	 */
	public void addElement(RubyElement method) {
		elements.add(method);
	}

	/**
	 * @param element
	 * @return
	 */
	public boolean contains(RubyElement element) {
		RubyElement dup = getElement(element.getName());
		if (dup == null) return false;
		return dup.equals(element);
	}

	public RubyElement getElement(String name) {
		for (Iterator iter = elements.iterator(); iter.hasNext();) {
			RubyElement element = (RubyElement) iter.next();
			if (element.getName().equals(name)) { return element; }
		}
		return null;
	}

	public boolean isOutlineElement() {
		if (isBlock()) { return false; }
		return true;
	}

	/**
	 * @return
	 */
	private boolean isBlock() {
		return isType(RubyElement.FOR) || isType(RubyElement.CASE) || isType(RubyElement.DO) || isType(RubyElement.WHILE) || isType(RubyElement.UNLESS) || isType(RubyElement.UNTIL) || isType(RubyElement.IF) || isType(RubyElement.BEGIN);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.internal.core.parser.IRubyElement#getElements()
	 */
	public Object[] getElements() {
		Set outlineElements = new HashSet();
		for (Iterator iter = elements.iterator(); iter.hasNext();) {
			RubyElement element = (RubyElement) iter.next();
			if (element.isOutlineElement())
				outlineElements.add(element);
			else {
				Object[] elements = element.getElements();
				if (elements.length > 0) {
					outlineElements.addAll(Arrays.asList(elements));
				} else {
					continue;
				}
			}
		}
		return outlineElements.toArray();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.internal.core.parser.IRubyElement#hasElements()
	 */
	public boolean hasElements() {
		return getElements().length > 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return this.type + ": " + getName() + ", [" + getStart() + "," + getEnd() + "]";
	}

	/**
	 * @param lineNum
	 * @param offset
	 */
	public void setEnd(int lineNum, int offset) {
		this.end = new Position(lineNum, offset);
	}

	/**
	 * @param i
	 * @return
	 */
	public boolean isType(int i) {
		return type == i;
	}

	/**
	 * @return
	 */
	public boolean isMultiLine() {
		return isType(RubyElement.CLASS) || isType(RubyElement.MODULE) || isType(RubyElement.METHOD) || isBlock();
	}

	/**
	 * @return
	 */
	public boolean isVariable() {
		return isType(RubyElement.CLASS_VAR) || isType(RubyElement.INSTANCE_VAR) || isType(RubyElement.GLOBAL);
	}

	/**
	 * @return
	 */
	public int getType() {
		return type;
	}

	/**
	 * Adds the access level to the current element's access. This method does
	 * the nitty-gritty finding out if we added READ to a previously WRITE
	 * element, making it PUBLIC, etc.
	 * 
	 * @param newAccess
	 */
	public void addAccess(String newAccess) {
		if (newAccess == null) return;

		if (isType(RubyElement.METHOD)) {
			setAccess(newAccess);
			return;
		}

		String oldAccess = getAccess();
		if (newAccess.equals(RubyElement.PUBLIC)) {
			setAccess(newAccess);
			return;
		}
		if (newAccess.equals(RubyElement.READ)) {
			if (oldAccess.equals(RubyElement.PRIVATE)) {
				setAccess(newAccess);
				return;
			}
			if (oldAccess.equals(RubyElement.WRITE)) {
				setAccess(RubyElement.PUBLIC);
				return;
			}
		}
		if (newAccess.equals(RubyElement.WRITE)) {
			if (oldAccess.equals(RubyElement.PRIVATE)) {
				setAccess(newAccess);
				return;
			}
			if (oldAccess.equals(RubyElement.READ)) {
				setAccess(RubyElement.PUBLIC);
				return;
			}
		}

	}
}