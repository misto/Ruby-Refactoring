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
import org.rubypeople.rdt.internal.debug.core.SuspensionPoint;
import org.rubypeople.rdt.internal.debug.core.parsing.StepEndReader;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RubyStackFrame implements IStackFrame {

	class StepThread extends Thread {
		RubyStackFrame frame;
		String mode;
		public StepThread(RubyStackFrame frame, String mode) {
			this.frame = frame;
			this.mode = mode;
		}

		public void run() {
			if (mode.equals("StepOver")) {
				frame.suspendAfterStep(getRubyDebuggerProxy().readStepOverEnd(frame));
			} else {
				frame.suspendAfterStep(getRubyDebuggerProxy().readStepIntoEnd(frame));
			}
		}
	}

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
	} /**
			 * @see org.eclipse.debug.core.model.IStackFrame#getThread()
			 */
	public IThread getThread() {
		return thread;
	}
	public void setThread(RubyThread thread) {
		this.thread = thread;
	} /**
			 * @see org.eclipse.debug.core.model.IStackFrame#getVariables()
			 */
	public IVariable[] getVariables() throws DebugException {
		if (variables == null) {
			variables = this.getRubyDebuggerProxy().readVariables(this);
		}
		return variables;
	} /**
			 * @see org.eclipse.debug.core.model.IStackFrame#hasVariables()
			 */
	public boolean hasVariables() throws DebugException {
		if (variables == null) {
			return false;
		}
		return variables.length > 0;
	} /**
			 * @see org.eclipse.debug.core.model.IStackFrame#getLineNumber()
			 */
	public int getLineNumber() {
		return lineNumber;
	} /**
			 * @see org.eclipse.debug.core.model.IStackFrame#getCharStart()
			 */
	public int getCharStart() throws DebugException {
		// charStart = 0  and charEnd = -1 is just the way thes variables
		// have to be set in order to make the editor jump to the line, once
		// a breakpoint has occurred. 
		// see LaunchView::openEditorAndSetMarker
		return 0;
	} /**
		 * @see org.eclipse.debug.core.model.IStackFrame#getCharEnd()
		 */
	public int getCharEnd() throws DebugException {
		// charStart = 0  and charEnd = -1 is just the way thes variables
		// have to be set in order to make the editor jump to the line, once
		// a breakpoint has occurred. 
		// see LaunchView::openEditorAndSetMarker		
		return -1;
	} /**
		 * @see org.eclipse.debug.core.model.IStackFrame#getName()
		 */
	public String getName() {
		return file + ":" + this.getLineNumber();
	}

	public String getFileName() {
		return file;
	} /**
			 * @see org.eclipse.debug.core.model.IStackFrame#getRegisterGroups()
			 */
	public IRegisterGroup[] getRegisterGroups() throws DebugException {
		return null;
	} /**
			 * @see org.eclipse.debug.core.model.IStackFrame#hasRegisterGroups()
			 */
	public boolean hasRegisterGroups() throws DebugException {
		return false;
	} /**
			 * @see org.eclipse.debug.core.model.IDebugElement#getModelIdentifier()
			 */
	public String getModelIdentifier() {
		return this.getThread().getModelIdentifier();
	} /**
			 * @see org.eclipse.debug.core.model.IDebugElement#getDebugTarget()
			 */
	public IDebugTarget getDebugTarget() {
		return this.getThread().getDebugTarget();
	} /**
			 * @see org.eclipse.debug.core.model.IDebugElement#getLaunch()
			 */
	public ILaunch getLaunch() {
		return this.getDebugTarget().getLaunch();
	} /**
			 * @see org.eclipse.debug.core.model.IStep#canStepInto()
			 */
	public boolean canStepInto() {
		return canResume();
	} /**
			 * @see org.eclipse.debug.core.model.IStep#canStepOver()
			 */
	public boolean canStepOver() {
		return canResume();
	} /**
			 * @see org.eclipse.debug.core.model.IStep#canStepReturn()
			 */
	public boolean canStepReturn() {
		return false;
	} /**
			 * @see org.eclipse.debug.core.model.IStep#isStepping()
			 */
	public boolean isStepping() {
		return false;
	} /**
			 * @see org.eclipse.debug.core.model.IStep#stepInto()
			 */
	public void stepInto() throws DebugException {
		thread.setSuspended(false);
		new StepThread(this, "StepInto").start();
		DebugEvent ev = new DebugEvent(this.getThread(), DebugEvent.RESUME, DebugEvent.STEP_INTO);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
	} /**
			 * @see org.eclipse.debug.core.model.IStep#stepOver()
			 */
	public void stepOver() throws DebugException {
		thread.setSuspended(false);
		new StepThread(this, "StepOver").start();
		DebugEvent ev = new DebugEvent(this.getThread(), DebugEvent.RESUME, DebugEvent.STEP_OVER);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
	}

	private void suspendAfterStep(SuspensionPoint stepInfo) {
		if (stepInfo == null) { // program has terminated
			this.getRubyDebuggerProxy().stop();
			return;
		}
		if (!stepInfo.getFile().equals(this.file) || stepInfo.getFramesNumber() != thread.getStackFramesSize()) {
			System.out.println("Rereading Frames after step.");
			this.getRubyDebuggerProxy().readFrames((RubyThread) this.getThread());
		}
		this.lineNumber = stepInfo.getLine();
		variables = null;
		thread.setSuspended(true);
		DebugEvent ev = new DebugEvent(this.getThread(), DebugEvent.SUSPEND, DebugEvent.STEP_END);
		DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
	} /**
			 * @see org.eclipse.debug.core.model.IStep#stepReturn()
			 */
	public void stepReturn() throws DebugException {
	} /**
		 * @see org.eclipse.debug.core.model.ISuspendResume#canResume()
		 */
	public boolean canResume() {
		return this.getThread().canResume();
	} /**
			 * @see org.eclipse.debug.core.model.ISuspendResume#canSuspend()
			 */
	public boolean canSuspend() {
		return this.getThread().canSuspend();
	} /**
			 * @see org.eclipse.debug.core.model.ISuspendResume#isSuspended()
			 */
	public boolean isSuspended() {
		return this.getThread().isSuspended();
	} /**
			 * @see org.eclipse.debug.core.model.ISuspendResume#resume()
			 */
	public void resume() throws DebugException {
		this.getThread().resume();
	} /**
			 * @see org.eclipse.debug.core.model.ISuspendResume#suspend()
			 */
	public void suspend() throws DebugException {
	} /**
		 * @see org.eclipse.debug.core.model.ITerminate#canTerminate()
		 */
	public boolean canTerminate() {
		return this.getThread().canTerminate();
	} /**
			 * @see org.eclipse.debug.core.model.ITerminate#isTerminated()
			 */
	public boolean isTerminated() {
		return this.getThread().isTerminated();
	} /**
			 * @see org.eclipse.debug.core.model.ITerminate#terminate()
			 */
	public void terminate() throws DebugException {

	} /**
		 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(Class)
		 */
	public Object getAdapter(Class arg0) {
		return null;
	}

	public int getIndex() {
		return index;
	}

	public RubyDebuggerProxy getRubyDebuggerProxy() {
		return thread.getRubyDebuggerProxy();
	}
}
