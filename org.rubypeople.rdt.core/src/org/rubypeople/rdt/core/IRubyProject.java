/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_x.x.x/RDT.license or otherwise at
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
package org.rubypeople.rdt.core;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Chris
 * 
 */
public interface IRubyProject extends IRubyElement, IParent {

	public abstract IProject getProject();

	public abstract List getLoadPathEntries();

	public abstract List getReferencedProjects();

	public String[] getRequiredProjectNames() throws RubyModelException;

	public abstract void save() throws CoreException;

	/**
	 * Returns the first type found following this project's classpath with the
	 * given fully qualified name or <code>null</code> if none is found. The
	 * fully qualified name is a dot-separated name. For example, a class B
	 * defined as a member type of a class A in package x.y should have a the
	 * fully qualified name "x.y.A.B".
	 * 
	 * Note that in order to be found, a type name (or its toplevel enclosing
	 * type name) must match its corresponding compilation unit name. As a
	 * consequence, secondary types cannot be found using this functionality.
	 * Secondary types can however be explicitely accessed through their
	 * enclosing unit or found by the <code>SearchEngine</code>.
	 * 
	 * @param fullyQualifiedName
	 *            the given fully qualified name
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * @return the first type found following this project's classpath with the
	 *         given fully qualified name or <code>null</code> if none is
	 *         found
	 * @see IRubyType#getFullyQualifiedName(char)
	 * @since 2.0
	 */
	IRubyType findType(String fullyQualifiedName) throws RubyModelException;

}