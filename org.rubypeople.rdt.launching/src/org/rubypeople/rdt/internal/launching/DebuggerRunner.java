package org.rubypeople.rdt.internal.launching;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.debug.core.DebuggerNotFoundException;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;

public class DebuggerRunner extends InterpreterRunner {

	public IProcess run(InterpreterRunnerConfiguration configuration, ILaunch launch) {
		IProcess process = super.run(configuration, launch);
		RubyDebugTarget debugTarget = new RubyDebugTarget(launch, process);
		RubyDebuggerProxy proxy = new RubyDebuggerProxy(debugTarget) ;
		if (proxy.checkConnection()) {
			proxy.start();
			launch.addDebugTarget(debugTarget);	
		}
		else {
			RdtLaunchingPlugin.log(new Status(IStatus.ERROR, RdtLaunchingPlugin.PLUGIN_ID, IStatus.ERROR, RdtLaunchingMessages.getString("RdtLaunchingPlugin.processTerminatedBecauseNoDebuggerConnection"),null)) ;
			debugTarget.terminate();
		}
		return process;
	}

	protected String getDebugCommandLineArgument() {
		if (RdtDebugCorePlugin.getDefault().isRubyDebuggerVerbose()) {
			return " -reclipseDebugVerbose";
		}
		return " -reclipseDebug";
	}

	public static String getDirectoryOfRubyDebuggerFile() {
	    return RubyPlugin.getOSDirectory(RdtLaunchingPlugin.getDefault()) + "ruby" ;
	}
	
	protected String renderLoadPath(InterpreterRunnerConfiguration configuration) {
		return super.renderLoadPath(configuration) + " -I " + osDependentPath(DebuggerRunner.getDirectoryOfRubyDebuggerFile().replace('/', File.separatorChar));
	}
}