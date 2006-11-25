package org.rubypeople.rdt.internal.debug.core.model;

import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;

// see RubyDebugTarget for the reason why PlatformObject is being extended
public class RubyThread extends PlatformObject implements IThread {
	private RubyStackFrame[] frames;

	private IDebugTarget target;

	private boolean isSuspended = false;

	private boolean isTerminated = false;

	private boolean isStepping = false;

	private String name;

	private int id;

	public RubyThread(IDebugTarget target, int id) {
		this.target = target;
		this.setId(id);
		this.updateName();
	}

	public IStackFrame[] getStackFrames() {
		// Not all clients ask hasStackFrames before calling this method
		// (DeferredThread)
		// Therefore we must not return null
		// Since 3.2: It seems as if the first method called on a thread is
		// hasStackFrames
		// this is done frome AsynchronousContentAdapter and therefore we have
		// the time to
		// send this call to the debuggger ;
		if (frames == null) {
			createStackFrames();
		}
		return frames;
	}

	private synchronized void  createStackFrames() {
		if (isSuspended()) {
		getRubyDebuggerProxy().readFrames(this);
		for (int i = 0; i < frames.length; i++) {
			RubyStackFrame frame = frames[i];
//			DebugEvent ev = new DebugEvent(frame, DebugEvent.CREATE);
//			DebugPlugin.getDefault().fireDebugEventSet(
//					new DebugEvent[] { ev });
		}
		} else {
			frames = new RubyStackFrame[] {} ;
		}
	}

	public int getStackFramesSize() {
		return frames.length;
	}

	public boolean hasStackFrames() {
		return isSuspended ; //TODO: changegetStackFrames().length > 0;
	}

	public int getPriority() throws DebugException {
		return 0;
	}

	public IStackFrame getTopStackFrame() throws DebugException {
		// TODO: check for empty
		return getStackFrames()[0] ;
	}

	public IBreakpoint[] getBreakpoints() {
		// TODO: Experimental Code
		return new IBreakpoint[] { DebugPlugin.getDefault()
				.getBreakpointManager().getBreakpoints(
						IRubyDebugTarget.MODEL_IDENTIFIER)[0] };
	}

	public String getModelIdentifier() {
		return this.getDebugTarget().getModelIdentifier();
	}

	public IDebugTarget getDebugTarget() {
		return target;
	}

	public ILaunch getLaunch() {
		return this.getDebugTarget().getLaunch();
	}

	public boolean canResume() {
		return isSuspended;
	}

	public boolean canSuspend() {
		return !isSuspended;
	}

	public boolean isSuspended() {
		return isSuspended;
	}

	protected void setSuspended(boolean isSuspended) {
		this.isSuspended = isSuspended;
	}

	/* 
	 * call after user wants to resume or step
	 */
	protected void resume(boolean isStep) {
		isStepping = isStep ;
		isSuspended = false;
		this.updateName();
		this.frames = new RubyStackFrame[] {} ;		
	}
	

	public void resume() throws DebugException {
		resume(false /* isStep*/) ;
		((RubyDebugTarget) this.getDebugTarget()).getRubyDebuggerProxy()
				.resume(this);
		// TODO: resume should be sent from ruby debugger
		DebugEvent ev = new DebugEvent(this, DebugEvent.RESUME, DebugEvent.CLIENT_REQUEST);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
	}

	/*
	 * called when suspension event was sent from ruby debugger
	 */
	public void doSuspend(SuspensionPoint suspensionPoint) {
		int suspensionReason = 0 ;
		if (suspensionPoint.isStep()) {
			suspensionReason = DebugEvent.STEP_END ;
		} else {
			suspensionReason = DebugEvent.BREAKPOINT ;
		}
		DebugEvent ev = new DebugEvent(this, DebugEvent.SUSPEND,
				suspensionReason);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
		frames = null ;
		isSuspended = true;
		isStepping = false;
		this.createName(suspensionPoint);
	}

	public void suspend() {
		frames = null ;
		isStepping = false;
		isSuspended = true;
		// TODO: send suspension command to ruby debugger
	}

	public boolean canStepInto() {
		return isSuspended && this.hasStackFrames();
	}

	public boolean canStepOver() {
		return isSuspended && this.hasStackFrames();
	}

	public boolean canStepReturn() {
		return false;
	}

	public boolean isStepping() {
		return isStepping;
	}

	public void stepInto() throws DebugException {
		isStepping = true;
		this.updateName();
		this.frames = null;
		frames[0].stepInto();
	}

	public void stepOver() throws DebugException {
		if (frames != null && frames.length > 0) {		
			frames[0].stepOver();
		}
	}

	public void stepReturn() throws DebugException {
	}

	public boolean canTerminate() {
		return !isTerminated;
	}

	public boolean isTerminated() {
		return isTerminated;
	}

	public void terminate() throws DebugException {
		this.getDebugTarget().terminate();
		isTerminated = true;
		this.frames = null;
	}

	public RubyDebuggerProxy getRubyDebuggerProxy() {
		return ((RubyDebugTarget) this.getDebugTarget()).getRubyDebuggerProxy();
	}

	public void setStackFrames(RubyStackFrame[] frames) {
		this.frames = frames;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	protected void updateName() {
		this.createName(null);
	}

	protected void createName(SuspensionPoint suspensionPoint) {
		this.name = "Ruby Thread - " + this.getId();
		if (suspensionPoint != null) {
			this.name += " (" + suspensionPoint + ")";
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	

	public Object getAdapter(Class adapterType) {
		// Patch from Shugo Maeda: open debug perspective on suspend
		if (adapterType.equals(ILaunch.class)) {
			return getLaunch();	
		}
		return super.getAdapter(adapterType);
	}
}
