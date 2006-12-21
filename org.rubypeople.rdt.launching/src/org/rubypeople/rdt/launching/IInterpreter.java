package org.rubypeople.rdt.launching;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.internal.launching.IllegalCommandException;

public interface IInterpreter {

	public IPath getInstallLocation();

	public void setInstallLocation(IPath validInstallLocation);

	public String getName();

	public void setName(String newName);

	public String getCommand() throws IllegalCommandException;

	public Process exec(List commandLine, File workingDirectory) throws CoreException;

}