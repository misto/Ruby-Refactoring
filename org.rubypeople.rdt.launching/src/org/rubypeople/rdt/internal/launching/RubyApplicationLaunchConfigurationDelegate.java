package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

public class RubyApplicationLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {

	public RubyApplicationLaunchConfigurationDelegate() {
		super();
	}

	/**
	 * @see ILaunchConfigurationDelegate#launch(ILaunchConfiguration, String, ILaunch, IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		new InterpreterRunner().run(new InterpreterRunnerConfiguration(configuration), launch);
	}
}
