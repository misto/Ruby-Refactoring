package org.rubypeople.rdt.internal.launching;

import java.io.File;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.rubypeople.rdt.core.RubyProject;

public class InterpreterRunnerConfiguration {
	protected ILaunchConfiguration configuration;

	public InterpreterRunnerConfiguration(ILaunchConfiguration aConfiguration) {
		configuration = aConfiguration;
	}
	
	public String getAbsoluteFileName() {
		IPath path = new Path(getFileName());
		IProject project = getProject().getProject();

		return project.getLocation().toOSString() + "/" + getFileName();
	}
	
	public String getFileName() {
		String fileName = "";

		try {
			fileName = configuration.getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "No file specified in configuration");
		} catch(CoreException e) {}
		
		return fileName.replace('\\', '/');
	}
	
	public RubyProject getProject() {
		String projectName = "";
		
		try {
			projectName = configuration.getAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, "");
		} catch(CoreException e) {
			RdtLaunchingPlugin.getDefault().log(e);
		}

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
			RdtLaunchingPlugin.log(e);
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
