package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.io.IOException;

import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IProcess;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;






/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DebuggerRunner extends InterpreterRunner {

	public IProcess run(InterpreterRunnerConfiguration configuration, ILaunch launch) {
		IProcess process = super.run(configuration, launch) ;
		RubyDebugTarget debugTarget = new RubyDebugTarget(launch, process);				
		launch.addDebugTarget(debugTarget);
		new RubyDebuggerProxy(debugTarget).start() ;
		return process ;
	}
	
	protected String getDebugCommandLineArgument() {
		return " -reclipseDebug" ;	
	}

}
