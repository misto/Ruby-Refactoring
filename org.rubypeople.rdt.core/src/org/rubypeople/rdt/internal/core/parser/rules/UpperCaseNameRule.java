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
package org.rubypeople.rdt.internal.core.parser.rules;

import org.rubypeople.rdt.internal.core.parser.ParseError;
import org.rubypeople.rdt.internal.core.parser.ast.RubyElement;

/**
 * @author Chris
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class UpperCaseNameRule implements ParseRule {

	private RubyElement element;

	/**
	 * @param token
	 */
	public UpperCaseNameRule(RubyElement token) {
		this.element = token;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.internal.core.parser.ParseRule#getError()
	 */
	public ParseError getError() {
		String type = "Element";
		if (element.isType(RubyElement.CLASS)) {
			type = "Class";
		}
		else if (element.isType(RubyElement.MODULE)) {
			type = "Module";
		}
		return new ParseError(type + " names should begin with an uppercase letter.", element, getSeverity());
	}

	public boolean isAllowed() {
		return (!(Character.isLowerCase(element.getName().charAt(0))));
	}
	
	private int getSeverity() {
		return ParseRule.ERROR;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.core.parser.rules.ParseRule#addOnFailure()
	 */
	public boolean addOnFailure() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.core.parser.rules.ParseRule#addError()
	 */
	public boolean addError() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.core.parser.rules.ParseRule#run()
	 */
	public void run() {}
}
