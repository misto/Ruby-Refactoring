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

	/**
	 * 
	 * @return
	 * @throws IllegalCommandException
	 * @deprecated 
	 */
	public String getCommand() throws IllegalCommandException;

	/**
	 * 
	 * @param commandLine
	 * @param workingDirectory
	 * @return
	 * @throws CoreException
	 * @deprecated This doesn't match JDT behavior. We'll need to work on a better solution.
	 */
	public Process exec(List commandLine, File workingDirectory) throws CoreException;

	public IPath[] getLibraryLocations();

	public String getId();

	public IInterpreterInstallType getVMInstallType();

	public void setLibraryLocations(IPath[] paths);

	public String[] getVMArguments();

	public void setVMArguments(String[] vmArgs);
	
	public IVMRunner getVMRunner(String mode);
}