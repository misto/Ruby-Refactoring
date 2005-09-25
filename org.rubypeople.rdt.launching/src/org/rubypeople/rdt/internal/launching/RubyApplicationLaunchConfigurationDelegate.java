package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;

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
			throw new CoreException(new Status(IStatus.ERROR, RdtLaunchingPlugin.PLUGIN_ID, IStatus.OK, RdtLaunchingMessages.getString("RdtLaunchingPlugin.noInterpreterSelected"), null));
        }

		if (mode.equals("debug")) {
			debuggerRunner.run( this.wrapConfiguration(configuration), launch);
		} else {
			interpreterRunner.run( this.wrapConfiguration(configuration), launch);
		}		
	}
    
    protected InterpreterRunnerConfiguration wrapConfiguration(ILaunchConfiguration configuration) {
    	return new InterpreterRunnerConfiguration(configuration) ;
    }
}
