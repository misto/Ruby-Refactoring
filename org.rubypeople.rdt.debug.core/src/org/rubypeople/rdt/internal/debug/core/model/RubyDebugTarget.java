package org.rubypeople.rdt.internal.debug.core.model;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IMemoryBlock;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IThread;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;


public class RubyDebugTarget implements IRubyDebugTarget {

	private IProcess process;
	private boolean isTerminated;
	private ILaunch launch;
	private RubyThread[] threads;
	private RubyDebuggerProxy rubyDebuggerProxy;

	public RubyDebugTarget(ILaunch launch, IProcess process) {
		this.launch = launch;
		this.process = process;
		this.threads = new RubyThread[0] ;
		IBreakpointManager manager= DebugPlugin.getDefault().getBreakpointManager();
		manager.addBreakpointListener(this);

	}

	public void updateThreads() {
		// preconditions:
		// 1) both threadInfos and updatedThreads are sorted by their id attribute
		// 2) once a thread has died its id is never reused for new threads again. Instead each new 
		//    thread gets an id which is the currently highest id + 1.

		RdtDebugCorePlugin.debug("udpating threads");
		ThreadInfo[] threadInfos = this.getRubyDebuggerProxy().readThreads();
		RubyThread[] updatedThreads = new RubyThread[threadInfos.length];
		int threadIndex = 0;
		for (int i = 0; i < threadInfos.length; i++) {
			while (threadIndex < threads.length && threadInfos[i].getId() != threads[threadIndex].getId()) {
				// step over dead threads, which do not occur in threadInfos anymore
				threadIndex += 1;
			}
			if (threadIndex == threads.length) {
				updatedThreads[i] = new RubyThread(this, threadInfos[i].getId());
			} else {
				updatedThreads[i] = threads[threadIndex];
			}
		}
		threads = updatedThreads;
		DebugEvent ev = new DebugEvent(this, DebugEvent.CHANGE);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });

	}

	protected RubyThread getThreadById(int id) {
		for (int i = 0; i < threads.length; i++) {
			if (threads[i].getId() == id) {
				return threads[i];
			}
		}
		return null;
	}

	public void suspensionOccurred(SuspensionPoint suspensionPoint) {
		this.updateThreads();
		RubyThread thread = this.getThreadById(suspensionPoint.getThreadId());
		if (thread == null) {
			RdtDebugCorePlugin.log(IStatus.ERROR, "Thread with id " + suspensionPoint.getThreadId() + " was not found");
			return;
		}
		thread.doSuspend(suspensionPoint);
	}

	public IThread[] getThreads() {
		return threads;
	}

	public boolean hasThreads() throws DebugException {
		return threads.length > 0;
	}

	public String getName() throws DebugException {
		return "Ruby";
	}

	public boolean supportsBreakpoint(IBreakpoint arg0) {
		return false;
	}

	public String getModelIdentifier() {
		return MODEL_IDENTIFIER;
	}

	public IDebugTarget getDebugTarget() {
		return this;
	}

	public ILaunch getLaunch() {
		return launch;
	}

	public boolean canTerminate() {
		return !isTerminated;
	}

	public boolean isTerminated() {
		return isTerminated;
	}

	public void terminate() {
		this.threads = new RubyThread[0] ;
		isTerminated = true;
	}

	public boolean canResume() {
		return false;
	}

	public boolean canSuspend() {
		return false;
	}

	public boolean isSuspended() {
		return false;
	}

	public void resume() throws DebugException {
	}

	public void suspend() throws DebugException {
	}

	public void breakpointAdded(IBreakpoint breakpoint) {
		this.getRubyDebuggerProxy().addBreakpoint(breakpoint) ;
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta arg1) {
		this.getRubyDebuggerProxy().removeBreakpoint(breakpoint) ;		
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta arg1) {
		// is called e.g. after a line has been inserted before a breakpoint
		// but then the debugger is out of sync with the file anyway, so debugging
		// should be stopped here.
	}

	public boolean canDisconnect() {
		return false;
	}

	public void disconnect() throws DebugException {
	}

	public boolean isDisconnected() {
		return false;
	}

	public boolean supportsStorageRetrieval() {
		return false;
	}

	public IMemoryBlock getMemoryBlock(long arg0, long arg1) throws DebugException {
		return null;
	}

	public Object getAdapter(Class arg0) {
		return null;
	}

	public IProcess getProcess() {
		return process;
	}

	public void setProcess(IProcess process) {
		this.process = process;
	}

	public RubyDebuggerProxy getRubyDebuggerProxy() {
		return rubyDebuggerProxy;
	}

	public void setRubyDebuggerProxy(RubyDebuggerProxy rubyDebuggerProxy) {
		this.rubyDebuggerProxy = rubyDebuggerProxy;
	}

}
