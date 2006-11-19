package org.rubypeople.rdt.internal.debug.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public class RubyDebugCommandFactory implements ICommandFactory {

	public String createReadFrames(RubyThread thread) {
		return "w" ;
	}
	
	public String createReadLocalVariables(RubyStackFrame frame) {
		return "frame " + frame.getIndex() + " ; v l " ;
	}
	
	public String createReadInstanceVariable(RubyVariable variable) {
		//((RubyThread) variable.getStackFrame().getThread()).getId()
		return "frame " + variable.getStackFrame().getIndex() + " ; v i " + variable.getObjectId();
	}

	public String createStepOver(RubyStackFrame stackFrame) {
		return "th " + ((RubyThread) stackFrame.getThread()).getId() + " ; next " + stackFrame.getIndex();
	}

	public String createStepReturn(RubyStackFrame stackFrame) {
		return "th " + ((RubyThread) stackFrame.getThread()).getId() + " ; next " + (stackFrame.getIndex() + 1);
	}

	public String createStepInto(RubyStackFrame stackFrame) {
		return "th " + ((RubyThread) stackFrame.getThread()).getId() + " ; step " + stackFrame.getIndex();
	}

	public String createReadThreads() {
		return "th l";
	}

	public String createLoad(String filename) {
		return "load " + filename;
	}

	public String createInspect(RubyStackFrame frame, String expression) {
		return "th " + ((RubyThread) frame.getThread()).getId() + " ; v inspect " + frame.getIndex() + " " + expression;
	}

	public String createResume(RubyThread thread) {
		return "cont";
	}

	public String createSetBreakpoint(String mode, String file, int line) {
		StringBuffer setBreakPointCommand = new StringBuffer();
		setBreakPointCommand.append("b ");
		setBreakPointCommand.append(mode);
		setBreakPointCommand.append(" ");
		setBreakPointCommand.append(file);
		setBreakPointCommand.append(":");
		setBreakPointCommand.append(line);
		return setBreakPointCommand.toString();
	}

	public String createCatchOff() {
		return "catch off";
	}

	public String createCatchOn(IBreakpoint breakpoint) throws CoreException {
		return "catch " + ((RubyExceptionBreakpoint) breakpoint).getException();
	}
}
