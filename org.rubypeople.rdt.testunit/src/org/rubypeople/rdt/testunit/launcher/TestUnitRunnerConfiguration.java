package org.rubypeople.rdt.testunit.launcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration;
import org.rubypeople.rdt.testunit.TestunitPlugin;

public class TestUnitRunnerConfiguration extends InterpreterRunnerConfiguration {

	public TestUnitRunnerConfiguration(ILaunchConfiguration aConfiguration) {
		super(aConfiguration);
	}

	public String getAbsoluteFileName() {
		IProject project = getProject().getProject();

		// FIXME Added so that we can do absolute paths as well
		// This works by chopping off the "C:" on windows
		// I'm not sure what will happen on other OSes
		String filename = getFileName();
		if (filename.indexOf(':') != -1) { return filename.substring(filename.indexOf(':') + 1); }
		return project.getLocation().toOSString() + "/" + filename;
	}
	
	
	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration#getProgramArguments()
	 */
	public String getProgramArguments() {
		int port = 6789;
		String fileName = "";
		boolean keepAlive = true;
		try {
			// Pull out the port and other unit testing variables
			// and convert them into command line args
			port = configuration.getAttribute(TestUnitLaunchConfiguration.PORT_ATTR, 6789);
			fileName = configuration.getAttribute(TestUnitLaunchConfiguration.LAUNCH_CONTAINER_ATTR, "");
			keepAlive = configuration.getAttribute(TestUnitLaunchConfiguration.ATTR_KEEPRUNNING, true);
		} catch (CoreException e) {
			TestunitPlugin.log(e);
		}
		
		return fileName + " " + port + " " + keepAlive;
	}
}