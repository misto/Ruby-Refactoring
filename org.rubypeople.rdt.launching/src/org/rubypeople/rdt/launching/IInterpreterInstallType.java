package org.rubypeople.rdt.launching;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

public interface IInterpreterInstallType {

	IInterpreter findInterpreterInstallByName(String vmName);

	String getId();

	IInterpreter[] getInterpreterInstalls();

	IStatus validateInstallLocation(File installLocation);

	IInterpreter findInterpreterInstall(String id);

	IPath[] getDefaultLibraryLocations(File installLocation);

}
