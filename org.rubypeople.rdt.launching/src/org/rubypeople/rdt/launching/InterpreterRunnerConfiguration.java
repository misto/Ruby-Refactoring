package org.rubypeople.rdt.launching;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.QualifiedName;
import org.rubypeople.rdt.internal.core.RubyProject;

public class InterpreterRunnerConfiguration {
	protected IFile rubyFile;

	public InterpreterRunnerConfiguration(IFile aRubyFile) {
		rubyFile = aRubyFile;
	}
	
	public String getRubyFileName() {
		return rubyFile.getName();
	}
	
	public RubyProject getProject() {
		IResource resource = (IResource) ((IAdaptable)rubyFile).getAdapter(IResource.class);
		if (resource != null) {
			RubyProject project = new RubyProject();
			project.setProject(resource.getProject());
			return project;
		}

		return null;
	}
	
	public File getAbsoluteWorkingDirectory() {
		return rubyFile.getParent().getLocation().toFile();
	}
	
	public ExecutionArguments getExecutionArguments() {
		ExecutionArguments executionArguments = new ExecutionArguments();
		try {
			String arguments = rubyFile.getPersistentProperty(new QualifiedName("executionArguments", "interpreter"));
			executionArguments.setInterpreterArguments(arguments != null ? arguments : "");

			arguments = rubyFile.getPersistentProperty(new QualifiedName("executionArguments", "program"));
			executionArguments.setRubyFileArguments(arguments != null ? arguments : "");
		} catch(CoreException e) {}
		
		return executionArguments;
	}
}
