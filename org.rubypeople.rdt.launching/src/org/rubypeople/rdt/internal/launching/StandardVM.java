package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.launching.AbstractVMInstall;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IVMInstallType;

public class StandardVM extends AbstractVMInstall {

	private CommandExecutor fCommandExecutor;

	public StandardVM(IVMInstallType type, String id) {
		super(type, id);
		fCommandExecutor = new StandardCommandExecutor();
	}

	public Process exec(List args, File workingDirectory)
			throws CoreException {
		try {
			LaunchingPlugin.debug("Launching: " + args) ;
			LaunchingPlugin.debug("Working Dir: " + workingDirectory) ;
            List rubyCmd = new ArrayList();
            rubyCmd.add(getCommand());
            rubyCmd.addAll(args);
            return fCommandExecutor.exec((String[]) rubyCmd.toArray(new String[] {}), workingDirectory);
		} catch (IOException e) {
            IStatus errorStatus = new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.OK, 
                    "Unable to execute interpreter: " + args + workingDirectory, e);
            throw new CoreException(errorStatus) ;
		}
	}

	// FIXME This stuff should be in the VMRunner like in JDT!
	public String getCommand() throws CoreException {
//		 Look for the user-specified ruby executable command
		String command= null;
//		Map map= config.getVMSpecificAttributesMap();
//		if (map != null) {
//			command = (String)map.get(IRubyLaunchConfigurationConstants.ATTR_RUBY_COMMAND);
//		}
		
		// If no ruby command was specified, use default executable
		if (command == null) {
			File exe = StandardVMType.findRubyExecutable(getInstallLocation());
			if (exe == null) {
				abort(MessageFormat.format(LaunchingMessages.StandardVMRunner_Unable_to_locate_executable_for__0__1, new String[]{getName()}), null, IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR); 
			}
			return exe.getAbsolutePath();
		}
				
		// Build the path to the ruby executable.  First try 'bin'
		String installLocation = getInstallLocation().getAbsolutePath() + File.separatorChar;
		File exe = new File(installLocation + "bin" + File.separatorChar + command); //$NON-NLS-1$ 		
		if (fileExists(exe)){
			return exe.getAbsolutePath();
		}
		exe = new File(exe.getAbsolutePath() + ".exe"); //$NON-NLS-1$
		if (fileExists(exe)){
			return exe.getAbsolutePath();
		}
		
		// not found
		abort(MessageFormat.format(LaunchingMessages.StandardVMRunner_Specified_executable__0__does_not_exist_for__1__4, new String[]{command, getName()}), null, IRubyLaunchConfigurationConstants.ERR_INTERNAL_ERROR); 
		// NOTE: an exception will be thrown - null cannot be returned
		return null;		
	}
	
	protected boolean fileExists(File file) {
		return file.exists() && file.isFile();
	}

	public String getRubyVersion() {
		 StandardVMType installType = (StandardVMType) getVMInstallType();
	        File installLocation = getInstallLocation();
	        if (installLocation != null) {
	            File executable = StandardVMType.findRubyExecutable(installLocation);
	            if (executable != null) {
	                String vmVersion = installType.getVMVersion(installLocation, executable);
	                // strip off extra info
	                StringBuffer version = new StringBuffer();
	                for (int i = 0; i < vmVersion.length(); i++) {
	                    char ch = vmVersion.charAt(i);
	                    if (Character.isDigit(ch) || ch == '.') {
	                        version.append(ch);
	                    } else {
	                        break;
	                    }
	                }
	                if (version.length() > 0) {
	                    return version.toString();
	                }
	            }
	        }
	        return null;
	}

}
