package org.rubypeople.rdt.internal.debug.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.rubypeople.rdt.internal.debug.core.model.IRubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.rubypeople.rdt.internal.debug.core.model.ThreadInfo;
import org.rubypeople.rdt.internal.debug.core.parsing.FramesReader;
import org.rubypeople.rdt.internal.debug.core.parsing.MultiReaderStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;
import org.rubypeople.rdt.internal.debug.core.parsing.ThreadInfoReader;
import org.rubypeople.rdt.internal.debug.core.parsing.VariableReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class RubyDebuggerProxy {

	private Socket socket;
	private PrintWriter writer;
	private IRubyDebugTarget debugTarget;
	private RubyLoop rubyLoop;
	private XmlPullParser xpp;
	protected MultiReaderStrategy multiReaderStrategy;

	public RubyDebuggerProxy(IRubyDebugTarget debugTarget) {
		this.debugTarget = debugTarget;
		debugTarget.setRubyDebuggerProxy(this);
	}

	public void start() {
		try {
			this.setBreakPoints();
			this.startRubyLoop();
		} catch (IOException e) {
		}
	}

	public void stop() {
		rubyLoop.setShouldStop();
		rubyLoop.interrupt();
	}

	protected Socket getSocket() throws IOException {
		if (socket == null) {
			socket = new Socket("localhost", 1098);
		}
		return socket;
	}

	public PrintWriter getWriter() throws IOException {
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
				xpp.setInput(new BufferedReader(new InputStreamReader(getSocket().getInputStream())));
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

	protected MultiReaderStrategy getMultiReaderStrategy() {
		if (this.multiReaderStrategy == null) {
			this.multiReaderStrategy = new MultiReaderStrategy(this.getXpp());
		}
		return multiReaderStrategy;
	}

	protected void setBreakPoints() throws IOException {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(IRubyDebugTarget.MODEL_IDENTIFIER);
		for (int i = 0; i < breakpoints.length; i++) {
			printBreakpoint(breakpoints[i], "add");
		}
	}

	public void addBreakpoint(IBreakpoint breakpoint) {
		try {
			this.printBreakpoint(breakpoint, "add");
		} catch (IOException e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public void removeBreakpoint(IBreakpoint breakpoint) {
		try {
			this.printBreakpoint(breakpoint, "remove");
		} catch (IOException e) {
			RdtDebugCorePlugin.log(e);
		}

	}

	protected void printBreakpoint(IBreakpoint breakpoint, String mode) throws IOException {
		StringBuffer setBreakPointCommand = new StringBuffer();
		setBreakPointCommand.append("b ");
		setBreakPointCommand.append(mode);
		setBreakPointCommand.append(" ");
		setBreakPointCommand.append(breakpoint.getMarker().getResource().getName());
		setBreakPointCommand.append(":");
		setBreakPointCommand.append(breakpoint.getMarker().getAttribute(IMarker.LINE_NUMBER, -1));
		this.println(setBreakPointCommand.toString());
	}

	public void startRubyLoop() {
		rubyLoop = new RubyLoop();
		rubyLoop.start();
	}

	public void resume(RubyThread thread) {
		try {
			println("th " + thread.getId() + ";cont");
		} catch (IOException e) {
			// terminate ?
		}
	}

	protected void println(String s) throws IOException {
		System.out.println("Sending debugger: " + s);
		try {
			this.getWriter().println(s);
		} catch (IOException e) {
			System.out.println("Could not send to debugger. Exception occured.");
			e.printStackTrace();
			throw e;
		}
	}

	protected IRubyDebugTarget getDebugTarget() {
		return debugTarget;
	}

	public RubyVariable[] readVariables(RubyStackFrame frame) {
		try {
			this.println("th " + ((RubyThread) frame.getThread()).getId() + " ; v l " + frame.getIndex());
			return new VariableReader(getMultiReaderStrategy()).readVariables(frame);
		} catch (IOException ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}

	public RubyVariable[] readInstanceVariables(RubyVariable variable) {
		try {
			this.println("th " + ((RubyThread) variable.getStackFrame().getThread()).getId() + " ; v i " + variable.getStackFrame().getIndex() + " " + variable.getQualifiedName());
			return new VariableReader(getMultiReaderStrategy()).readVariables(variable);
		} catch (IOException ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}

	public RubyVariable readInspectExpression(RubyStackFrame frame, String expression) {
		try {
			this.println("th " + ((RubyThread) frame.getThread()).getId() + " ; v inspect " + frame.getIndex() + " " + expression);
			RubyVariable[] variables = new VariableReader(getMultiReaderStrategy()).readVariables(frame);
			if (variables.length == 0) {
				return null ;	
			}
			else {
				return variables[0] ;	
			}			
		} catch (IOException ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}


	public void readStepOverEnd(RubyStackFrame stackFrame) {
		try {
			this.println("th " + ((RubyThread) stackFrame.getThread()).getId() + " ; next " + stackFrame.getIndex());

		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);

		}
	}

	public void readStepReturnEnd(RubyStackFrame stackFrame) {
		try {
			this.println("th " + ((RubyThread) stackFrame.getThread()).getId() + " ; next " + (stackFrame.getIndex() + 1));

		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public void readStepIntoEnd(RubyStackFrame stackFrame) {
		try {
			this.println("th " + ((RubyThread) stackFrame.getThread()).getId() + " ; step " + stackFrame.getIndex());
			/*return new SuspensionReader(getMultiReaderStrategy()).readSuspension(); */
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
		}
	}

	public RubyStackFrame[] readFrames(RubyThread thread) {
		try {
			this.println("th " + thread.getId() + " ; f ");
			return new FramesReader(getMultiReaderStrategy()).readFrames(thread);
		} catch (IOException e) {
			RdtDebugCorePlugin.log(e);
			return null;
		}

	}

	public ThreadInfo[] readThreads() {
		try {
			this.println("th l");
			return new ThreadInfoReader(getMultiReaderStrategy()).readThreads();
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
			return null;
		}
	}

	public void closeSocket() throws IOException {
		if (socket != null) {
			socket.close();
		}
	}

	class RubyLoop extends Thread {
		private boolean shouldStop;

		public RubyLoop() {
			shouldStop = false;
			this.setName("RubyDebuggerLoop");
		}

		public void setShouldStop() {
			shouldStop = true;
		}

		public void run() {
			try {
				getDebugTarget().updateThreads();
				println("cont");
				System.out.println("Waiting for breakpoints.");
				while (true) {
					final SuspensionPoint hit = new SuspensionReader(getMultiReaderStrategy()).readSuspension();
					System.out.println(hit);
					if (hit == null) {
						break;
					}
					new Thread() {
						public void run() {
							getDebugTarget().suspensionOccurred(hit);
						}
					}
					.start();
				}
			} catch (Exception ex) {
				ex.printStackTrace(); //RdtDebugCorePlugin.log(ex) ;
			} finally {
				getDebugTarget().terminate();
				try {
					closeSocket();
				} catch (IOException e) {
					RdtDebugCorePlugin.log(e);
				}
				System.out.println("Socket loop finished.");
			}
		}
	}
}
