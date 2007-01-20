package org.rubypeople.rdt.testunit.launcher;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.SocketUtil;
import org.rubypeople.rdt.internal.testunit.ui.TestunitPlugin;
import org.rubypeople.rdt.launching.RubyLaunchDelegate;

public class TestUnitLaunchConfigurationDelegate extends RubyLaunchDelegate {
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
	
	private int port = -1;

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		launch.setAttribute(TestunitPlugin.TESTUNIT_PORT_ATTR, Integer.toString(getPort()));
		super.launch(configuration, mode, launch, monitor);
	}
	
	public static String getTestRunnerPath() {
		String directory = RubyCore.getOSDirectory(TestunitPlugin.getDefault());
		File pluginDirFile  = new File(directory, "ruby");
		
		if (!pluginDirFile.exists()) 
			throw new RuntimeException("Expected directory of RemoteTestRunner.rb does not exist: " + pluginDirFile.getAbsolutePath()); 
	
		return pluginDirFile.getAbsolutePath() + File.separator + TestUnitLaunchShortcut.TEST_RUNNER_FILE;
	}
	
	private int getPort() {
		if (port == -1) {
			port  = SocketUtil.findFreePort();
		}
		return port;
	}

	@Override
	public String getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(configuration.getAttribute(TestUnitLaunchConfigurationDelegate.LAUNCH_CONTAINER_ATTR, ""));
		buffer.append(' ');
		buffer.append(Integer.toString(getPort()));
		buffer.append(' ');
		buffer.append(Boolean.toString(false));
		buffer.append(' ');
		buffer.append(configuration.getAttribute(TestUnitLaunchConfigurationDelegate.TESTTYPE_ATTR, ""));
		buffer.append(' ');
		buffer.append(configuration.getAttribute(TestUnitLaunchConfigurationDelegate.TESTNAME_ATTR, ""));
		return buffer.toString();
	}
}