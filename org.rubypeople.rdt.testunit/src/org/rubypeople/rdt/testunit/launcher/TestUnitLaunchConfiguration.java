package org.rubypeople.rdt.testunit.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.rubypeople.rdt.internal.launching.DebuggerRunner;
import org.rubypeople.rdt.internal.launching.InterpreterRunner;
import org.rubypeople.rdt.internal.launching.RubyRuntime;
import org.rubypeople.rdt.testunit.TestunitPlugin;

public class TestUnitLaunchConfiguration implements ILaunchConfigurationDelegate {

	public static final String PORT_ATTR = TestunitPlugin.PLUGIN_ID + ".PORT"; //$NON-NLS-1$
	/**
	 * The single test type, or "" iff running a launch container.
	 */
	public static final String TESTTYPE_ATTR = TestunitPlugin.PLUGIN_ID + ".TESTTYPE"; //$NON-NLS-1$
	/**
	 * The test method, or "" iff running the whole test type.
	 */
	public static final String TESTNAME_ATTR = TestunitPlugin.PLUGIN_ID + ".TESTNAME"; //$NON-NLS-1$
	public static final String ATTR_KEEPRUNNING = TestunitPlugin.PLUGIN_ID + ".KEEPRUNNING_ATTR"; //$NON-NLS-1$
	/**
	 * The launch container, or "" iff running a single test type.
	 */
	public static final String LAUNCH_CONTAINER_ATTR = TestunitPlugin.PLUGIN_ID + ".CONTAINER"; //$NON-NLS-1$

	public static final String ID_TESTUNIT_APPLICATION = "org.rubypeople.rdt.testunit.launchconfig"; //$NON-NLS-1$

	protected static final InterpreterRunner interpreterRunner = new InterpreterRunner();
	protected static final DebuggerRunner debuggerRunner = new DebuggerRunner();

	public TestUnitLaunchConfiguration() {
		super();
	}

	/**
	 * @see ILaunchConfigurationDelegate#launch(ILaunchConfiguration, String,
	 *      ILaunch, IProgressMonitor)
	 */
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		if (RubyRuntime.getDefault().getSelectedInterpreter() == null) throw new CoreException(new Status(IStatus.ERROR, TestunitPlugin.PLUGIN_ID, IStatus.OK, "You must define an interpreter before running Ruby Applications.", null));
		if (mode.equals("debug")) {
			debuggerRunner.run(new TestUnitRunnerConfiguration(configuration), launch);
		} else {
			interpreterRunner.run(new TestUnitRunnerConfiguration(configuration), launch);
		}
	}
}