package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugPlugin;
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
		return " -reclipseDebug";
	}

	protected String getDirectoryOfRubyDebuggerFile() {
		// this class will usually reside in the launching.jar file ...
		URL url = this.getClass().getResource("");
		if (url != null && url.getProtocol().equals("jar")) {
			String jarFile = url.getFile().substring(0, url.getFile().lastIndexOf("!"));
			String dir = jarFile.substring(0, jarFile.lastIndexOf("/"));
			return dir.replaceFirst("file:", "") + "/ruby";
		}
		// ... but in a runtime workbench the class has been loaded from bin directory
		url = this.getClass().getResource("/");
		if (url != null && url.getProtocol().equals("file")) {
			return url.getFile() + "../ruby";
		}
		throw new RuntimeException("Could not find directory of ruby debugger file.") ;		
	}

	protected String renderLoadPath(InterpreterRunnerConfiguration configuration) {
		return super.renderLoadPath(configuration) + " -I " + osDependentPath(this.getDirectoryOfRubyDebuggerFile().replace('/', File.separatorChar));
	}
}
