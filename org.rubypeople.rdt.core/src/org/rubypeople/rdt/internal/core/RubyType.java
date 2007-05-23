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

import java.util.ArrayList;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.core.util.MementoTokenizer;

/**
 * @author Chris
 * 
 */
public class RubyType extends NamedMember implements IType {

	public RubyType(RubyElement parent, String name) {
		super(parent, name);
	}

	/**
	 * @see IType
	 */
	public String getSuperclassName() throws RubyModelException {
		RubyTypeElementInfo info = (RubyTypeElementInfo) getElementInfo();
		return info.getSuperclassName();
	}

	/**
	 * @see IType
	 */
	public String[] getIncludedModuleNames() throws RubyModelException {
		RubyTypeElementInfo info = (RubyTypeElementInfo) getElementInfo();
		return info.getIncludedModuleNames();
	}
    
    /**
     * @see IType#isMember()
     */
    public boolean isMember() {
        return getDeclaringType() != null;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.internal.core.parser.RubyElement#getElementType()
	 */
	public int getElementType() {
		return IRubyElement.TYPE;
	}

	/**
	 * @see IType#getField
	 */
	public IField getField(String fieldName) {
		if (fieldName.startsWith("@@"))
		  return new RubyClassVar(this, fieldName);
		if (fieldName.startsWith("@"))
			  return new RubyInstVar(this, fieldName);
		if (fieldName.startsWith("$"))
			  return new RubyGlobal(this, fieldName);
		if (Character.isUpperCase(fieldName.charAt(0)))
			  return new RubyConstant(this, fieldName);
		Assert.isTrue(false, "Tried to access a field which isn't an instance variable, class variable, global or constant");
		return null;
	}

	/**
	 * @see IType
	 */
	public IField[] getFields() throws RubyModelException {
		ArrayList list = getChildrenOfType(FIELD);
		IField[] array = new IField[list.size()];
		list.toArray(array);
		return array;
	}

	public RubyMethod getMethod(String name, String[] parameterNames) {
		return new RubyMethod(this, name, parameterNames);
	}

	/**
	 * @see IType
	 */
	public IMethod[] getMethods() throws RubyModelException {
		ArrayList list = getChildrenOfType(METHOD);
		IMethod[] array = new IMethod[list.size()];
		list.toArray(array);
		return array;
	}

	/**
	 * @see IMember
	 */
	public IType getDeclaringType() {
		IRubyElement parentElement = getParent();
		while (parentElement != null) {
			if (parentElement.getElementType() == IRubyElement.TYPE) {
				return (IType) parentElement;
			} else if (parentElement instanceof IMember) {
				parentElement = parentElement.getParent();
			} else {
				return null;
			}
		}
		return null;
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
		switch (primaryParent.getElementType()) {
		case IRubyElement.SCRIPT:
			return ((IRubyScript) primaryParent).getType(this.name);
		case IRubyElement.TYPE:
			return ((IType) primaryParent).getType(this.name);
		case IRubyElement.INSTANCE_VAR:
		case IRubyElement.CLASS_VAR:
		case IRubyElement.BLOCK:
		case IRubyElement.LOCAL_VARIABLE:
		case IRubyElement.METHOD:
			return ((IMember) primaryParent).getType(this.name, this.occurrenceCount);
		}
		return this;
	}

	/**
	 * @see IType
	 */
	public IType getType(String typeName) {
		return new RubyType(this, typeName);
	}

	public boolean equals(Object o) {
		if (!(o instanceof RubyType)) return false;
		return super.equals(o);
	}

	/*
	 * (non-Rubydoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyType#isClass()
	 */
	public boolean isClass() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyType#isModule()
	 */
	public boolean isModule() {
		return false;
	}

	public IMethod createMethod(String contents, IRubyElement sibling,
			boolean force, IProgressMonitor monitor) throws RubyModelException {
		CreateMethodOperation op = new CreateMethodOperation(this, contents, force);
		if (sibling != null) {
			op.createBefore(sibling);
		}
		op.runOperation(monitor);
		return (IMethod) op.getResultElements()[0];		
	}

	public ISourceFolder getSourceFolder() {
		IRubyElement parentElement = this.parent;
		while (parentElement != null) {
			if (parentElement.getElementType() == IRubyElement.SOURCE_FOLDER) {
				return (ISourceFolder)parentElement;
			}
			else {
				parentElement = parentElement.getParent();
			}
		}
		Assert.isTrue(false);  // should not happen
		return null;
	}

	public String getFullyQualifiedName() {
		IType declaring = getDeclaringType();
		if (declaring != null) {
			return declaring.getFullyQualifiedName() + "::" + getElementName();
		}
		return getElementName();
	}
	
	/*
	 * @see RubyElement
	 */
	public IRubyElement getHandleFromMemento(String token, MementoTokenizer memento, WorkingCopyOwner workingCopyOwner) {
		switch (token.charAt(0)) {
			case JEM_COUNT:
				return getHandleUpdatingCountFromMemento(memento, workingCopyOwner);
			case JEM_FIELD:
				if (!memento.hasMoreTokens()) return this;
				String fieldName = memento.nextToken();
				RubyElement field = (RubyElement)getField(fieldName);
				return field.getHandleFromMemento(memento, workingCopyOwner);
			case JEM_METHOD:
				if (!memento.hasMoreTokens()) return this;
				String selector = memento.nextToken();
				ArrayList params = new ArrayList();
				nextParam: while (memento.hasMoreTokens()) {
					token = memento.nextToken();
					switch (token.charAt(0)) {
						case JEM_TYPE:
							break nextParam;
						case JEM_METHOD:
							if (!memento.hasMoreTokens()) return this;
							String param = memento.nextToken();
							StringBuffer buffer = new StringBuffer();
							params.add(buffer.toString() + param);
							break;
						default:
							break nextParam;
					}
				}
				String[] parameters = new String[params.size()];
				params.toArray(parameters);
				RubyElement method = (RubyElement)getMethod(selector, parameters);
				switch (token.charAt(0)) {
					case JEM_TYPE:
					case JEM_LOCALVARIABLE:
						return method.getHandleFromMemento(token, memento, workingCopyOwner);
					default:
						return method;
				}
			case JEM_TYPE:
				String typeName;
				if (memento.hasMoreTokens()) {
					typeName = memento.nextToken();
					char firstChar = typeName.charAt(0);
					if (firstChar == JEM_FIELD || firstChar == JEM_METHOD || firstChar == JEM_TYPE || firstChar == JEM_COUNT) {
						token = typeName;
						typeName = ""; //$NON-NLS-1$
					} else {
						token = null;
					}
				} else {
					typeName = ""; //$NON-NLS-1$
					token = null;
				}
				RubyElement type = (RubyElement)getType(typeName);
				if (token == null) {
					return type.getHandleFromMemento(memento, workingCopyOwner);
				} else {
					return type.getHandleFromMemento(token, memento, workingCopyOwner);
				}			
		}
		return null;
	}

	/**
	 * @see IType#getTypeQualifiedName(char)
	 */
	public String getTypeQualifiedName(String enclosingTypeSeparator) {
		try {
			return getTypeQualifiedName(enclosingTypeSeparator, false/*don't show parameters*/);
		} catch (RubyModelException e) {
			// exception thrown only when showing parameters
			return null;
		}
	}

	/**
	 * @see IType
	 */
	public IType[] getTypes() throws RubyModelException {
		ArrayList list= getChildrenOfType(TYPE);
		IType[] array= new IType[list.size()];
		list.toArray(array);
		return array;
	}

}