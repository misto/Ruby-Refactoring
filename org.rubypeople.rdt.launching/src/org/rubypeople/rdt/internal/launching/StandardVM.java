package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.launching.AbstractVMInstall;
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
            rubyCmd.add(this.getCommand());
            rubyCmd.addAll(args);
            return fCommandExecutor.exec((String[]) rubyCmd.toArray(new String[] {}), workingDirectory);
		} catch (IOException e) {
            IStatus errorStatus = new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.OK, 
                    "Unable to execute interpreter: " + args + workingDirectory, e);
            throw new CoreException(errorStatus) ;
		}
		catch (IllegalCommandException e) {
			IStatus errorStatus = new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.OK, e.getMessage(), e);
			throw new CoreException(errorStatus) ;
		}
	}

	public String getCommand() throws IllegalCommandException {
		// TODO Auto-generated method stub
		return null;
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
