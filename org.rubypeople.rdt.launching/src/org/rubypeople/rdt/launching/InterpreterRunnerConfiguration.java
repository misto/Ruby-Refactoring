package org.rubypeople.rdt.launching;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;
import sun.security.krb5.internal.crypto.e;

public class InterpreterRunnerConfiguration {
	protected ILaunchConfiguration configuration;

	public InterpreterRunnerConfiguration(ILaunchConfiguration aConfiguration) {
		configuration = aConfiguration;
	}
	
	public String getRubyFileName() {
		String fileName = "";

		try {
			fileName = configuration.getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "No file specified in configuration");
		} catch(CoreException e) {}

		return fileName;
	}
	
	public RubyProject getProject() {
		String projectName = "";
		
		try {
			projectName = configuration.getAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, "");
		} catch(CoreException e) {}

		IProject project = RdtLaunchingPlugin.getWorkspace().getRoot().getProject(projectName);

		RubyProject rubyProject = new RubyProject();
		rubyProject.setProject(project);
		return rubyProject;
	}
	
	public File getAbsoluteWorkingDirectory() {
		return getProject().getProject().getLocation().toFile();
	}
	
	public ExecutionArguments getExecutionArguments() {
		ExecutionArguments executionArguments = new ExecutionArguments();
		try {
			String arguments = configuration.getAttribute(RubyLaunchConfigurationAttribute.INTERPRETER_ARGUMENTS, "");
			executionArguments.setInterpreterArguments(arguments);

			arguments = configuration.getAttribute(RubyLaunchConfigurationAttribute.PROGRAM_ARGUMENTS, "");
			executionArguments.setRubyFileArguments(arguments);
		} catch(CoreException e) {}
		
		return executionArguments;
	}
}
