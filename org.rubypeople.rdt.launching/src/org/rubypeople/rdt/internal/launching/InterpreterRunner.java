package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

public class InterpreterRunner {
   
	public InterpreterRunner() {}

	public IProcess run(InterpreterRunnerConfiguration configuration, ILaunch launch) {
		String commandLine = renderCommandLine(configuration);
		File workingDirectory = configuration.getAbsoluteWorkingDirectory();

		Process nativeRubyProcess = null;
		try {
			RdtLaunchingPlugin.debug("Launching: " + commandLine) ;
			RdtLaunchingPlugin.debug("Working Dir: " + workingDirectory) ;
			nativeRubyProcess = configuration.getInterpreter().exec(commandLine, workingDirectory);
		} catch (IOException e) {
			throw new RuntimeException("Unable to execute interpreter: " + commandLine + workingDirectory);
		}
        Map defaultAttributes = new HashMap();
        defaultAttributes.put(IProcess.ATTR_PROCESS_TYPE, "ruby");
		IProcess process = DebugPlugin.newProcess(launch, nativeRubyProcess, renderLabel(configuration), defaultAttributes);
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
		buffer.append(configuration.renderLoadPath());
		buffer.append(" " + configuration.getInterpreterArguments());
		buffer.append(interpreter.endOfOptionsDelimeter);
		buffer.append(RdtLaunchingPlugin.osDependentPath(configuration.getAbsoluteFileName()));
		buffer.append(" " + configuration.getProgramArguments());

		return buffer.toString();
	}


	
	protected String getDebugCommandLineArgument() {
		return "" ;	
	}
	
}
