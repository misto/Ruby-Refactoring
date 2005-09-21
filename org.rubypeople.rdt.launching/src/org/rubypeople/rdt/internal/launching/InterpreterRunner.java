package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;

public class InterpreterRunner {
   
	public InterpreterRunner() {}

	public IProcess run(InterpreterRunnerConfiguration configuration, ILaunch launch) throws CoreException {
		String commandLine = renderCommandLine(configuration);
		File workingDirectory = configuration.getAbsoluteWorkingDirectory();

		RubyInterpreter interpreter = configuration.getInterpreter() ;
		Process nativeRubyProcess = interpreter.exec(commandLine, workingDirectory);
        Map defaultAttributes = new HashMap();
        defaultAttributes.put(IProcess.ATTR_PROCESS_TYPE, "ruby");
		IProcess process = DebugPlugin.newProcess(launch, nativeRubyProcess, renderLabel(configuration), defaultAttributes);
		process.setAttribute(RdtLaunchingPlugin.PLUGIN_ID + ".launcher.cmdline", commandLine);
		return process ;
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
