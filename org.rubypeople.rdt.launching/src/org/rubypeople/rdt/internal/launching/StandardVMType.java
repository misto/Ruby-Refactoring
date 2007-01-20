package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.service.environment.Constants;
import org.rubypeople.rdt.launching.AbstractVMInstallType;
import org.rubypeople.rdt.launching.IVMInstall;

public class StandardVMType extends AbstractVMInstallType {

	/**
	 * Map of the install path for which we were unable to generate
	 * the library info during this session.
	 */
	private static Map fgFailedInstallPath= new HashMap();
	
	/**
	 * Convenience handle to the system-specific file separator character
	 */															
	private static final char fgSeparator = File.separatorChar;

	/**
	 * The list of locations in which to look for the java executable in candidate
	 * VM install locations, relative to the VM install location.
	 */
	private static final String[] fgCandidateRubyFiles = {"rubyw", "rubyw.exe", "ruby", "ruby.exe"}; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
	private static final String[] fgCandidateRubyLocations = {"bin" + fgSeparator}; //$NON-NLS-1$ 
	
	
	@Override
	protected IVMInstall doCreateVMInstall(String id) {
		return new StandardVM(this, id);
	}

	public IPath[] getDefaultLibraryLocations(File installLocation) {
		File rubyExecutable = findRubyExecutable(installLocation);
		// FIXME This is a big hack and is tailored to the one click installer on windows!
		String rubyPath = rubyExecutable.getParentFile().getAbsolutePath();
		String stdPath = rubyPath + fgSeparator + "lib" + fgSeparator + "ruby" + fgSeparator + "1.8";
		String sitePath = rubyPath + fgSeparator + "lib" + fgSeparator + "ruby" + fgSeparator + "site_ruby";
		IPath[] paths = new IPath[2];
		paths[0] = new Path(stdPath);
		paths[0] = new Path(sitePath);
		return paths;
	}

	public String getName() {
		return LaunchingMessages.StandardVMType_Standard_VM_3; 
	}

	public IStatus validateInstallLocation(File rubyHome) {
		IStatus status = null;
		File rubyExecutable = findRubyExecutable(rubyHome);
		if (rubyExecutable == null) {
			status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), 0, LaunchingMessages.StandardVMType_Not_a_JDK_Root__Java_executable_was_not_found_1, null);						
		} else {
			if (canDetectDefaultSystemLibraries(rubyHome, rubyExecutable)) {
				status = new Status(IStatus.OK, LaunchingPlugin.getUniqueIdentifier(), 0, LaunchingMessages.StandardVMType_ok_2, null); 
			} else {
				status = new Status(IStatus.ERROR, LaunchingPlugin.getUniqueIdentifier(), 0, LaunchingMessages.StandardVMType_Not_a_JDK_root__System_library_was_not_found__1, null); 
			}
		}
		return status;		
	}
	
	/**
	 * Starting in the specified VM install location, attempt to find the 'ruby' executable
	 * file.  If found, return the corresponding <code>File</code> object, otherwise return
	 * <code>null</code>.
	 */
	public static File findRubyExecutable(File vmInstallLocation) {
		// Try each candidate in order.  The first one found wins.  Thus, the order
		// of fgCandidateJavaLocations and fgCandidateJavaFiles is significant.
		for (int i = 0; i < fgCandidateRubyFiles.length; i++) {
			for (int j = 0; j < fgCandidateRubyLocations.length; j++) {
				File javaFile = new File(vmInstallLocation, fgCandidateRubyLocations[j] + fgCandidateRubyFiles[i]);
				if (javaFile.isFile()) {
					return javaFile;
				}				
			}
		}		
		return null;							
	}
	
	/**
	 * Return <code>true</code> if the appropriate system libraries can be found for the
	 * specified ruby executable, <code>false</code> otherwise.
	 */
	protected boolean canDetectDefaultSystemLibraries(File rubyHome, File rubyExecutable) {
		IPath[] locations = getDefaultLibraryLocations(rubyHome);
		return locations.length > 0; 
	}

	public String getVMVersion(File installLocation, File executable) {
		LibraryInfo info = getLibraryInfo(installLocation, executable);
		return info.getVersion();
	}

	/**
	 * Return library information corresponding to the specified install
	 * location. If the info does not exist, create it using the given Java
	 * executable.
	 */
	protected synchronized LibraryInfo getLibraryInfo(File rubyHome, File rubyExecutable) {
		
		// See if we already know the info for the requested VM.  If not, generate it.
		String installPath = rubyHome.getAbsolutePath();
		LibraryInfo info = LaunchingPlugin.getLibraryInfo(installPath);
		if (info == null) {
			info= (LibraryInfo)fgFailedInstallPath.get(installPath);
			if (info == null) {
				info = generateLibraryInfo(rubyHome, rubyExecutable);
				if (info == null) {
					info = getDefaultLibraryInfo(rubyHome);
					fgFailedInstallPath.put(installPath, info);
				} else {
				    // only persist if we were able to generate info - see bug 70011
				    LaunchingPlugin.setLibraryInfo(installPath, info);
				}
			}
		} 
		return info;
	}	
	
	private LibraryInfo generateLibraryInfo(
			File javaHome, File javaExecutable) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns default library info for the given install location.
	 * 
	 * @param installLocation
	 * @return LibraryInfo
	 */
	protected LibraryInfo getDefaultLibraryInfo(File installLocation) {
		IPath rtjar = getDefaultSystemLibrary(installLocation);
		File extDir = null;
		File endDir = null;
		String[] dirs = null;
		if (extDir == null) {
			dirs = new String[0];
		} else {
			dirs = new String[] {extDir.getAbsolutePath()};
		}
		String[] endDirs = null;
		if (endDir == null) {
			endDirs = new String[0]; 
		} else {
			endDirs = new String[] {endDir.getAbsolutePath()};
		}
		return new LibraryInfo("1.8.4", new String[] {rtjar.toOSString()}, dirs, endDirs);		 //$NON-NLS-1$
	}
	
	/**
	 * Return an <code>IPath</code> corresponding to the single library file containing the
	 * standard Ruby classes for VMs version 1.8.x.
	 */
	protected IPath getDefaultSystemLibrary(File rubyHome) {
		return new Path(rubyHome.getPath()).append("lib").append("ruby").append("1.8"); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-3$
	}

}
