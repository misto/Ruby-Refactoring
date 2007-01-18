/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.launching;


import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.internal.launching.RdtLaunchingMessages;
import org.rubypeople.rdt.internal.launching.RdtLaunchingPlugin;
/**
 * Abstract implementation of a Interpreter install.
 * <p>
 * Clients implementing Interpreter installs must subclass this class.
 * </p>
 */
public abstract class AbstractInterpreter implements IVMInstall, IVMInstall2 {

	private IVMInstallType fType;
	private String fId;
	private String fName;
	private File fInstallLocation;
	private IPath[] fSystemLibraryDescriptions;
	private String fInterpreterArgs;
	// system properties are cached in user preferences prefixed with this key, followed
	// by vm type, vm id, and system property name
	private static final String PREF_Interpreter_INSTALL_SYSTEM_PROPERTY = "PREF_Interpreter_INSTALL_SYSTEM_PROPERTY"; //$NON-NLS-1$
	// whether change events should be fired
	private boolean fNotify = true;
	
	/**
	 * Constructs a new Interpreter install.
	 * 
	 * @param	type	The type of this Interpreter install.
	 * 					Must not be <code>null</code>
	 * @param	id		The unique identifier of this Interpreter instance
	 * 					Must not be <code>null</code>.
	 * @throws	IllegalArgumentException	if any of the required
	 * 					parameters are <code>null</code>.
	 */
	public AbstractInterpreter(IVMInstallType type, String id) {
		if (type == null)
			throw new IllegalArgumentException(RdtLaunchingMessages.vmInstall_assert_typeNotNull); 
		if (id == null)
			throw new IllegalArgumentException(RdtLaunchingMessages.vmInstall_assert_idNotNull); 
		fType= type;
		fId= id;
	}

	/* (non-Rubydoc)
	 * Subclasses should not override this method.
	 * @see IInterpreterInstall#getId()
	 */
	public String getId() {
		return fId;
	}

	/* (non-Rubydoc)
	 * Subclasses should not override this method.
	 * @see IInterpreterInstall#getName()
	 */
	public String getName() {
		return fName;
	}

	/* (non-Rubydoc)
	 * Subclasses should not override this method.
	 * @see IInterpreterInstall#setName(String)
	 */
	public void setName(String name) {
		if (!name.equals(fName)) {
			PropertyChangeEvent event = new PropertyChangeEvent(this, IVMInstallChangedListener.PROPERTY_NAME, fName, name);
			fName= name;
			if (fNotify) {
				RubyRuntime.fireInterpreterChanged(event);
			}
		}
	}

	/* (non-Rubydoc)
	 * Subclasses should not override this method.
	 * @see IInterpreterInstall#getInstallLocation()
	 */
	public File getInstallLocation() {
		return fInstallLocation;
	}

	/* (non-Rubydoc)
	 * Subclasses should not override this method.
	 * @see IInterpreterInstall#setInstallLocation(File)
	 */
	public void setInstallLocation(File installLocation) {
		if (!installLocation.equals(fInstallLocation)) {
			PropertyChangeEvent event = new PropertyChangeEvent(this, IVMInstallChangedListener.PROPERTY_INSTALL_LOCATION, fInstallLocation, installLocation);
			fInstallLocation= installLocation;
			if (fNotify) {
				RubyRuntime.fireInterpreterChanged(event);
			}
		}
	}

	/* (non-Rubydoc)
	 * Subclasses should not override this method.
	 * @see IInterpreterInstall#getInterpreterInstallType()
	 */
	public IVMInstallType getVMInstallType() {
		return fType;
	}

	/* (non-Rubydoc)
	 * @see IInterpreter#getInterpreterRunner(String)
	 */
	public IVMRunner getVMRunner(String mode) {
		return null;
	}

	/* (non-Rubydoc)
	 * @see org.rubypeople.rdt.launching.IInterpreter#getLibraryLocations()
	 */
	public IPath[] getLibraryLocations() {
		return fSystemLibraryDescriptions;
	}

