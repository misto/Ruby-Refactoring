package org.rubypeople.rdt.testunit.launcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration;
import org.rubypeople.rdt.testunit.TestunitPlugin;

public class TestUnitRunnerConfiguration extends InterpreterRunnerConfiguration {

	public TestUnitRunnerConfiguration(ILaunchConfiguration aConfiguration) {
		super(aConfiguration);
	}

	public String getAbsoluteFileName() {
		IProject project = getProject().getProject();
	
		String filename = getFileName();
		if (Platform.getOS().equals(Platform.OS_WIN32)) {
			// just searching for a colon without consideration of the Platform
			// could chop off linux filenames which may contain colons
			if (filename.indexOf(':') != -1) { 
				filename = filename.substring(filename.indexOf(':') + 1); 
			}
		}		
		return filename ;
	}
	
	
	/* (non-Javadoc)
	 * @see org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration#getProgramArguments()
	 */
	public String getProgramArguments() {
		int port = 6789;
		String fileName = "";
		String testClass = "";
		boolean keepAlive = true;
		try {
			// Pull out the port and other unit testing variables
			// and convert them into command line args
			port = configuration.getAttribute(TestUnitLaunchConfiguration.PORT_ATTR, 6789);
			fileName = configuration.getAttribute(TestUnitLaunchConfiguration.LAUNCH_CONTAINER_ATTR, "");
			keepAlive = configuration.getAttribute(TestUnitLaunchConfiguration.ATTR_KEEPRUNNING, true);
			testClass = configuration.getAttribute(TestUnitLaunchConfiguration.TESTTYPE_ATTR, "");
		} catch (CoreException e) {
			TestunitPlugin.log(e);
		}
		
		return fileName + " " + port + " " + keepAlive + " " + testClass;
	}
}