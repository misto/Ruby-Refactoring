package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;
import org.rubypeople.rdt.launching.IInterpreter;

public class DebuggerRunner extends InterpreterRunner {

	private RubyDebugTarget debugTarget;

	public IProcess run(InterpreterRunnerConfiguration configuration, ILaunch launch) throws CoreException {
		debugTarget = new RubyDebugTarget(launch);
		IProcess process = super.run(configuration, launch);
		debugTarget.setProcess(process);
		RubyDebuggerProxy proxy = new RubyDebuggerProxy(debugTarget, isUseRubyDebug());
		if (proxy.checkConnection()) {
			try {
				if (isUseRubyDebug()) {
					String pathToRdebugExtension = getDirectoryOfRubyDebuggerFile() + "/rdebugExtension.rb";
					proxy.registerRdebugExtension(pathToRdebugExtension);
				}
				proxy.start();
				launch.addDebugTarget(debugTarget);
			} catch (Exception e) {
				RdtLaunchingPlugin.log(new Status(IStatus.ERROR, RdtLaunchingPlugin.PLUGIN_ID, IStatus.ERROR, e.getMessage(), e));
				debugTarget.terminate();
			}
		} else {
			RdtLaunchingPlugin.log(new Status(IStatus.ERROR, RdtLaunchingPlugin.PLUGIN_ID, IStatus.ERROR, RdtLaunchingMessages.RdtLaunchingPlugin_processTerminatedBecauseNoDebuggerConnection, null));
			debugTarget.terminate();
		}
		return process;
	}

	protected void addDebugCommandLineArgument(List<String> commandLine) {
		if (isUseRubyDebug()) {
			commandLine.add("--server");
			commandLine.add("--port");
			commandLine.add(Integer.toString(debugTarget.getPort()));
			commandLine.add("--cport");
			commandLine.add(Integer.toString(debugTarget.getPort() + 1));
			commandLine.add("-w");
			if (isDebuggerVerbose()) {
				commandLine.add("-d");
			}
			commandLine.add("-f");
			commandLine.add("xml");
		} else {
			if (!debugTarget.isUsingDefaultPort()) {
				commandLine.add("-r" + debugTarget.getDebugParameterFile().getAbsolutePath());
			}

			if (RdtDebugCorePlugin.isRubyDebuggerVerbose() || isDebuggerVerbose()) {
				commandLine.add("-reclipseDebugVerbose");
			} else {
				commandLine.add("-reclipseDebug");
			}
			commandLine.add("-I");
			commandLine.add(RdtLaunchingPlugin.osDependentPath(DebuggerRunner.getDirectoryOfRubyDebuggerFile().replace('/', File.separatorChar)));
		}
	}

	public static String getDirectoryOfRubyDebuggerFile() {
		return RubyCore.getOSDirectory(RdtLaunchingPlugin.getDefault()) + "ruby";
	}

	public boolean isUseRubyDebug() {
		return RdtLaunchingPlugin.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.USE_RUBY_DEBUG);
	}
	
	public boolean isDebuggerVerbose() {
		return RdtLaunchingPlugin.getDefault().getPluginPreferences().getBoolean(PreferenceConstants.VERBOSE_DEBUGGER);
	}

	protected IInterpreter convertInterpreter(IInterpreter rubyInterpreter) {
		if (isUseRubyDebug()) {
			IPath rdebugLocation = new Path(rubyInterpreter.getInstallLocation().getAbsolutePath()).removeLastSegments(1);
			rdebugLocation = rdebugLocation.append("rdebug");
			return new RubyInterpreter("rdebug", rdebugLocation.toFile());
		} else {
			return rubyInterpreter;
		}
	}
}