	/* (non-Rubydoc)
	 * @see org.eclipse.jdt.launching.IInterpreterInstall#setLibraryLocations(org.eclipse.jdt.launching.LibraryLocation[])
	 */
	public void setLibraryLocations(IPath[] locations) {
		if (locations == fSystemLibraryDescriptions) {
			return;
		}
		IPath[] newLocations = locations;
		if (newLocations == null) {
			newLocations = getVMInstallType().getDefaultLibraryLocations(getInstallLocation()); 
		}
		IPath[] prevLocations = fSystemLibraryDescriptions;
		if (prevLocations == null) {
			prevLocations = getVMInstallType().getDefaultLibraryLocations(getInstallLocation()); 
		}
		
		if (newLocations.length == prevLocations.length) {
			int i = 0;
			boolean equal = true;
			while (i < newLocations.length && equal) {
				equal = newLocations[i].equals(prevLocations[i]);
				i++;
			}
			if (equal) {
				// no change
				return;
			}
		}

		PropertyChangeEvent event = new PropertyChangeEvent(this, IVMInstallChangedListener.PROPERTY_LIBRARY_LOCATIONS, prevLocations, newLocations);
		fSystemLibraryDescriptions = locations;
		if (fNotify) {
			RubyRuntime.fireInterpreterChanged(event);		
		}
	}

	/**
	 * Whether this Interpreter should fire property change notifications.
	 * 
	 * @param notify
	 * @since 2.1
	 */
	protected void setNotify(boolean notify) {
		fNotify = notify;
	}

	/* (non-Rubydoc)
	 * @see java.lang.Object#equals(java.lang.Object)
     * @since 2.1
	 */
	public boolean equals(Object object) {
		if (object instanceof IVMInstall) {
			IVMInstall vm = (IVMInstall)object;
			return getVMInstallType().equals(vm.getVMInstallType()) &&
				getId().equals(vm.getId());
		}
		return false;
	}

	/* (non-Rubydoc)
	 * @see java.lang.Object#hashCode()
	 * @since 2.1
	 */
	public int hashCode() {
		return getVMInstallType().hashCode() + getId().hashCode();
	}
	
	/* (non-Rubydoc)
	 * @see org.rubypeople.rdt.launching.IInterpreter#getDefaultInterpreterArguments()
	 * @since 3.0
	 */
	public String[] getVMArguments() {
		String args = getInterpreterArgs();
		if (args == null) {
		    return null;
		}
		ExecutionArguments ex = new ExecutionArguments(args, ""); //$NON-NLS-1$
		return ex.getVMArgumentsArray();
	}
	
	/* (non-Rubydoc)
	 * @see org.rubypeople.rdt.launching.IInterpreter#setDefaultInterpreterArguments(java.lang.String[])
	 * @since 3.0
	 */
	public void setVMArguments(String[] vmArgs) {
		if (vmArgs == null) {
			setVMArgs(null);
		} else {
		    StringBuffer buf = new StringBuffer();
		    for (int i = 0; i < vmArgs.length; i++) {
	            String string = vmArgs[i];
	            buf.append(string);
	            buf.append(" "); //$NON-NLS-1$
	        }
			setVMArgs(buf.toString().trim());
		}
	}
	
    /* (non-Rubydoc)
     * @see org.eclipse.jdt.launching.IInterpreterInstall2#getInterpreterArgs()
     */
    public String getInterpreterArgs() {
        return fInterpreterArgs;
    }
    
    /* (non-Rubydoc)
     * @see org.rubypeople.rdt.launching.IInterpreter2#setInterpreterArgs(java.lang.String)
     */
    public void setVMArgs(String vmArgs) {
        if (fInterpreterArgs == null) {
            if (vmArgs == null) {
                // No change
                return;
            }
        } else if (fInterpreterArgs.equals(vmArgs)) {
    		// No change
    		return;
    	}
        PropertyChangeEvent event = new PropertyChangeEvent(this, IVMInstallChangedListener.PROPERTY_VM_ARGUMENTS, fInterpreterArgs, vmArgs);
        fInterpreterArgs = vmArgs;
		if (fNotify) {
			RubyRuntime.fireInterpreterChanged(event);		
		}
    }	
    
    /* (non-Rubydoc)
     * Subclasses should override.
     * @see org.rubypeople.rdt.launching.IInterpreter2#getRubyVersion()
     */
    public String getRubyVersion() {
        return null;
    }
    		
	/**
	 * Throws a core exception with an error status object built from the given
	 * message, lower level exception, and error code.
	 * 
	 * @param message the status message
	 * @param exception lower level exception associated with the error, or
	 *            <code>null</code> if none
	 * @param code error code
	 * @throws CoreException the "abort" core exception
	 * @since 3.2
	 */
	protected void abort(String message, Throwable exception, int code) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, RdtLaunchingPlugin
				.getUniqueIdentifier(), code, message, exception));
	}	
    
}
