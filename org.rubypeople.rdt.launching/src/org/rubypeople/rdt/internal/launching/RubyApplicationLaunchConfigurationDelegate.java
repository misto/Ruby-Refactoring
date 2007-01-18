package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.rubypeople.rdt.launching.RubyRuntime;

public class RubyApplicationLaunchConfigurationDelegate implements ILaunchConfigurationDelegate {
	protected static final InterpreterRunner interpreterRunner = new InterpreterRunner();
	protected static final DebuggerRunner debuggerRunner = new DebuggerRunner();

	public RubyApplicationLaunchConfigurationDelegate() {
		super();
	}

	/**
	 * @see ILaunchConfigurationDelegate#launch(ILaunchConfiguration, String, ILaunch, IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (RubyRuntime.getDefault().getSelectedInterpreter() == null) {
			throw new CoreException(new Status(IStatus.ERROR, LaunchingPlugin.PLUGIN_ID, IStatus.OK, RdtLaunchingMessages.RdtLaunchingPlugin_noInterpreterSelected, null));
        }

		if (mode.equals("debug")) {
			debuggerRunner.run( this.wrapConfigurationAndHandleLaunch(configuration, launch), launch);
		} else {
			interpreterRunner.run( this.wrapConfigurationAndHandleLaunch(configuration, launch), launch);
		}		
	}
    
    protected InterpreterRunnerConfiguration wrapConfigurationAndHandleLaunch(ILaunchConfiguration configuration, ILaunch launch) {
    	return new InterpreterRunnerConfiguration(configuration) ;
    }
}
