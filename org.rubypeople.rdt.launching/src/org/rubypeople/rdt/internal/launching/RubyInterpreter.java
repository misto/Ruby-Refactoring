package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;

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
		String directory = installLocation.toOSString() + File.separator;
		if (new File(directory + "rubyw.exe").isFile())
			return directory + "rubyw.exe";

		if (new File(directory, "ruby").isFile())
			return directory + "ruby";
			
		return null;
	}
	
	public Process exec(String arguments, File workingDirectory) throws IOException {
		return Runtime.getRuntime().exec(this.getCommand() + " " +  arguments, null, workingDirectory);
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
