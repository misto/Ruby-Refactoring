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
public interface IType extends IRubyElement, IMember {

	public RubyMethod getMethod(String name, String[] parameterNames);

	/**
	 * Returns the methods and constructors declared by this type. For binary
	 * types, this may include the special <code>&lt;clinit&gt</code>; method
	 * and synthetic methods. If this is a source type, the results are listed
	 * in the order in which they appear in the source, otherwise, the results
	 * are in no particular order.
	 * 
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return the methods and constructors declared by this type
	 */
	IMethod[] getMethods() throws RubyModelException;

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
	IType getType(String name);

	/**
	 * @param string
	 * @return
	 */
	public IField getField(String string);

	/**
	 * Returns the fields declared by this type. If this is a source type, the
	 * results are listed in the order in which they appear in the source,
	 * otherwise, the results are in no particular order. For binary types, this
	 * includes synthetic fields.
	 * 
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return the fields declared by this type
	 */
	IField[] getFields() throws RubyModelException;

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

	/**
	 * Returns the names of interfaces that this type implements or extends, in
	 * the order in which they are listed in the source.
	 * </p>
	 * For classes, this gives the interfaces that this class implements. For
	 * interfaces, this gives the interfaces that this interface extends. An
	 * empty collection is returned if this type does not implement or extend
	 * any interfaces. For source types, simple names are returned, for binary
	 * types, qualified names are returned. For anonymous types, an empty
	 * collection is always returned. If the list of supertypes includes
	 * parameterized types, the string may include type arguments enclosed in
	 * "&lt;&gt;". If the result is needed for anything other than display
	 * purposes, use {@link #getSuperInterfaceTypeSignatures()} which returns
	 * structured signature strings containing more precise information.
	 * </p>
	 * 
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource.
	 * @return the names of interfaces that this type implements or extends, in
	 *         the order in which they are listed in the source, an empty
	 *         collection if none
	 */
	String[] getIncludedModuleNames() throws RubyModelException;

    public boolean isMember() throws RubyModelException;

}