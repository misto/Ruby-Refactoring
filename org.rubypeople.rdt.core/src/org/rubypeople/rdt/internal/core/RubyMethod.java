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
package org.rubypeople.rdt.internal.core;

import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * @author Chris
 * 
 */
public class RubyMethod extends NamedMember implements IMethod {

	/**
	 * @param name
	 */
	public RubyMethod(RubyElement parent, String name) {
		super(parent, name);
	}

	public int getElementType() {
		return RubyElement.METHOD;
	}

	/*
	 * @see RubyElement#getPrimaryElement(boolean)
	 */
	public IRubyElement getPrimaryElement(boolean checkOwner) {
		if (checkOwner) {
			RubyScript cu = (RubyScript) getAncestor(SCRIPT);
			if (cu.isPrimary()) return this;
		}
		IRubyElement primaryParent = this.parent.getPrimaryElement(false);
		// FIXME We need to send more info than the method name. Number of
		// params?
		return ((IType) primaryParent).getMethod(this.name);
	}

	public boolean isConstructor() {
		return getElementName().equals("initialize");
	}

	public boolean equals(Object o) {
		if (!(o instanceof RubyMethod)) return false;
		return super.equals(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IMember#getDeclaringType()
	 */
	public IType getDeclaringType() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyMethod#getVisibility()
	 */
	public int getVisibility() throws RubyModelException {
		RubyMethodElementInfo info = (RubyMethodElementInfo) getElementInfo();
		return info.getVisibility();
	}

}