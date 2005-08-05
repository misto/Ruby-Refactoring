package org.rubypeople.rdt.testunit.launcher;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration;
import org.rubypeople.rdt.internal.launching.RdtLaunchingPlugin;
import org.rubypeople.rdt.testunit.TestunitPlugin;

public class TestUnitRunnerConfiguration extends InterpreterRunnerConfiguration {

	public TestUnitRunnerConfiguration(ILaunchConfiguration aConfiguration) {
		super(aConfiguration);
	}

	public String getAbsoluteFileName() {
		return new File(getFileName()).getAbsolutePath();
	}
	
	public String getFileName() {
		return getTestRunnerPath();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration#getAbsoluteFileDirectory()
	 */
	public String getAbsoluteFileDirectory() {
		IPath path = new Path(this.getFileName());
		path = path.removeLastSegments(1);
		return path.toOSString();
	}

	public String getAbsoluteTestFileName() {
		String fileName = "";
		try {
			fileName = configuration.getAttribute(TestUnitLaunchConfigurationDelegate.LAUNCH_CONTAINER_ATTR, "");
		} catch (CoreException e) {}

		IPath path = new Path(fileName);
		path = path.removeLastSegments(1);
		return path.toOSString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration#getProgramArguments()
	 */
	public String getProgramArguments() {
		int port = 6789;
		String fileName = "";
		String testClass = "";
		String testMethod = "";
		// FIXME Remove keepAlive on this end and remove looking for it on
		// RemoteTestRunner.rb
		boolean keepAlive = false;
		try {
			// Pull out the port and other unit testing variables
			// and convert them into command line args
			port = configuration.getAttribute(TestUnitLaunchConfigurationDelegate.PORT_ATTR, 6789);
			fileName = configuration.getAttribute(TestUnitLaunchConfigurationDelegate.LAUNCH_CONTAINER_ATTR, "");
			testClass = configuration.getAttribute(TestUnitLaunchConfigurationDelegate.TESTTYPE_ATTR, "");
			testMethod = configuration.getAttribute(TestUnitLaunchConfigurationDelegate.TESTNAME_ATTR, "");
		} catch (CoreException e) {
			TestunitPlugin.log(e);
		}

		return fileName + " " + port + " " + keepAlive + " " + testClass + " " + testMethod;
	}

	public String renderLoadPath() {
		String absoluteTestFilePath = RdtLaunchingPlugin.osDependentPath(this.getAbsoluteTestFileName());
		if (absoluteTestFilePath.length() == 0) { return super.renderLoadPath(); }
		return super.renderLoadPath() + " -I " + absoluteTestFilePath;
	}

	public static String getTestRunnerPath() {
		String location = TestunitPlugin.getDefault().getBundle().getLocation();
		int prefixLength = location.indexOf('@');
		if (prefixLength == -1)  
			throw new RuntimeException("Location of launching bundle does not contain @: " + location); 
		
		String pluginDir = location.substring(prefixLength + 1) + "ruby";
		File pluginDirFile = new File(pluginDir);
		
		if (!pluginDirFile.exists()) 
			throw new RuntimeException("Expected directory of RemoteTestRunner.rb does not exist: " + pluginDir); 
	
		return pluginDirFile.getAbsolutePath() + File.separator + TestUnitLaunchShortcut.TEST_RUNNER_FILE;
	}
}