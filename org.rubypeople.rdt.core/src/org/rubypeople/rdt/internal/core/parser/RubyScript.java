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
import java.util.HashSet;
import java.util.Set;
/**
 * @author Chris
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RubyScript extends RubyElement {
	private Set parseErrors = new HashSet();
	private static final String DEFAULT_NAME = "DEFAULT_NAME";
	
	public RubyScript() {
		super(DEFAULT_NAME, new Position(0,0));
	}

	/**
	 * @param requires
	 */
	public void addRequires(RubyRequires requires) {
		addElement(requires);
	}
	
	/**
	 * @param string
	 * @return
	 */
	public RubyRequires getRequires(String string) {
		return (RubyRequires) getElement(string);
	}
	/**
	 * @param rubyClass
	 */
	public void addClass(RubyClass rubyClass) {
		addElement(rubyClass);
	}
	/**
	 * @param string
	 * @return
	 */
	public RubyClass getClass(String string) {
		return (RubyClass) getElement(string);
	}
	
	/**
	 * @param exception
	 */
	public void addParseError(ParseError exception) {
		parseErrors.add(exception);
	}
	/**
	 * @return
	 */
	public boolean hasParseErrors() {
		return !parseErrors.isEmpty();
	}
	/**
	 * @return
	 */
	public Set getParseErrors() {
		return parseErrors;
	}
	/**
	 * @param string
	 * @return
	 */
	public RubyModule getModule(String string) {
		return (RubyModule) getElement(string);
	}
	/**
	 * @param module
	 */
	public void addModule(RubyModule module) {
		addElement(module);
	}
	/**
	 * @param string
	 * @return
	 */
	public RubyMethod getMethod(String string) {
		return (RubyMethod) getElement(string);
	}

	/**
	 * @return
	 */
	public int getErrorCount() {
		return parseErrors.size();
	}

}
