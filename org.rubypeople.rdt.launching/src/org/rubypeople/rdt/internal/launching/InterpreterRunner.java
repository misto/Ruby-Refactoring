package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.boot.BootLoader;
import org.eclipse.core.resources.IProject;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.core.RubyProject;

public class InterpreterRunner {

	public InterpreterRunner() {
	}

	public IProcess run(InterpreterRunnerConfiguration configuration, ILaunch launch) {
		String commandLine = renderCommandLine(configuration);
		File workingDirectory = configuration.getAbsoluteWorkingDirectory();

		Process nativeRubyProcess = null;
		try {
			nativeRubyProcess = configuration.getInterpreter().exec(commandLine, workingDirectory);
		} catch (IOException e) {
			throw new RuntimeException("Unable to execute interpreter: " + commandLine + workingDirectory);
		}

		IProcess process = DebugPlugin.getDefault().newProcess(launch, nativeRubyProcess, renderLabel(configuration));
		process.setAttribute(RdtLaunchingPlugin.PLUGIN_ID + ".launcher.cmdline", commandLine);
		return process ;
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
		buffer.append(this.getDebugCommandLineArgument());
		buffer.append(renderLoadPath(configuration));
		buffer.append(" " + configuration.getInterpreterArguments());
		buffer.append(interpreter.endOfOptionsDelimeter);
		buffer.append(osDependentPath(configuration.getAbsoluteFileName()));
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

		loadPath.append(" -I " + osDependentPath(project.getLocation().toOSString()));
	}

	protected String osDependentPath(String aPath) {
		if (BootLoader.getOS().equals(BootLoader.OS_WIN32))
			aPath = "\"" + aPath + "\"";

		return aPath;
	}
	
	protected String getDebugCommandLineArgument() {
		return "" ;	
	}
	
}
