package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.rubypeople.rdt.internal.core.RubyCore;

public class RubyApplicationLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {
	protected static final InterpreterRunner runner = new InterpreterRunner();

	public RubyApplicationLaunchConfigurationDelegate() {
		super();
	}

	/**
	 * @see ILaunchConfigurationDelegate#launch(ILaunchConfiguration, String, ILaunch, IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (RubyRuntime.getDefault().getSelectedInterpreter() == null)
			throw new CoreException(new Status(IStatus.ERROR, RdtLaunchingPlugin.PLUGIN_ID, IStatus.OK, "You must define an interpreter before running Ruby Applications.", null));

		runner.run(new InterpreterRunnerConfiguration(configuration), launch);
	}
}
