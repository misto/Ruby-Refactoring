package org.rubypeople.rdt.testunit.launcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration;

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
}