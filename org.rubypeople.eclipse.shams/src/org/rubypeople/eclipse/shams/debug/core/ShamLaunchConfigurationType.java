package org.rubypeople.eclipse.shams.debug.core;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class ShamLaunchConfigurationType implements ILaunchConfigurationType {
	protected boolean newInstanceCreated;

	public ShamLaunchConfigurationType() {
		super();
	}

	public boolean supportsMode(String mode) {
		return false;
	}

	public String getName() {
		return null;
	}

	public String getIdentifier() {
		return null;
	}

	public boolean isPublic() {
		return false;
	}

	public ILaunchConfigurationWorkingCopy newInstance(IContainer container, String name) throws CoreException {
		newInstanceCreated = true;
		return new ShamLaunchConfigurationWorkingCopy();
	}

	public ILaunchConfigurationDelegate getDelegate() throws CoreException {
		return null;
	}

	public boolean wasNewInstanceCreated() {
		boolean temp = newInstanceCreated;
		newInstanceCreated = false;
		return temp;
	}

}
