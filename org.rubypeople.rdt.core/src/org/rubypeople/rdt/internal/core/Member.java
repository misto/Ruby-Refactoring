/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyType;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * @see IMember
 */

public abstract class Member extends SourceRefElement implements IMember {

	protected Member(RubyElement parent) {
		super(parent);
	}

	protected static boolean areSimilarMethods(String name1, String[] params1, String name2, String[] params2, String[] simpleNames1) {
		if (name1.equals(name2)) {
			int params1Length = params1.length;
			if (params1Length == params2.length) {
				// TODO Check param types?
				return true;
			}
		}
		return false;
	}

	/**
	 * @see IMember
	 */
	public IRubyType getDeclaringType() {
		RubyElement parentElement = (RubyElement) getParent();
		if (parentElement.getElementType() == TYPE) { return (IRubyType) parentElement; }
		return null;
	}
	
	/*
	 * Returns the outermost context defining a local element. Per construction,
	 * it can only be a method/field/initializarer member; thus, returns null if
	 * this member is already a top-level type or member type. e.g for
	 * X.java/X/Y/foo()/Z/bar()/T, it will return X.java/X/Y/foo()
	 */
	public Member getOuterMostLocalContext() {
		IRubyElement current = this;
		Member lastLocalContext = null;
		parentLoop: while (true) {
			switch (current.getElementType()) {
			case SCRIPT:
				break parentLoop; // done recursing
			case TYPE:
				// cannot be a local context
				break;
			case CLASS_VAR:
			case INSTANCE_VAR:
			case METHOD:
				// these elements can define local members
				lastLocalContext = (Member) current;
				break;
			}
			current = current.getParent();
		}
		return lastLocalContext;
	}

	/**
	 * @see IMember
	 */
	public ISourceRange getNameRange() throws RubyModelException {
		MemberElementInfo info = (MemberElementInfo) getElementInfo();
		return new SourceRange(info.getNameSourceStart(), info.getNameSourceEnd() - info.getNameSourceStart() + 1);
	}

	/**
	 * @see IMember
	 */
	public IRubyType getType(String typeName, int count) {
		RubyType type = new RubyType(this, typeName);
		type.occurrenceCount = count;
		return type;
	}

	/**
	 */
	public String readableName() {
		IRubyElement declaringType = getDeclaringType();
		if (declaringType != null) {
			String declaringName = ((RubyElement) getDeclaringType()).readableName();
			StringBuffer buffer = new StringBuffer(declaringName);
			buffer.append("::");
			buffer.append(this.getElementName());
			return buffer.toString();
		}
		return super.readableName();
	}

	/**
	 * Updates the name range for this element.
	 */
	protected void updateNameRange(int nameStart, int nameEnd) {
		try {
			MemberElementInfo info = (MemberElementInfo) getElementInfo();
			info.setNameSourceStart(nameStart);
			info.setNameSourceEnd(nameEnd);
		} catch (RubyModelException npe) {
			return;
		}
	}
}
