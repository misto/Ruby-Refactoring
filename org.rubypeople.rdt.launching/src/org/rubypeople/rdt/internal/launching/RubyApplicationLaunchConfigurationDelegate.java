package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class RubyApplicationLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

	public RubyApplicationLaunchConfigurationDelegate() {
		super();
	}

	public ILaunch launch(ILaunchConfiguration configuration, String mode, IProgressMonitor monitor) throws CoreException {
		return null;
	}

}
