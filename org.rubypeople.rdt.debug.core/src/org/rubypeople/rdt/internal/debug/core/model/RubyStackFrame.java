package org.rubypeople.rdt.internal.debug.core.model;

import java.util.ArrayList;
import java.util.Vector;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IRegisterGroup;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IVariable;
import org.rubypeople.rdt.internal.debug.core.RubyDebuggerProxy;
import org.rubypeople.rdt.internal.debug.core.StepSuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;


public class RubyStackFrame implements IStackFrame {

	private RubyThread thread;
	private String file;
	private int lineNumber;
	private int index;
	private RubyVariable[] variables;
	public RubyStackFrame(RubyThread thread, String file, int line, int index) {
		this.lineNumber = line;
		this.index = index;
		this.file = file;
		this.thread = thread;
	} 
	
	public IThread getThread() {
		return thread;
	}
	
	public void setThread(RubyThread thread) {
		this.thread = thread;
	}
	
	public IVariable[] getVariables() throws DebugException {
		if (variables == null) {
			variables = this.getRubyDebuggerProxy().readVariables(this);
		}
		return variables;
	}
	
	public boolean hasVariables() throws DebugException {
		if (variables == null) {
			return false;
		}
		return variables.length > 0;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
	
	public int getCharStart() throws DebugException {
		// charStart = 0  and charEnd = -1 is just the way thes variables
		// have to be set in order to make the editor jump to the line, once
		// a breakpoint has occurred. 
		// see LaunchView::openEditorAndSetMarker
		return 0;
	}
	
	public int getCharEnd() throws DebugException {
		// charStart = 0  and charEnd = -1 is just the way thes variables
		// have to be set in order to make the editor jump to the line, once
		// a breakpoint has occurred. 
		// see LaunchView::openEditorAndSetMarker		
		return -1;
	}
	
	public String getName() {
		return file + ":" + this.getLineNumber();
	}

	public String getFileName() {
		return file;
	}
	
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return null;
	}
	
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	}
	
	public String getModelIdentifier() {
		return this.getThread().getModelIdentifier();
	}
	
	public IDebugTarget getDebugTarget() {
		return this.getThread().getDebugTarget();
	}
	
	public ILaunch getLaunch() {
		return this.getDebugTarget().getLaunch();
	}
	
	public boolean canStepInto() {
		return canResume();
	}
	
	public boolean canStepOver() {
		return canResume();
	}
	
	public boolean canStepReturn() {
		return canResume();
	}
	
	public boolean isStepping() {
		return false;
	}
	
	public void stepInto() throws DebugException {
		thread.setSuspended(false);
		new StepThread(this, "StepInto").start();
		DebugEvent ev = new DebugEvent(this.getThread(), DebugEvent.RESUME, DebugEvent.STEP_INTO);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
	}
	
	public void stepOver() throws DebugException {
		thread.setSuspended(false);
		new StepThread(this, "StepOver").start();
		DebugEvent ev = new DebugEvent(this.getThread(), DebugEvent.RESUME, DebugEvent.STEP_OVER);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
	}

	private void suspendAfterStep(SuspensionPoint suspension) {
		if (suspension == null) { // program has terminated
			this.getRubyDebuggerProxy().stop();
			return;
		}
		if (!suspension.isStep()) {
			((RubyDebugTarget) this.getDebugTarget()).suspensionOccurred(suspension) ;
			return ;
		}
		StepSuspensionPoint stepSuspension = (StepSuspensionPoint) suspension ;
		if (!stepSuspension.getFile().equals(this.file) || stepSuspension.getFramesNumber() != thread.getStackFramesSize()) {
			System.out.println("Rereading Frames after step.");
			this.getRubyDebuggerProxy().readFrames((RubyThread) this.getThread());
		}
		this.lineNumber = suspension.getLine();
		variables = null;
		thread.setSuspended(true);
		DebugEvent ev = new DebugEvent(this.getThread(), DebugEvent.SUSPEND, DebugEvent.STEP_END);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
	} 
	
	public void stepReturn() throws DebugException {
		thread.setSuspended(false);
		new StepThread(this, "StepReturn").start();
		DebugEvent ev = new DebugEvent(this.getThread(), DebugEvent.RESUME, DebugEvent.STEP_RETURN);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });		
	}
	
	public boolean canResume() {
		return this.getThread().canResume();
	}
	
	public boolean canSuspend() {
		return this.getThread().canSuspend();
	}
	
	public boolean isSuspended() {
		return this.getThread().isSuspended();
	} 
	
	public void resume() throws DebugException {
		this.getThread().resume();
	}
	
	public void suspend() throws DebugException {
	}
	
	public boolean canTerminate() {
		return this.getThread().canTerminate();
	}
	
	public boolean isTerminated() {
		return this.getThread().isTerminated();
	} 
	
	public void terminate() throws DebugException {

	} 
	
	public Object getAdapter(Class arg0) {
		return null;
	}

	public int getIndex() {
		return index;
	}

	public RubyDebuggerProxy getRubyDebuggerProxy() {
		return thread.getRubyDebuggerProxy();
	}
	
	class StepThread extends Thread {
		RubyStackFrame frame;
		String mode;
		public StepThread(RubyStackFrame frame, String mode) {
			this.frame = frame;
			this.mode = mode;
		}

		public void run() {
			if (mode.equals("StepOver")) {
				frame.suspendAfterStep(getRubyDebuggerProxy().readStepOverEnd(RubyStackFrame.this));
			} else if (mode.equals("StepInto")) {
				frame.suspendAfterStep(getRubyDebuggerProxy().readStepIntoEnd(frame));			
			} else if (mode.equals("StepReturn")) {
				frame.suspendAfterStep(getRubyDebuggerProxy().readStepReturnEnd(frame));
			}			
		}
	}
	
}
