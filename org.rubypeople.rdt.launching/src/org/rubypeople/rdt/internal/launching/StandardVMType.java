package org.rubypeople.rdt.internal.launching;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
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
		LibraryInfo info = getLibraryInfo(installLocation, rubyExecutable);
		String[] loadpath = info.getBootpath();
		IPath[] paths = new IPath[loadpath.length];
		for (int i = 0; i < loadpath.length; i++) {
			paths[i] = new Path(loadpath[i]);
		}
//		String stdPath = installLocation.getAbsolutePath() + fgSeparator + "lib" + fgSeparator + "ruby" + fgSeparator + "1.8";
//		String sitePath = installLocation.getAbsolutePath() + fgSeparator + "lib" + fgSeparator + "ruby" + fgSeparator + "site_ruby" + fgSeparator + "1.8";
//		IPath[] paths = new IPath[2];
//		paths[0] = new Path(stdPath);
//		paths[1] = new Path(sitePath);
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
	
	private LibraryInfo generateLibraryInfo(File rubyHome, File rubyExecutable) {
		LibraryInfo info = null;		
		//locate the script to grab us our loadpaths
		File file = LaunchingPlugin.getFileInPlugin(new Path("ruby/loadpath.rb")); //$NON-NLS-1$
		if (file.exists()) {	
			String javaExecutablePath = rubyExecutable.getAbsolutePath();
			String[] cmdLine = new String[] {javaExecutablePath, file.getAbsolutePath()};  //$NON-NLS-1$
			Process p = null;
			try {
				p = Runtime.getRuntime().exec(cmdLine);
				IProcess process = DebugPlugin.newProcess(new Launch(null, ILaunchManager.RUN_MODE, null), p, "Library Detection"); //$NON-NLS-1$
				for (int i= 0; i < 200; i++) {
					// Wait no more than 10 seconds (200 * 50 mils)
					if (process.isTerminated()) {
						break;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
					}
				}
				info = parseLibraryInfo(process);
			} catch (IOException ioe) {
				LaunchingPlugin.log(ioe);
			} finally {
				if (p != null) {
					p.destroy();
				}
			}
		}
		if (info == null) {
		    // log error that we were unable to generate library info - see bug 70011
		    LaunchingPlugin.log(MessageFormat.format("Failed to retrieve default libraries for {0}", new String[]{rubyHome.getAbsolutePath()})); //$NON-NLS-1$
		}
		return info;
	}
	
	/**
	 * Parses the output from 'LibraryDetector'.
	 */
	protected LibraryInfo parseLibraryInfo(IProcess process) {
		IStreamsProxy streamsProxy = process.getStreamsProxy();
		String text = null;
		if (streamsProxy != null) {
			text = streamsProxy.getOutputStreamMonitor().getContents();
		}
		BufferedReader reader = new BufferedReader(new StringReader(text));
		List<String> lines = new ArrayList<String>();
		try {
			String line = null;
			while ((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (lines.size() > 0) {
			String version = lines.remove(0);
		    if (lines.size() > 0) {
		    	String[] loadpath = (String[]) lines.toArray(new String[lines.size()]);
		    	return new LibraryInfo(version, loadpath);		
		    }
		}
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
		return new LibraryInfo("1.8.4", new String[] {rtjar.toOSString()});		 //$NON-NLS-1$
	}
	
	/**
	 * Return an <code>IPath</code> corresponding to the single library file containing the
	 * standard Ruby classes for VMs version 1.8.x.
	 */
	protected IPath getDefaultSystemLibrary(File rubyHome) {
		return new Path(rubyHome.getPath()).append("lib").append("ruby").append("1.8"); //$NON-NLS-2$ //$NON-NLS-1$ //$NON-NLS-3$
	}

}
