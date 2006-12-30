package org.rubypeople.rdt.internal.debug.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.rubypeople.rdt.internal.debug.core.model.IRubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.rubypeople.rdt.internal.debug.core.model.ThreadInfo;
import org.rubypeople.rdt.internal.debug.core.parsing.BreakpointAddedReader;
import org.rubypeople.rdt.internal.debug.core.parsing.ErrorReader;
import org.rubypeople.rdt.internal.debug.core.parsing.EvalReader;
import org.rubypeople.rdt.internal.debug.core.parsing.FramesReader;
import org.rubypeople.rdt.internal.debug.core.parsing.LoadResultReader;
import org.rubypeople.rdt.internal.debug.core.parsing.MultiReaderStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;
import org.rubypeople.rdt.internal.debug.core.parsing.ThreadInfoReader;
import org.rubypeople.rdt.internal.debug.core.parsing.VariableReader;
import org.rubypeople.rdt.internal.debug.core.parsing.XmlStreamReaderException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class RubyDebuggerProxy {

	public final static String DEBUGGER_ACTIVE_KEY = "org.rubypeople.rdt.debug.ui.debuggerActive";
	private Socket socket;
	private PrintWriter writer;
	private IRubyDebugTarget debugTarget;
	private RubyLoop rubyLoop;
	private XmlPullParser xpp;
	protected MultiReaderStrategy multiReaderStrategy;
	private ICommandFactory commandFactory;
	private final boolean isRubyDebug;

	public RubyDebuggerProxy(IRubyDebugTarget debugTarget, boolean isRubyDebug) {
		this.debugTarget = debugTarget;
		this.isRubyDebug = isRubyDebug;
		debugTarget.setRubyDebuggerProxy(this);
		commandFactory = isRubyDebug ? new RubyDebugCommandFactory() : new ClassicDebuggerCommandFactory();
	}

	public boolean checkConnection() {
		try {
			return this.getSocket().isConnected();
		} catch (DebuggerNotFoundException ex) {
			return false;
		} catch (IOException ex) {
			return false;
		}
	}

	public String registerRdebugExtension(String pathToRdebugExtension) throws IOException, RubyProcessingException {
		// should be called before start
		if (!isRubyDebug) {
			return "false";
		}
		try {
			// TODO: do not let the debugger stop on the first line
			new SuspensionReader(getMultiReaderStrategy()).readSuspension();
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
		}
		String expression = "eval require '" + pathToRdebugExtension + "'";
		println(expression);
		EvalReader reader = new EvalReader(getMultiReaderStrategy());
		return reader.readEvalResult(); // throws
		// RubyProcessingException
	}

	public void start() throws RubyProcessingException {
		try {
			this.setBreakPoints();
			this.startRubyLoop();
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public void stop() {
		if (rubyLoop == null) {
			// only in tests, where no real connection is established
			return;
		}
		rubyLoop.setShouldStop();
		rubyLoop.interrupt();
	}

	protected Socket acquireSocket() throws IOException {

		int tryCount = 10;
		for (int i = 0; i < tryCount; i++) {
			try {
				socket = new Socket("localhost", debugTarget.getPort());
				return socket;
			} catch (IOException e) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e1) {}
			}
		}
		return null;

	}

	protected Socket getSocket() throws IOException, DebuggerNotFoundException {

		if (socket == null) {
			socket = acquireSocket();
			if (socket == null) {
				throw new DebuggerNotFoundException();
			}
		}
		return socket;
	}

	public PrintWriter getWriter() throws IOException, DebuggerNotFoundException {
		if (writer == null) {
			writer = new PrintWriter(this.getSocket().getOutputStream(), true);
		}
		return writer;
	}

	public void setWriter(PrintWriter writer) {
		this.writer = writer;
	}

	public XmlPullParser getXpp() {
		if (xpp == null) {
			try {
				XmlPullParserFactory factory = XmlPullParserFactory.newInstance("org.kxml2.io.KXmlParser,org.kxml2.io.KXmlSerializer", null);
				xpp = factory.newPullParser();
				xpp.setInput(getSocket().getInputStream(), "UTF-8");
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return xpp;
	}

	public void setXpp(XmlPullParser xpp) {
		this.xpp = xpp;
	}

	protected synchronized MultiReaderStrategy getMultiReaderStrategy() {
		if (this.multiReaderStrategy == null) {
			this.multiReaderStrategy = new MultiReaderStrategy(this.getXpp());
		}
		return multiReaderStrategy;
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
					this.println(commandFactory.createCatchOn(breakpoint));
				} else if (breakpoint instanceof RubyLineBreakpoint) {
					RubyLineBreakpoint rubyLineBreakpoint = (RubyLineBreakpoint) breakpoint;
					String command = commandFactory.createAddBreakpoint(rubyLineBreakpoint.getFileName(), rubyLineBreakpoint.getLineNumber());
					this.println(command);
					int index = readBreakpointIndex();
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
				this.println(commandFactory.createCatchOff());
			} else if (breakpoint instanceof RubyLineBreakpoint) {
				RubyLineBreakpoint rubyLineBreakpoint = (RubyLineBreakpoint) breakpoint;
				if (rubyLineBreakpoint.getIndex() != -1) {
					String command = commandFactory.createRemoveBreakpoint(rubyLineBreakpoint.getIndex());
					this.println(command);
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

	public void startRubyLoop() {
		rubyLoop = new RubyLoop();
		rubyLoop.start();
		Runnable runnable = new Runnable() {
			public void run() {
				try {
					while (true) {
						new ErrorReader(getMultiReaderStrategy()).read();
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
		RdtDebugCorePlugin.debug("Sending debugger: " + s);
		try {
			this.getWriter().println(s);
		} catch (IOException e) {
			RdtDebugCorePlugin.debug("Could not send to debugger. Exception occured.", e);
			throw e;
		}
	}

	protected IRubyDebugTarget getDebugTarget() {
		return debugTarget;
	}

	public int readBreakpointIndex() {
		try {
			return new BreakpointAddedReader(getMultiReaderStrategy()).readBreakpointNo();
		} catch (Exception ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
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

	public void closeSocket() throws IOException {
		if (socket != null) {
			socket.close();
		}
	}

	class RubyLoop extends Thread {

		public RubyLoop() {
			this.setName("RubyDebuggerLoop");
		}

		public void setShouldStop() {}

		public void run() {
			try {
				System.setProperty(DEBUGGER_ACTIVE_KEY, "true");
				getDebugTarget().updateThreads();
				println("cont");
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
					closeSocket();
				} catch (IOException e) {
					RdtDebugCorePlugin.log(e);
				}
				RdtDebugCorePlugin.debug("Socket reader loop finished.");
			}
		}
	}
}
