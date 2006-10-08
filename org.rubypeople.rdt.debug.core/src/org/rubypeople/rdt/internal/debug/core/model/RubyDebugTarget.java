package org.rubypeople.rdt.internal.debug.core.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
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
import org.rubypeople.rdt.core.SocketUtil;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;

// RubyDebugTarget extends PlatformObject in order to benefit from the getAdapter Implementation
// provided there. This is crucial for displaying the RubyDebugTarget in the DebugView when
// the TreeContentManager asks for an IDeferredWorkbenchAdapter or IWorkbenchAdapter
// This kind of Adapter is deliverd from the DebugElementAdapterFactory.

public class RubyDebugTarget extends PlatformObject implements IRubyDebugTarget {
	private static int DEFAULT_PORT = 1098 ;
	private IProcess process;
	private boolean isTerminated;
	private ILaunch launch;
	private RubyThread[] threads;
	private RubyDebuggerProxy rubyDebuggerProxy;
	private int port = -1 ;
	private File debugParameterFile;

	public RubyDebugTarget(ILaunch launch) {
		this(launch, null) ;
	}
	
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
				DebugEvent ev = new DebugEvent(updatedThreads[i], DebugEvent.CREATE);
				DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });				
			} else {
				updatedThreads[i] = threads[threadIndex];
			}
		}
		threads = updatedThreads;

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
		System.out.println("THREADS: " + threads.length) ;
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
		if (isTerminated) {
			return ;
		}
		try {
			this.getProcess().terminate() ;
			this.threads = new RubyThread[0] ;
			isTerminated = true;
			rubyDebuggerProxy.stop() ;
		} catch (DebugException e) {
			RdtDebugCorePlugin.debug("Exception while terminating process.", e) ;
		}
		
		// launch is one of the listeners
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] {new DebugEvent(this, DebugEvent.TERMINATE)});
		
		// delete the debugParameteFile if it could be created
		if (debugParameterFile.exists()) {
			boolean deleted = debugParameterFile.delete() ;
			if (!deleted) {
				RdtDebugCorePlugin.debug("Could not delete debugParameteFile:" + debugParameterFile.toURI()) ;
			}
		}
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
		if (isTerminated) {
			return ;
		}
		this.getRubyDebuggerProxy().addBreakpoint(breakpoint) ;
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta arg1) {
		if (isTerminated) {
			return ;
		}		
		this.getRubyDebuggerProxy().removeBreakpoint(breakpoint) ;		
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta arg1) {
		// is called e.g. after a line has been inserted before a breakpoint
		// or the enablement status has changed
		// in the first case it is essential that the debugger has reloaded the file
		// so that the breakpoint moving is in synch with the new file
		if (isTerminated) {
			return ;
		}	
		this.getRubyDebuggerProxy().updateBreakpoint(breakpoint, arg1) ;
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
	
	public File getDebugParameterFile() {
		if (debugParameterFile == null) {
			try {
				debugParameterFile = File.createTempFile("eclipseDebug",".rb") ;
			} catch (IOException e) {
				RdtDebugCorePlugin.log("Could not create debugParameterFile", e) ;
			}
		}
		return debugParameterFile ;
	}
	
	public boolean addDebugParameter(String line) {
		try {
			FileWriter writer = new FileWriter(this.getDebugParameterFile());
			new PrintWriter(writer).println(line);
			writer.flush();
			writer.close();
			return true;
		} catch (IOException ex) {
			RdtDebugCorePlugin.log(ex);
			return false;
		}
	}
	
	public int getPort() {
		if (port == -1) {
			port = SocketUtil.findFreePort() ;
			// port can still be -1, if findFreePort fails
			if (port != -1) {
				// see eclipseDebug.rb for how $EclipseListenPort is used from ruby side
				if (!addDebugParameter("$EclipseListenPort=" + port)) {
					port = -1 ;
				}
			}
			// if we couldn't find a free port a write the free port to the file, use the default
			if (port == -1) {
				port = DEFAULT_PORT ;
			}
			RdtDebugCorePlugin.debug("Using port: " + port) ;
		}		
		return port ;
	}
	
	public boolean isUsingDefaultPort() {
		return this.getPort() == DEFAULT_PORT ;
	}
}
