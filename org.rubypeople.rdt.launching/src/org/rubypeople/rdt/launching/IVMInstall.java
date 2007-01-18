package org.rubypeople.rdt.launching;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.internal.launching.IllegalCommandException;

public interface IVMInstall {

	public File getInstallLocation();

	public void setInstallLocation(File validInstallLocation);

	public String getName();

	public void setName(String newName);

	public String getCommand() throws IllegalCommandException;

	public Process exec(List commandLine, File workingDirectory) throws CoreException;

	public IPath[] getLibraryLocations();

	public String getId();

	public IInterpreterInstallType getInterpreterInstallType();

	public void setLibraryLocations(IPath[] paths);

	public String[] getInterpreterArguments();

	public void setInterpreterArguments(String[] vmArgs);
	
	public IVMRunner getInterpreterRunner(String mode);
}