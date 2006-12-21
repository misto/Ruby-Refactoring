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
import org.rubypeople.rdt.launching.IInterpreter;

public class RubyInterpreter implements IInterpreter {
	public static final String END_OF_OPTIONS_DELIMITER = "--";

	protected IPath installLocation;
	protected String name;

    private final CommandExecutor commandExecutor;

	public RubyInterpreter(String aName, IPath validInstallLocation) {
        this(aName, validInstallLocation, new StandardCommandExecutor());
	}

	public RubyInterpreter(String aName, IPath validInstallLocation, CommandExecutor commandExecutor) {
	    name = aName;
	    installLocation = validInstallLocation;
        this.commandExecutor = commandExecutor;
    }

    /* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.launching.IInterpreter#getInstallLocation()
	 */
    public IPath getInstallLocation() {
		return installLocation;
	}

	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.launching.IInterpreter#setInstallLocation(org.eclipse.core.runtime.IPath)
	 */
	public void setInstallLocation(IPath validInstallLocation) {
		installLocation = validInstallLocation;
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
		if( new File(installLocation.toOSString()).isFile() ){
			return installLocation.toOSString();
		}
		String errorMessage = MessageFormat.format(RdtLaunchingMessages.getString("RdtLaunchingPlugin.interpreterNotFound"), new Object[] {this.getName()}) ;
		throw new IllegalCommandException(errorMessage) ;
	}
	
	public Process exec(List args, File workingDirectory) throws CoreException {
		try {
			RdtLaunchingPlugin.debug("Launching: " + args) ;
			RdtLaunchingPlugin.debug("Working Dir: " + workingDirectory) ;
            List rubyCmd = new ArrayList();
            rubyCmd.add(this.getCommand());
            rubyCmd.addAll(args);
            return commandExecutor.exec((String[]) rubyCmd.toArray(new String[] {}), workingDirectory);
		} catch (IOException e) {
            IStatus errorStatus = new Status(IStatus.ERROR, RdtLaunchingPlugin.PLUGIN_ID, IStatus.OK, 
                    "Unable to execute interpreter: " + args + workingDirectory, e);
            throw new CoreException(errorStatus) ;
		}
		catch (IllegalCommandException e) {
			IStatus errorStatus = new Status(IStatus.ERROR, RdtLaunchingPlugin.PLUGIN_ID, IStatus.OK, e.getMessage(), e);
			throw new CoreException(errorStatus) ;
		}

	}
	
	public boolean equals(Object other) {
		if (other instanceof RubyInterpreter) {
			IInterpreter otherInterpreter = (IInterpreter) other;
			if (name.equals(otherInterpreter.getName()))
				return installLocation.equals(otherInterpreter.getInstallLocation());
		}
		
		return false;
	}
}
