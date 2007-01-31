package org.rubypeople.rdt.internal.debug.core;

import java.io.IOException;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.rubypeople.rdt.internal.debug.core.commands.AbstractDebuggerConnection;
import org.rubypeople.rdt.internal.debug.core.commands.BreakpointCommand;
import org.rubypeople.rdt.internal.debug.core.commands.ClassicDebuggerConnection;
import org.rubypeople.rdt.internal.debug.core.commands.GenericCommand;
import org.rubypeople.rdt.internal.debug.core.commands.RubyDebugConnection;
import org.rubypeople.rdt.internal.debug.core.model.IRubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.rubypeople.rdt.internal.debug.core.model.ThreadInfo;
import org.rubypeople.rdt.internal.debug.core.parsing.AbstractReadStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.ErrorReader;
import org.rubypeople.rdt.internal.debug.core.parsing.FramesReader;
import org.rubypeople.rdt.internal.debug.core.parsing.LoadResultReader;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;
import org.rubypeople.rdt.internal.debug.core.parsing.ThreadInfoReader;
import org.rubypeople.rdt.internal.debug.core.parsing.VariableReader;

public class RubyDebuggerProxy {

	public final static String DEBUGGER_ACTIVE_KEY = "org.rubypeople.rdt.debug.ui.debuggerActive";
	private AbstractDebuggerConnection debuggerConnection;
	private IRubyDebugTarget debugTarget;
	private RubyLoop rubyLoop;
	private ICommandFactory commandFactory;

	public RubyDebuggerProxy(IRubyDebugTarget debugTarget, String rubyFileDirectory, boolean isRubyDebug) {
		this.debugTarget = debugTarget;
		debugTarget.setRubyDebuggerProxy(this);
		commandFactory = isRubyDebug ? new RubyDebugCommandFactory() : new ClassicDebuggerCommandFactory();
		debuggerConnection = isRubyDebug ? new RubyDebugConnection(rubyFileDirectory, debugTarget.getPort()) : new ClassicDebuggerConnection(debugTarget.getPort());
	}

	public boolean checkConnection() {
		return debuggerConnection.isCommandPortConnected();
	}

	public void start() throws RubyProcessingException, IOException {
		
			debuggerConnection.connect();
			this.setBreakPoints();
			this.startRubyLoop();
	}

	public void stop() {
		if (rubyLoop == null) {
			// only in tests, where no real connection is established
			return;
		}
		rubyLoop.setShouldStop();
		rubyLoop.interrupt();
	}

	protected void setBreakPoints() throws IOException {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(IRubyDebugTarget.MODEL_IDENTIFIER);
		for (int i = 0; i < breakpoints.length; i++) {
			this.addBreakpoint(breakpoints[i]);
		}
	}

