package org.rubypeople.rdt.internal.debug.core.model;

import java.util.Vector;

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

public class RubyThread implements IThread {
	private RubyStackFrame[] frames;
	private IDebugTarget target;
	private boolean isSuspended = false;
	private boolean isTerminated = false;
	private boolean isStepping = false;
	private String name ;
	private int id ;
	
	public RubyThread(IDebugTarget target, int id) {
		this.target = target;
		this.setId(id) ;
		this.createName() ;
	}

	public IStackFrame[] getStackFrames() throws DebugException {
		return frames;
	}

	public int getStackFramesSize() {
		return frames.length;
	}

	public boolean hasStackFrames() {
		if (frames == null) {
			return false;
		}
		return frames.length > 0;
	}

	public int getPriority() throws DebugException {
		return 0;
	}

	public IStackFrame getTopStackFrame() throws DebugException {
		if (frames == null || frames.length == 0) {
			return null;
		}
		return (IStackFrame) frames[0];
	}


	public IBreakpoint[] getBreakpoints() {
		return null;
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

	public void resume() throws DebugException {
		isSuspended = false;
		this.createName() ;
		this.frames = null ;
		DebugEvent ev = new DebugEvent(this, DebugEvent.RESUME, DebugEvent.CLIENT_REQUEST);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
		((RubyDebugTarget) this.getDebugTarget()).getRubyDebuggerProxy().resume();
	}

	public void doSuspend(SuspensionPoint suspensionPoint) {
		this.getRubyDebuggerProxy().readFrames(this);
		this.createName(suspensionPoint) ;
		this.suspend() ;
	}

	public void suspend()  {
		isStepping = false ;
		isSuspended = true;
		DebugEvent ev = new DebugEvent(this, DebugEvent.SUSPEND, DebugEvent.BREAKPOINT);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
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
		isStepping = true ;
		this.createName() ;
		this.frames = null ;
		frames[0].stepInto();
	}

	public void stepOver() throws DebugException {
		isStepping = true ;		
		this.createName() ;
		this.frames = null ;		
		frames[0].stepOver() ;
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
		isTerminated = true;
		this.frames = null;
	}

	public Object getAdapter(Class arg0) {
		return null;
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

	protected void createName() {
		this.createName(null) ;	
	}
	
	protected void createName(SuspensionPoint suspensionPoint) {
		this.name = "Ruby Thread - " + this.getId()  ;
		if (suspensionPoint != null) { 
			this.name += " (" + suspensionPoint + ")" ;
		}
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

}
