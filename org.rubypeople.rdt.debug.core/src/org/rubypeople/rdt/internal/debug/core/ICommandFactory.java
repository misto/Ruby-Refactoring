package org.rubypeople.rdt.internal.debug.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.model.IBreakpoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public interface ICommandFactory {

	public String createReadFrames(RubyThread thread);

	public String createReadLocalVariables(RubyStackFrame frame);

	public String createReadInstanceVariable(RubyVariable variable);

	public String createStepOver(RubyStackFrame stackFrame);

	public String createStepReturn(RubyStackFrame stackFrame);

	public String createStepInto(RubyStackFrame stackFrame);

	public String createReadThreads();

	public String createInspect(RubyStackFrame frame, String expression);

	public String createResume(RubyThread thread);

	public String createSetBreakpoint(String mode, String file, int line);

	public String createCatchOff();

	public String createCatchOn(IBreakpoint breakpoint) throws CoreException;

	public String createLoad(String filename);
}