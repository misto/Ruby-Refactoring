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

import org.eclipse.core.resources.IResourceStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * @see IRubyModelStatus
 */

public class RubyModelStatus extends Status implements IRubyModelStatus, IRubyModelStatusConstants, IResourceStatus {

	/**
	 * The elements related to the failure, or <code>null</code>
	 * if no elements are involved.
	 */
	protected IRubyElement[] elements = new IRubyElement[0];
	/**
	 * The path related to the failure, or <code>null</code>
	 * if no path is involved.
	 */
	protected IPath path;
	/**
	 * The <code>String</code> related to the failure, or <code>null</code>
	 * if no <code>String</code> is involved.
	 */
	protected String string;
	/**
	 * Empty children
	 */
	protected final static IStatus[] NO_CHILDREN = new IStatus[] {};
	protected IStatus[] children= NO_CHILDREN;

	/**
	 * Singleton OK object
	 */
	public static final IRubyModelStatus VERIFIED_OK = new RubyModelStatus(OK, OK, Util.bind("status.OK")); //$NON-NLS-1$

	/**
	 * Constructs an Ruby model status with no corresponding elements.
	 */
	public RubyModelStatus() {
		// no code for an multi-status
		super(ERROR, RubyCore.PLUGIN_ID, 0, "RubyModelStatus", null); //$NON-NLS-1$
	}
	/**
	 * Constructs an Ruby model status with no corresponding elements.
	 */
	public RubyModelStatus(int code) {
		super(ERROR, RubyCore.PLUGIN_ID, code, "RubyModelStatus", null); //$NON-NLS-1$
		this.elements= RubyElement.NO_ELEMENTS;
	}
	/**
	 * Constructs an Ruby model status with the given corresponding
	 * elements.
	 */
	public RubyModelStatus(int code, IRubyElement[] elements) {
		super(ERROR, RubyCore.PLUGIN_ID, code, "RubyModelStatus", null); //$NON-NLS-1$
		this.elements= elements;
		this.path= null;
	}
	/**
	 * Constructs an Ruby model status with no corresponding elements.
	 */
	public RubyModelStatus(int code, String string) {
		this(ERROR, code, string);
	}
	/**
	 * Constructs an Ruby model status with no corresponding elements.
	 */
	public RubyModelStatus(int severity, int code, String string) {
		super(severity, RubyCore.PLUGIN_ID, code, "RubyModelStatus", null); //$NON-NLS-1$
		this.elements= RubyElement.NO_ELEMENTS;
		this.path= null;
		this.string = string;
	}	
	/**
	 * Constructs an Ruby model status with no corresponding elements.
	 */
	public RubyModelStatus(int code, Throwable throwable) {
		super(ERROR, RubyCore.PLUGIN_ID, code, "RubyModelStatus", throwable); //$NON-NLS-1$
		this.elements= RubyElement.NO_ELEMENTS;
	}
	/**
	 * Constructs an Ruby model status with no corresponding elements.
	 */
	public RubyModelStatus(int code, IPath path) {
		super(ERROR, RubyCore.PLUGIN_ID, code, "RubyModelStatus", null); //$NON-NLS-1$
		this.elements= RubyElement.NO_ELEMENTS;
		this.path= path;
	}
	/**
	 * Constructs an Ruby model status with the given corresponding
	 * element.
	 */
	public RubyModelStatus(int code, IRubyElement element) {
		this(code, new IRubyElement[]{element});
	}
	/**
	 * Constructs an Ruby model status with the given corresponding
	 * element and string
	 */
	public RubyModelStatus(int code, IRubyElement element, String string) {
		this(code, new IRubyElement[]{element});
		this.string = string;
	}
	
