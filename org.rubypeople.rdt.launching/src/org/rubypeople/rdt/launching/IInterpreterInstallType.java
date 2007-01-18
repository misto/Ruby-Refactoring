package org.rubypeople.rdt.launching;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;

public interface IInterpreterInstallType {

	IVMInstall findVMInstallByName(String vmName);

	String getId();

	IVMInstall[] getVMInstalls();

	IStatus validateInstallLocation(File installLocation);

	IVMInstall findVMInstall(String id);

	IPath[] getDefaultLibraryLocations(File installLocation);

	String getName();

}
