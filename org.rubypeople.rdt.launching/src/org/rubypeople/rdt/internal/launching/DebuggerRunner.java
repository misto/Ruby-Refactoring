package org.rubypeople.rdt.internal.launching;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;

public class DebuggerRunner extends InterpreterRunner {

	public IProcess run(InterpreterRunnerConfiguration configuration, ILaunch launch) throws CoreException {
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
        String debugLoadPathAddition =  RdtLaunchingPlugin.osDependentPath(DebuggerRunner.getDirectoryOfRubyDebuggerFile().replace('/', File.separatorChar));
		if (RdtDebugCorePlugin.isRubyDebuggerVerbose()) {
			return " -reclipseDebugVerbose -I " + debugLoadPathAddition ;
		}
		return " -reclipseDebug -I "+ debugLoadPathAddition ;
	}

	public static String getDirectoryOfRubyDebuggerFile() {
	    return RubyCore.getOSDirectory(RdtLaunchingPlugin.getDefault()) + "ruby" ;
	}
}