package org.rubypeople.rdt.launching;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;
import sun.security.krb5.internal.crypto.e;

public class InterpreterRunnerConfiguration {
	protected ILaunchConfiguration configuration;

	public InterpreterRunnerConfiguration(ILaunchConfiguration aConfiguration) {
		configuration = aConfiguration;
	}
	
	public String getAbsoluteFileName() {
		IPath path = new Path(getFileName());
		IProject project = getProject().getProject();

		return project.getFile(path).getLocation().toOSString();
	}
	
	public String getFileName() {
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
		String file = null;
		try {
			file = configuration.getAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, "");
		} catch(CoreException e) {
			System.out.println("InterpreterRunnerConfiguration#getAbsoluteWorkingDirectory(): " + e);
		}
		return new File(file);
	}
	
	public String getInterpreterArguments() {
		try {
			return configuration.getAttribute(RubyLaunchConfigurationAttribute.INTERPRETER_ARGUMENTS, "");
		} catch(CoreException e) {}
		
		return "";
	}
	
	public String getProgramArguments() {
		try {
			return configuration.getAttribute(RubyLaunchConfigurationAttribute.PROGRAM_ARGUMENTS, "");
		} catch (CoreException e) {}
		
		return "";
	}

	public RubyInterpreter getInterpreter() {
		String selectedInterpreter = null;
		try {
			selectedInterpreter = configuration.getAttribute(RubyLaunchConfigurationAttribute.SELECTED_INTERPRETER, "");
		} catch(CoreException e) {}
		
		return RubyRuntime.getDefault().getInterpreter(selectedInterpreter);
	}
}
