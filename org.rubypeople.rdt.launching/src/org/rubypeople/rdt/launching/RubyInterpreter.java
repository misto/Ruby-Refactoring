package org.rubypeople.rdt.launching;

import java.io.File;

import org.eclipse.core.runtime.IPath;

public class RubyInterpreter {
	public final String endOfOptionsDelimeter = " -- ";

	protected IPath installLocation;
	protected String name;

	public RubyInterpreter(String aName, IPath validInstallLocation) {
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
	
	public String getCommand() {
		return installLocation.toOSString() + File.separator + "bin" + File.separator + "rubyw.exe";
	}
	
	public boolean equals(Object other) {
		if (other instanceof RubyInterpreter) {
			RubyInterpreter otherInterpreter = (RubyInterpreter) other;
			if (name.equals(otherInterpreter.getName()))
				return installLocation.equals(otherInterpreter.getInstallLocation());
		}
		
		return false;
	}
}
