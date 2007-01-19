package org.rubypeople.rdt.internal.launching;

import java.io.File;

import org.eclipse.debug.core.ILaunchManager;
import org.rubypeople.rdt.launching.AbstractVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.IVMRunner;

public class StandardVM extends AbstractVMInstall {

	public StandardVM(IVMInstallType type, String id) {
		super(type, id);
	}
	
	@Override
	public IVMRunner getVMRunner(String mode) {
		if (ILaunchManager.RUN_MODE.equals(mode)) {
			return new StandardVMRunner(this);
		} else if (ILaunchManager.DEBUG_MODE.equals(mode)) {
			return new StandardVMDebugger(this);
		}
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
