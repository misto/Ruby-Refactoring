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

import org.rubypeople.rdt.internal.core.RubyMethod;

/**
 * @author Chris
 * 
 */
public interface IRubyType extends IRubyElement, IMember {

	public RubyMethod getMethod(String name);

	/**
	 * @return
	 */
	boolean isClass();

	/**
	 * @return
	 */
	boolean isModule();

	/**
	 * Returns the member type declared in this type with the given simple name.
	 * This is a handle-only method. The type may or may not exist.
	 * 
	 * @param name
	 *            the given simple name
	 * @return the member type declared in this type with the given simple name
	 */
	IRubyType getType(String name);

	/**
	 * @param string
	 * @return
	 */
	public IField getField(String string);

	/**
	 * Returns the name of this type's superclass, or <code>null</code> for
	 * source types that do not specify a superclass.
	 * <p>
	 * For interfaces, the superclass name is always
	 * <code>"java.lang.Object"</code>. For source types, the name as
	 * declared is returned, for binary types, the resolved, qualified name is
	 * returned. For anonymous types, the superclass name is the name appearing
	 * after the 'new' keyword'. If the superclass is a parameterized type, the
	 * string may include its type arguments enclosed in "&lt;&gt;". If the
	 * returned string is needed for anything other than display purposes, use
	 * {@link #getSuperclassTypeSignature()}which returns a structured type
	 * signature string containing more precise information.
	 * </p>
	 * 
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return the name of this type's superclass, or <code>null</code> for
	 *         source types that do not specify a superclass
	 */
	String getSuperclassName() throws RubyModelException;

}