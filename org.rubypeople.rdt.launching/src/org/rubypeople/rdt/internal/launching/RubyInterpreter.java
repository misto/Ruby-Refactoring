package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.IVMRunner;

public class RubyInterpreter implements IVMInstall {
	public static final String END_OF_OPTIONS_DELIMITER = "--";

	protected File installLocation;
	protected String name;

    private final CommandExecutor commandExecutor;

	public RubyInterpreter(String aName, File validInstallLocation) {
        this(aName, validInstallLocation, new StandardCommandExecutor());
	}

	public RubyInterpreter(String aName, File validInstallLocation, CommandExecutor commandExecutor) {
	    name = aName;
	    installLocation = validInstallLocation;
        this.commandExecutor = commandExecutor;
    }

    /* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.launching.IInterpreter#getInstallLocation()
	 */
    public File getInstallLocation() {
		return installLocation;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.launching.IInterpreter#getName()
	 */
	public String getName() {
		return name;
	}
	
	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.launching.IInterpreter#setName(java.lang.String)
	 */
	public void setName(String newName) {
		name = newName;
	}
	
	public String getCommand() throws IllegalCommandException {
		if( installLocation.isFile() ){
			return installLocation.getAbsolutePath();
		}
		String errorMessage = MessageFormat.format(RdtLaunchingMessages.RdtLaunchingPlugin_interpreterNotFound, new Object[] {this.getName()}) ;
		throw new IllegalCommandException(errorMessage) ;
	}
	
	public Process exec(List args, File workingDirectory) throws CoreException {
		try {
			LaunchingPlugin.debug("Launching: " + args) ;
			LaunchingPlugin.debug("Working Dir: " + workingDirectory) ;
            List rubyCmd = new ArrayList();
            rubyCmd.add(this.getCommand());
            rubyCmd.addAll(args);
            return commandExecutor.exec((String[]) rubyCmd.toArray(new String[] {}), workingDirectory);
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
	
	public boolean equals(Object other) {
		if (other instanceof RubyInterpreter) {
			IVMInstall otherInterpreter = (IVMInstall) other;
			if (name.equals(otherInterpreter.getName()))
				return installLocation.equals(otherInterpreter.getInstallLocation());
		}
		
		return false;
	}

	public String getId() {
		// TODO Auto-generated method stub
		return null;
	}

	public String[] getVMArguments() {
		// TODO Auto-generated method stub
		return null;
	}

	public IVMInstallType getVMInstallType() {
		// TODO Auto-generated method stub
		return null;
	}

	public IVMRunner getVMRunner(String mode) {
		// TODO Auto-generated method stub
		return null;
	}

	public IPath[] getLibraryLocations() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setInstallLocation(File validInstallLocation) {
		this.installLocation = validInstallLocation;		
	}

	public void setVMArguments(String[] vmArgs) {
		// TODO Auto-generated method stub
		
	}

	public void setLibraryLocations(IPath[] paths) {
		// TODO Auto-generated method stub
		
	}
}
