package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.internal.core.RubyProject;

public class InterpreterRunner {

	public InterpreterRunner() {}

	public void run(InterpreterRunnerConfiguration configuration, ILaunch launch) {
		String commandLine = renderCommandLine(configuration);
		File workingDirectory = configuration.getAbsoluteWorkingDirectory();

		Process nativeRubyProcess = null;
		try {
			nativeRubyProcess = Runtime.getRuntime().exec(commandLine, null, workingDirectory);
		} catch (IOException e) {
			throw new RuntimeException("Unable to execute interpreter: " + commandLine + workingDirectory);
		}

		IProcess process = DebugPlugin.getDefault().newProcess(launch, nativeRubyProcess, renderLabel(configuration));
		process.setAttribute(RdtLaunchingPlugin.PLUGIN_ID + ".launcher.cmdline", commandLine);
	}

	protected String renderLabel(InterpreterRunnerConfiguration configuration) {
		StringBuffer buffer = new StringBuffer();
		
		RubyInterpreter interpreter = configuration.getInterpreter();
		buffer.append("Ruby ");
		buffer.append(interpreter.getCommand());
		buffer.append(" : ");
		buffer.append(configuration.getFileName());
		
		return buffer.toString();
	}

	protected String renderCommandLine(InterpreterRunnerConfiguration configuration) {
		RubyInterpreter interpreter = configuration.getInterpreter();

		StringBuffer buffer = new StringBuffer();
		buffer.append(interpreter.getCommand());
		buffer.append(renderLoadPath(configuration));
		buffer.append(" " + configuration.getInterpreterArguments());
		buffer.append(interpreter.endOfOptionsDelimeter);
		buffer.append(configuration.getAbsoluteFileName());
		buffer.append(" " + configuration.getProgramArguments());

		return buffer.toString();
	}

	protected String renderLoadPath(InterpreterRunnerConfiguration configuration) {
		StringBuffer loadPath = new StringBuffer();
		
		RubyProject project = configuration.getProject();
		addToLoadPath(loadPath, project.getProject());

		Iterator referencedProjects = project.getReferencedProjects().iterator();
		while (referencedProjects.hasNext())
			addToLoadPath(loadPath, (IProject) referencedProjects.next());

		return loadPath.toString();
	}
	
	protected void addToLoadPath(StringBuffer loadPath, IProject project) {
		loadPath.append(" -I " + project.getLocation().toOSString());
	}
}
