package org.rubypeople.rdt.launching;

import org.rubypeople.rdt.internal.launching.RdtLaunchingPlugin;

public interface IInterpreterInstallChangedListener {


	/**
	 * Property constant indicating the name associated
	 * with a VM install has changed.
	 */
	public static final String PROPERTY_NAME = RdtLaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_NAME"; //$NON-NLS-1$
	
	/**
	 * Property constant indicating the install location of
	 * a VM install has changed.
	 */
	public static final String PROPERTY_INSTALL_LOCATION = RdtLaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_INSTALL_LOCATION";	 //$NON-NLS-1$
	
	/**
	 * Property constant indicating the library locations associated
	 * with a VM install have changed.
	 */
	public static final String PROPERTY_LIBRARY_LOCATIONS = RdtLaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_LIBRARY_LOCATIONS"; //$NON-NLS-1$

	/**
	 * Property constant indicating the VM arguments associated
	 * with a VM install has changed.
     * 
     * @since 0.9.0
	 */
	public static final String PROPERTY_VM_ARGUMENTS = RdtLaunchingPlugin.getUniqueIdentifier() + ".PROPERTY_VM_ARGUMENTS"; //$NON-NLS-1$

	
	public void defaultInterpreterInstallChanged(IVMInstall previous, IVMInstall current);

	public void interpreterChanged(PropertyChangeEvent event);

	public void interpreterAdded(IVMInstall newVm);

	public void interpreterRemoved(IVMInstall removedVm);
}
