package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

public class InterpreterRunner {
   
	public InterpreterRunner() {}

	public IProcess run(InterpreterRunnerConfiguration configuration, ILaunch launch) throws CoreException {
		List commandLine = renderCommandLine(configuration);
		File workingDirectory = configuration.getAbsoluteWorkingDirectory();

		RubyInterpreter interpreter = convertInterpreter(configuration.getInterpreter()) ;
		Process nativeRubyProcess = interpreter.exec(commandLine, workingDirectory);
        Map defaultAttributes = new HashMap();
        defaultAttributes.put(IProcess.ATTR_PROCESS_TYPE, "ruby");
		IProcess process = DebugPlugin.newProcess(launch, nativeRubyProcess, renderLabel(configuration), defaultAttributes);
		process.setAttribute(RdtLaunchingPlugin.PLUGIN_ID + ".launcher.cmdline", commandLine.toString());
		return process ;
	}
	
	protected RubyInterpreter convertInterpreter(RubyInterpreter rubyInterpreter) {
		return rubyInterpreter ;
	}

	protected String renderLabel(InterpreterRunnerConfiguration configuration) {
		StringBuffer buffer = new StringBuffer();

		try {
			RubyInterpreter interpreter = configuration.getInterpreter();
			buffer.append("Ruby ");
			buffer.append(interpreter.getCommand());
			buffer.append(" : ");
			buffer.append(configuration.getFileName());
		} catch (IllegalCommandException e) {
			// can't happen because renderLabel assumes that a successful launch has been done
		}

		return buffer.toString();
	}

	private List renderCommandLine(InterpreterRunnerConfiguration configuration) {
		List commandLine = new ArrayList();
		
		addDebugCommandLineArgument(commandLine);
		commandLine.addAll(configuration.renderLoadPath());
		commandLine.addAll(ArgumentSplitter.split(configuration.getInterpreterArguments()));
		commandLine.add(RubyInterpreter.END_OF_OPTIONS_DELIMITER);
		commandLine.add(RdtLaunchingPlugin.osDependentPath(configuration.getAbsoluteFileName()));
		commandLine.addAll(ArgumentSplitter.split(configuration.getProgramArguments()));

		return commandLine;
	}


	protected void addDebugCommandLineArgument(List commandLine) {
	}
	
}
