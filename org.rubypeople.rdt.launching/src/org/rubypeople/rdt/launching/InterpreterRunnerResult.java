package org.rubypeople.rdt.launching;

import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;

public class InterpreterRunnerResult {
	protected IProcess process;

	public InterpreterRunnerResult(IProcess aProcess) {
		super();
		process = aProcess;
	}

	public IProcess[] getProcesses() {
		return new IProcess[] { process };
	}
	
	public IDebugTarget getDebugTarget() {
		return null;
	}
}
