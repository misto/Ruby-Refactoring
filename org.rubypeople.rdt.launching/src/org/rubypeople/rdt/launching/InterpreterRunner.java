package org.rubypeople.rdt.launching;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.internal.core.RuntimeProcess;

public class InterpreterRunner {

	public InterpreterRunner() {}

	public InterpreterRunnerResult run(InterpreterRunnerConfiguration configuration) {
		String commandLine = renderCommandLine(configuration);
		File workingDirectory = configuration.getAbsoluteWorkingDirectory();

		IProcess rubyProcess = null;
		try {
			Process nativeRubyProcess = Runtime.getRuntime().exec(commandLine, null, workingDirectory);
			rubyProcess = new RuntimeProcess(null, nativeRubyProcess, "Ruby Interpreter");
		} catch (IOException e) {
			throw new RuntimeException("Unable to execute interpreter: " + commandLine + workingDirectory);
		}

		return new InterpreterRunnerResult(rubyProcess);
	}

	public String renderCommandLine(InterpreterRunnerConfiguration configuration) {
		RubyInterpreter interpreter = RubyRuntime.getDefault().getSelectedInterpreter();

		StringBuffer buffer = new StringBuffer();
		buffer.append(interpreter.getCommand());
		buffer.append(getLibraryPath(configuration));
		buffer.append(" " + configuration.getExecutionArguments().interpreterArguments);
		buffer.append(interpreter.endOfOptionsDelimeter);
		buffer.append(configuration.getRubyFileName());
		buffer.append(" " + configuration.getExecutionArguments().rubyFileArguments);

		return buffer.toString();
	}

	protected String getLibraryPath(InterpreterRunnerConfiguration configuration) {
		StringBuffer buffer = new StringBuffer();
				
		Iterator referencedProjects = configuration.getProject().getReferencedProjects().iterator();
		while (referencedProjects.hasNext()) {
			IProject iProject = (IProject) referencedProjects.next();
			buffer.append(" -I " + iProject.getLocation().toOSString());
		}

		return buffer.toString();
	}
}
