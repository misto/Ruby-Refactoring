package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.VMRunnerConfiguration;

public class RDebugVMDebugger extends StandardVMDebugger {

	private static final String PORT_SWITCH = "--port";
	private static final String VERBOSE_FLAG = "-d";
	private static final String RDEBUG_EXECUTABLE_WINDOWS = "rdebug-ide.cmd";
	private static final String RDEBUG_EXECUTABLE = "rdebug-ide";

	public RDebugVMDebugger(IVMInstall vmInstance) {
		super(vmInstance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.launching.IVMRunner#run(org.rubypeople.rdt.launching.VMRunnerConfiguration,
	 *      org.eclipse.debug.core.ILaunch,
	 *      org.eclipse.core.runtime.IProgressMonitor)
	 */
	public void run(VMRunnerConfiguration config, ILaunch launch, IProgressMonitor monitor) throws CoreException {

		// Set it up to use rdebug executable
		Map map = config.getVMSpecificAttributesMap();
		if (map == null) map = new HashMap();
		String executable = findRDebugExecutable(fVMInstance.getInstallLocation());
		map.put(IRubyLaunchConfigurationConstants.ATTR_RUBY_COMMAND, executable);
		config.setVMSpecificAttributesMap(map);
		
		super.run(config, launch, monitor);
	}

	protected List<String> debugSpecificVMArgs(RubyDebugTarget debugTarget) {
		List<String> arguments = new ArrayList<String>();
		arguments.add(PORT_SWITCH);
		arguments.add(Integer.toString(debugTarget.getPort()));
		if (isDebuggerVerbose()) {
			arguments.add(VERBOSE_FLAG);
		}
		return arguments;
	}
	
	protected RubyDebuggerProxy getDebugProxy(RubyDebugTarget debugTarget) {
		return new RubyDebuggerProxy(debugTarget, true);
	}

	public static String findRDebugExecutable(File vmInstallLocation) {
		// see StandardVMRunner.constructProgramString
		String cmdWin = RDEBUG_EXECUTABLE_WINDOWS;
		String path = vmInstallLocation + File.separator + "bin" + File.separator + cmdWin;
		if (new File(path).exists()) {
			return cmdWin;
		}
		return RDEBUG_EXECUTABLE;
	}
	
}
