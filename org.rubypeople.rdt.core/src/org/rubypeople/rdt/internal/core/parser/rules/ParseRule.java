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

import org.eclipse.core.resources.IMarker;
import org.rubypeople.rdt.internal.core.parser.ParseError;

public interface ParseRule {

	int ERROR = IMarker.SEVERITY_ERROR;
	int WARNING = IMarker.SEVERITY_WARNING;
	int OK = IMarker.SEVERITY_INFO;

	/**
	 * Evaluates the rule and tells whether the rule has passed or not
	 * 
	 * @return true if the rule has been passed, false if not
	 */
	public boolean isAllowed();

	/**
	 * Creates a ParseError which captures the failure of this rule
	 * 
	 * @return a ParseError generated to be used when this rule fails
	 */
	public ParseError getError();

	/**
	 * Flag to indicate whether the element in question should be added to the
	 * AST or ignored if this rule fails
	 * 
	 * @return a boolean to indicate whether failure of this rule should
	 *         eliminate the element from the AST
	 */
	public boolean addOnFailure();

	/**
	 * Flag to indicate whether the ParseError that this rule generates should
	 * be added as a (potential) marker
	 * 
	 * @return true if the ParseError should be retrieved and used (potentially
	 *         for markers)
	 */
	public boolean addError();

	/**
	 * Allows the ParseRule to optionally have some sort of processing that can
	 * be run on the elements in question
	 */
	public void run();
}