	/**
	 * Constructs an Ruby model status with the given corresponding
	 * element and path
	 */
	public RubyModelStatus(int code, IRubyElement element, IPath path) {
		this(code, new IRubyElement[]{element});
		this.path = path;
	}	
	/**
	 * Constructs an Ruby model status with the given corresponding
	 * element, path and string
	 */
	public RubyModelStatus(int code, IRubyElement element, IPath path, String string) {
		this(code, new IRubyElement[]{element});
		this.path = path;
		this.string = string;
	}	
	/**
	 * Constructs an Ruby model status with no corresponding elements.
	 */
	public RubyModelStatus(CoreException coreException) {
		super(ERROR, RubyCore.PLUGIN_ID, CORE_EXCEPTION, "RubyModelStatus", coreException); //$NON-NLS-1$
		elements= RubyElement.NO_ELEMENTS;
	}
	protected int getBits() {
		int severity = 1 << (getCode() % 100 / 33);
		int category = 1 << ((getCode() / 100) + 3);
		return severity | category;
	}
	/**
	 * @see IStatus
	 */
	public IStatus[] getChildren() {
		return children;
	}
	/**
	 * @see IRubyModelStatus
	 */
	public IRubyElement[] getElements() {
		return elements;
	}
	/**
	 * Returns the message that is relevant to the code of this status.
	 */
	public String getMessage() {
		Throwable exception = getException();
		if (exception == null) {
			switch (getCode()) {
				case CORE_EXCEPTION :
					return Util.bind("status.coreException"); //$NON-NLS-1$

				case BUILDER_INITIALIZATION_ERROR:
					return Util.bind("build.initializationError"); //$NON-NLS-1$

				case BUILDER_SERIALIZATION_ERROR:
					return Util.bind("build.serializationError"); //$NON-NLS-1$

				case DEVICE_PATH:
					return Util.bind("status.cannotUseDeviceOnPath", getPath().toString()); //$NON-NLS-1$

				case DOM_EXCEPTION:
					return Util.bind("status.JDOMError"); //$NON-NLS-1$

				case ELEMENT_DOES_NOT_EXIST:
					return Util.bind("element.doesNotExist",((RubyElement)elements[0]).toStringWithAncestors()); //$NON-NLS-1$

				case ELEMENT_NOT_ON_CLASSPATH:
					return Util.bind("element.notOnClasspath",((RubyElement)elements[0]).toStringWithAncestors()); //$NON-NLS-1$

				case EVALUATION_ERROR:
					return Util.bind("status.evaluationError", string); //$NON-NLS-1$

				case INDEX_OUT_OF_BOUNDS:
					return Util.bind("status.indexOutOfBounds"); //$NON-NLS-1$

				case INVALID_CONTENTS:
					return Util.bind("status.invalidContents"); //$NON-NLS-1$

				case INVALID_DESTINATION:
					return Util.bind("status.invalidDestination", ((RubyElement)elements[0]).toStringWithAncestors()); //$NON-NLS-1$

				case INVALID_ELEMENT_TYPES:
					StringBuffer buff= new StringBuffer(Util.bind("operation.notSupported")); //$NON-NLS-1$
					for (int i= 0; i < elements.length; i++) {
						if (i > 0) {
							buff.append(", "); //$NON-NLS-1$
						}
						buff.append(((RubyElement)elements[i]).toStringWithAncestors());
					}
					return buff.toString();

				case INVALID_NAME:
					return Util.bind("status.invalidName", string); //$NON-NLS-1$

				case INVALID_PACKAGE:
					return Util.bind("status.invalidPackage", string); //$NON-NLS-1$

				case INVALID_PATH:
					if (string != null) {
						return string;
					}
					return Util.bind("status.invalidPath", getPath() == null ? "null" : getPath().toString()); //$NON-NLS-1$ //$NON-NLS-2$

				case INVALID_PROJECT:
					return Util.bind("status.invalidProject", string); //$NON-NLS-1$

				case INVALID_RESOURCE:
					return Util.bind("status.invalidResource", string); //$NON-NLS-1$

				case INVALID_RESOURCE_TYPE:
					return Util.bind("status.invalidResourceType", string); //$NON-NLS-1$

				case INVALID_SIBLING:
					if (string != null) {
						return Util.bind("status.invalidSibling", string); //$NON-NLS-1$
					}
					return Util.bind("status.invalidSibling", ((RubyElement)elements[0]).toStringWithAncestors()); //$NON-NLS-1$

				case IO_EXCEPTION:
					return Util.bind("status.IOException"); //$NON-NLS-1$

				case NAME_COLLISION:
					if (string != null) {
						return string;
					}
					return Util.bind("status.nameCollision", ""); //$NON-NLS-1$ //$NON-NLS-2$
			
				case NO_ELEMENTS_TO_PROCESS:
					return Util.bind("operation.needElements"); //$NON-NLS-1$

				case NULL_NAME:
					return Util.bind("operation.needName"); //$NON-NLS-1$

				case NULL_PATH:
					return Util.bind("operation.needPath"); //$NON-NLS-1$

				case NULL_STRING:
					return Util.bind("operation.needString"); //$NON-NLS-1$

				case PATH_OUTSIDE_PROJECT:
					return Util.bind("operation.pathOutsideProject", string, ((RubyElement)elements[0]).toStringWithAncestors()); //$NON-NLS-1$

				case READ_ONLY:
					IRubyElement element = elements[0];
					String name = element.getElementName();
					return  Util.bind("status.readOnly", name); //$NON-NLS-1$

				case RELATIVE_PATH:
					return Util.bind("operation.needAbsolutePath", getPath().toString()); //$NON-NLS-1$

				case TARGET_EXCEPTION:
					return Util.bind("status.targetException"); //$NON-NLS-1$

				case UPDATE_CONFLICT:
					return Util.bind("status.updateConflict"); //$NON-NLS-1$

				case NO_LOCAL_CONTENTS :
					return Util.bind("status.noLocalContents", getPath().toString()); //$NON-NLS-1$


			case CP_VARIABLE_PATH_UNBOUND:
				IRubyProject javaProject = (IRubyProject)elements[0];
				return Util.bind("classpath.unboundVariablePath", path.makeRelative().toString(), javaProject.getElementName()); //$NON-NLS-1$
					
			case CLASSPATH_CYCLE: 
				javaProject = (IRubyProject)elements[0];
				return Util.bind("classpath.cycle", javaProject.getElementName()); //$NON-NLS-1$
												 
			case DISABLED_CP_EXCLUSION_PATTERNS:
				javaProject = (IRubyProject)elements[0];
				String projectName = javaProject.getElementName();
				IPath newPath = path;
				if (path.segment(0).toString().equals(projectName)) {
					newPath = path.removeFirstSegments(1);
				}
				return Util.bind("classpath.disabledInclusionExclusionPatterns", newPath.makeRelative().toString(), projectName); //$NON-NLS-1$

			case DISABLED_CP_MULTIPLE_OUTPUT_LOCATIONS:
				javaProject = (IRubyProject)elements[0];
				projectName = javaProject.getElementName();
				newPath = path;
				if (path.segment(0).toString().equals(projectName)) {
					newPath = path.removeFirstSegments(1);
				}
				return Util.bind("classpath.disabledMultipleOutputLocations", newPath.makeRelative().toString(), projectName); //$NON-NLS-1$
			}
			if (string != null) {
				return string;
			}
			return ""; // //$NON-NLS-1$
		}
		String message = exception.getMessage();
		if (message != null) {
			return message;
		}
		return exception.toString();
	}
	/**
	 * @see IRubyModelStatus#getPath()
	 */
	public IPath getPath() {
		return path;
	}
	/**
	 * @see IStatus#getSeverity()
	 */
	public int getSeverity() {
		if (children == NO_CHILDREN) return super.getSeverity();
		int severity = -1;
		for (int i = 0, max = children.length; i < max; i++) {
			int childrenSeverity = children[i].getSeverity();
			if (childrenSeverity > severity) {
				severity = childrenSeverity;
			}
		}
		return severity;
	}
	/**
	 * @see IRubyModelStatus#getString()
	 * @deprecated
	 */
	public String getString() {
		return string;
	}
	/**
	 * @see IRubyModelStatus#isDoesNotExist()
	 */
	public boolean isDoesNotExist() {
		int code = getCode();
		return code == ELEMENT_DOES_NOT_EXIST || code == ELEMENT_NOT_ON_CLASSPATH;
	}
	/**
	 * @see IStatus#isMultiStatus()
	 */
	public boolean isMultiStatus() {
		return children != NO_CHILDREN;
	}
	/**
	 * @see IStatus#isOK()
	 */
	public boolean isOK() {
		return getCode() == OK;
	}
	/**
	 * @see IStatus#matches(int)
	 */
	public boolean matches(int mask) {
		if (! isMultiStatus()) {
			return matches(this, mask);
		}
		for (int i = 0, max = children.length; i < max; i++) {
			if (matches((RubyModelStatus) children[i], mask))
				return true;
		}
		return false;
	}
	/**
	 * Helper for matches(int).
	 */
	protected boolean matches(RubyModelStatus status, int mask) {
		int severityMask = mask & 0x7;
		int categoryMask = mask & ~0x7;
		int bits = status.getBits();
		return ((severityMask == 0) || (bits & severityMask) != 0) && ((categoryMask == 0) || (bits & categoryMask) != 0);
	}
	/**
	 * Creates and returns a new <code>IRubyModelStatus</code> that is a
	 * a multi-status status.
	 *
	 * @see IStatus#isMultiStatus()
	 */
	public static IRubyModelStatus newMultiStatus(IRubyModelStatus[] children) {
		RubyModelStatus jms = new RubyModelStatus();
		jms.children = children;
		return jms;
	}
	/**
	 * Returns a printable representation of this exception for debugging
	 * purposes.
	 */
	public String toString() {
		if (this == VERIFIED_OK){
			return "RubyModelStatus[OK]"; //$NON-NLS-1$
		}
		StringBuffer buffer = new StringBuffer();
		buffer.append("Ruby Model Status ["); //$NON-NLS-1$
		buffer.append(getMessage());
		buffer.append("]"); //$NON-NLS-1$
		return buffer.toString();
	}
}
