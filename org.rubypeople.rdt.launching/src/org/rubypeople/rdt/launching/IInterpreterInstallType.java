package org.rubypeople.rdt.launching;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

public interface IInterpreterInstallType {

	IVMInstall findInterpreterInstallByName(String vmName);

	String getId();

	IVMInstall[] getInterpreterInstalls();

	IStatus validateInstallLocation(File installLocation);

	IVMInstall findInterpreterInstall(String id);

	IPath[] getDefaultLibraryLocations(File installLocation);

	String getName();

}
