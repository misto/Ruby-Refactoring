package org.rubypeople.rdt.testunit.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration;
import org.rubypeople.rdt.internal.launching.RubyApplicationLaunchConfigurationDelegate;
import org.rubypeople.rdt.testunit.TestunitPlugin;

public class TestUnitLaunchConfigurationDelegate extends RubyApplicationLaunchConfigurationDelegate {

	public static final String PORT_ATTR = TestunitPlugin.PLUGIN_ID + ".PORT"; //$NON-NLS-1$
	/**
	 * The single test type, or "" iff running a launch container.
	 */
	public static final String TESTTYPE_ATTR = TestunitPlugin.PLUGIN_ID + ".TESTTYPE"; //$NON-NLS-1$
	/**
	 * The test method, or "" iff running the whole test type.
	 */
	public static final String TESTNAME_ATTR = TestunitPlugin.PLUGIN_ID + ".TESTNAME"; //$NON-NLS-1$
	/**
	 * The launch container, or "" iff running a single test type.
	 */
	public static final String LAUNCH_CONTAINER_ATTR = TestunitPlugin.PLUGIN_ID + ".CONTAINER"; //$NON-NLS-1$

	public static final String ID_TESTUNIT_APPLICATION = "org.rubypeople.rdt.testunit.launchconfig"; //$NON-NLS-1$

  	public TestUnitLaunchConfigurationDelegate() {
		super();
	}

    protected InterpreterRunnerConfiguration wrapConfiguration(ILaunchConfiguration configuration) {
        return new TestUnitRunnerConfiguration(configuration) ;
    }

}