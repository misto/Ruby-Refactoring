package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IInterpreterInstallType;
import org.rubypeople.rdt.launching.IVMRunner;

public class VMStandin implements IVMInstall {

	public VMStandin(IInterpreterInstallType vmType, String id) {
		// TODO Auto-generated constructor stub
	}

	public Process exec(List commandLine, File workingDirectory)
			throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getCommand() throws IllegalCommandException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public File getInstallLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	public IInterpreterInstallType getVMInstallType() {
		// TODO Auto-generated method stub
		return null;
	}

	public IPath[] getLibraryLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getVMArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setInstallLocation(File installLocation) {
		// TODO Auto-generated method stub

	}

	public void setLibraryLocations(IPath[] paths) {
		// TODO Auto-generated method stub

	}

	public void setName(String newName) {
		// TODO Auto-generated method stub

	}

	public void convertToRealVM() {
		// TODO Auto-generated method stub
		
	}

	public IVMRunner getVMRunner(String mode) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setVMArguments(String[] vmArgs) {
		// TODO Auto-generated method stub
		
	}

	public void setVMArgs(String vmArgs) {
		// TODO Auto-generated method stub
		
	}

}