	public void addBreakpoint(IBreakpoint breakpoint) {
		try {
			if (breakpoint.isEnabled()) {
				if (breakpoint instanceof RubyExceptionBreakpoint) {
					// TODO: check result
					String command = commandFactory.createCatchOn(breakpoint) ;
					new BreakpointCommand(command).execute(debuggerConnection) ;
				} else if (breakpoint instanceof RubyLineBreakpoint) {
					RubyLineBreakpoint rubyLineBreakpoint = (RubyLineBreakpoint) breakpoint;
					String command = commandFactory.createAddBreakpoint(rubyLineBreakpoint.getFileName(), rubyLineBreakpoint.getLineNumber());
					int index = new BreakpointCommand(command).executeWithResult(debuggerConnection);
					rubyLineBreakpoint.setIndex(index);
				}
			}
		} catch (IOException e) {
			RdtDebugCorePlugin.log(e);
		} catch (CoreException e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public void removeBreakpoint(IBreakpoint breakpoint) {
		try {
			if (breakpoint instanceof RubyExceptionBreakpoint) {
				// so far we allow only one catch exception
				// catch off must be set in the case that the enablement has
				// changed to disabled
				String command = commandFactory.createCatchOff();
				new BreakpointCommand(command).execute(debuggerConnection) ;
			} else if (breakpoint instanceof RubyLineBreakpoint) {
				RubyLineBreakpoint rubyLineBreakpoint = (RubyLineBreakpoint) breakpoint;
				if (rubyLineBreakpoint.getIndex() != -1) {
					String command = commandFactory.createRemoveBreakpoint(rubyLineBreakpoint.getIndex());
					// TODO: check for errors
					int deletedIndex = new BreakpointCommand(command).executeWithResult(debuggerConnection);
					rubyLineBreakpoint.setIndex(-1);
				}
			}
		} catch (IOException e) {
			RdtDebugCorePlugin.log(e);
		}

	}

	public void updateBreakpoint(IBreakpoint breakpoint, IMarkerDelta markerDelta) {
		int currentline = markerDelta.getAttribute(IMarker.LINE_NUMBER, -1);
		try {
			if (currentline == ((RubyLineBreakpoint) breakpoint).getLineNumber()) {
				return;
			}
			this.removeBreakpoint(breakpoint);
			this.addBreakpoint(breakpoint);
		} catch (CoreException e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public void startRubyLoop() throws DebuggerNotFoundException, IOException {
		debuggerConnection.start();
		rubyLoop = new RubyLoop();
		rubyLoop.start();
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					while (true) {
						new ErrorReader(debuggerConnection.getCommandReadStrategy()).read();
					}
				} catch (Exception e) {
					RdtDebugCorePlugin.log(e);
				}
			};
		};
		new Thread(runnable).start();
	}

	public void resume(RubyThread thread) {
		try {
			println(commandFactory.createResume(thread));
		} catch (IOException e) {
			// terminate ?
		}
	}

	protected void println(String s) throws IOException {
		try {
			// TOOD: GenericCommand is only temporary solution
			new GenericCommand(s, false /* isControl */).execute(debuggerConnection);
		} catch (IOException e) {
			RdtDebugCorePlugin.debug("Could not send to debugger. Exception occured.", e);
			throw e;
		}
	}

	protected IRubyDebugTarget getDebugTarget() {
		return debugTarget;
	}

	public RubyVariable[] readVariables(RubyStackFrame frame) {
		try {
			this.println(commandFactory.createReadLocalVariables(frame));
			return new VariableReader(getMultiReaderStrategy()).readVariables(frame);
		} catch (Exception ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}

	public RubyVariable[] readInstanceVariables(RubyVariable variable) {
		try {
			this.println(commandFactory.createReadInstanceVariable(variable));
			return new VariableReader(getMultiReaderStrategy()).readVariables(variable);
		} catch (Exception ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}

	public RubyVariable readInspectExpression(RubyStackFrame frame, String expression) throws RubyProcessingException {
		try {
			this.println(commandFactory.createInspect(frame, expression));
			RubyVariable[] variables = new VariableReader(getMultiReaderStrategy()).readVariables(frame);
			if (variables.length == 0) {
				return null;
			} else {
				return variables[0];
			}
		} catch (IOException ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}

	public void sendStepOverEnd(RubyStackFrame stackFrame) {
		try {
			this.println(commandFactory.createStepOver(stackFrame));
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);

		}
	}

	public void sendStepReturnEnd(RubyStackFrame stackFrame) {
		try {
			this.println(commandFactory.createStepReturn(stackFrame));
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public void sendStepIntoEnd(RubyStackFrame stackFrame) {
		try {
			this.println(commandFactory.createStepInto(stackFrame));
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public RubyStackFrame[] readFrames(RubyThread thread) {
		try {
			this.println(commandFactory.createReadFrames(thread));
			return new FramesReader(getMultiReaderStrategy()).readFrames(thread);
		} catch (IOException e) {
			RdtDebugCorePlugin.log(e);
			return null;
		}

	}

	public ThreadInfo[] readThreads() {
		try {
			this.println(commandFactory.createReadThreads());
			return new ThreadInfoReader(getMultiReaderStrategy()).readThreads();
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
			return null;
		}
	}

	public LoadResultReader.LoadResult readLoadResult(String filename) {
		try {
			this.println(commandFactory.createLoad(filename));
			return new LoadResultReader(getMultiReaderStrategy()).readLoadResult();
		} catch (Exception e) {
			return null;
		}
	}

	public void closeConnection() throws IOException {
		debuggerConnection.exit();
	}

	private AbstractReadStrategy getMultiReaderStrategy() {
		return debuggerConnection.getCommandReadStrategy();
	}

	class RubyLoop extends Thread {

		public RubyLoop() {
			this.setName("RubyDebuggerLoop");
		}

		public void setShouldStop() {}

		public void run() {
			try {
				System.setProperty(DEBUGGER_ACTIVE_KEY, "true");
				
				// TODO
				//getDebugTarget().updateThreads();
				RdtDebugCorePlugin.debug("Waiting for breakpoints.");
				while (true) {
					final SuspensionPoint hit = new SuspensionReader(getMultiReaderStrategy()).readSuspension();
					if (hit == null) {
						break;
					}
					RdtDebugCorePlugin.debug(hit);
					// TODO: should this be using the JOB API?
					new Thread() {

						public void run() {
							getDebugTarget().suspensionOccurred(hit);
						}
					}.start();
				}
			} catch (DebuggerNotFoundException ex) {
				throw ex;
			} catch (Exception ex) {
				RdtDebugCorePlugin.debug("Exception in socket reader loop.", ex);
			} finally {
				System.setProperty(DEBUGGER_ACTIVE_KEY, "false");
				getDebugTarget().terminate();
				try {
					closeConnection();
				} catch (IOException e) {
					RdtDebugCorePlugin.log(e);
				}
				RdtDebugCorePlugin.debug("Socket reader loop finished.");
			}
		}
	}


}
