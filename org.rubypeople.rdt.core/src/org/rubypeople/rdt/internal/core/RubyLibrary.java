/*
 * Created on Feb 17, 2004
 * 
 * $Id$
 * Copyright (c) 2003 by Xerox Corporation.  All rights reserved.
 * 
 * @author Chris Williams
 */
package org.rubypeople.rdt.internal.core;

import org.eclipse.core.runtime.IPath;

/**
 * RubyLibrary
 * @author CAWilliams
 *
 */
public class RubyLibrary {
	protected IPath installLocation;
	protected String name;

	public RubyLibrary(String aName, IPath validInstallLocation) {
		name = aName;
		installLocation = validInstallLocation;
	}

	public IPath getInstallLocation() {
		return installLocation;
	}

	public void setInstallLocation(IPath validInstallLocation) {
		installLocation = validInstallLocation;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String newName) {
		name = newName;
	}
	
	public boolean equals(Object other) {
		if (other instanceof RubyLibrary) {
			RubyLibrary otherInterpreter = (RubyLibrary) other;
			if (name.equals(otherInterpreter.getName()))
				return installLocation.equals(otherInterpreter.getInstallLocation());
		}
		return false;
	}
}
