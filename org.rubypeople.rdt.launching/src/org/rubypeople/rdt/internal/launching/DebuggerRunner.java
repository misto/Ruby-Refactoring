package org.rubypeople.rdt.internal.launching;

import java.io.File;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;

public class DebuggerRunner extends InterpreterRunner {

	public IProcess run(InterpreterRunnerConfiguration configuration, ILaunch launch) {
		IProcess process = super.run(configuration, launch);
		RubyDebugTarget debugTarget = new RubyDebugTarget(launch, process);
		new RubyDebuggerProxy(debugTarget).start();
		launch.addDebugTarget(debugTarget);
		return process;
	}

	protected String getDebugCommandLineArgument() {
		if (RdtDebugCorePlugin.getDefault().isRubyDebuggerVerbose()) {
			return " -reclipseDebugVerbose";
		} else {
			return " -reclipseDebug";
		}
	}

	public static String getDirectoryOfRubyDebuggerFile() {
		// Lets check the new OSGI Bundles...
		String pluginDir = RdtLaunchingPlugin.getDefault().getBundle().getLocation();
		if (pluginDir.startsWith("reference:file:")) { return pluginDir.substring(15) + "/ruby"; }
		throw new RuntimeException("Could not find directory of ruby debugger file (eclipseDebug.rb).");
	}

	protected String renderLoadPath(InterpreterRunnerConfiguration configuration) {
		return super.renderLoadPath(configuration) + " -I " + osDependentPath(DebuggerRunner.getDirectoryOfRubyDebuggerFile().replace('/', File.separatorChar));
	}
}