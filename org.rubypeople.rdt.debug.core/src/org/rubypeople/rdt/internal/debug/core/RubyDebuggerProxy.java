package org.rubypeople.rdt.internal.debug.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.rubypeople.rdt.internal.debug.core.model.RubyDebugTarget;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;
import org.rubypeople.rdt.internal.debug.core.parsing.FramesReader;
import org.rubypeople.rdt.internal.debug.core.parsing.VariableReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RubyDebuggerProxy {

	private Socket socket;
	private PrintWriter socketWriter;
	private RubyDebugTarget debugTarget;
	private RubyLoop rubyLoop;
	private XmlPullParser xpp;

	class RubyLoop extends Thread {
		XmlPullParser xpp;
		RubyDebuggerProxy debuggerProxy;
		private boolean shouldStop;

		public RubyLoop(XmlPullParser xpp, RubyDebuggerProxy debuggerProxy) {
			this.xpp = xpp;
			this.debuggerProxy = debuggerProxy;
			shouldStop = false;
			this.setName("RubyDebuggerLoop");
		}

		public void setShouldStop() {
			shouldStop = true;
		}

		public void run() {
			try {
				System.out.println("Waiting for breakpoints.");
				SuspensionPoint hit;
				while ((hit = new SuspensionReader().readSuspension(xpp)) != null) {
					System.out.println(hit);					
					((RubyThread) getDebugTarget().getThreads()[0]).doSuspend(hit) ;
					try {
						System.out.println("Waiting for resume.");
						Thread.sleep(Long.MAX_VALUE);
					} catch (InterruptedException ex) {
						if (shouldStop) {
							break;
						}
					}
					System.out.println("Continue");
					println("cont");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			} finally {
				getDebugTarget().terminate();
				//DebugEvent ev = new DebugEvent(getDebugTarget(), DebugEvent.TERMINATE, DebugEvent.CLIENT_REQUEST);
				//DebugPlugin.getDefault().fireDebugEventSet(new DebugEvent[] { ev });
				try {
					debuggerProxy.closeSocket() ;
				} catch (IOException e) {
					e.printStackTrace() ;
				}
				System.out.println("Socket loop finished.");
			}
		}
	}

	public RubyDebuggerProxy(RubyDebugTarget debugTarget) {
		this.debugTarget = debugTarget;
		debugTarget.setRubyDebuggerProxy(this);
	}

	public void start() {
		try {
			this.setBreakPoints();
			this.startSocketReader();
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

	protected PrintWriter getSocketWriter() throws IOException {
		if (socketWriter == null) {
			socketWriter = new PrintWriter(this.getSocket().getOutputStream(), true);
		}
		return socketWriter;
	}

	public void setBreakPoints() throws IOException {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints(RubyDebugTarget.MODEL_IDENTIFIER);
		for (int i = 0; i < breakpoints.length; i++) {
			StringBuffer setBreakPointCommand = new StringBuffer();
			setBreakPointCommand.append("b ");
			//setBreakPointCommand.append(breakpoints[i].getMarker().getResource().getLocation().toOSString());
			setBreakPointCommand.append(breakpoints[i].getMarker().getResource().getName());
			setBreakPointCommand.append(":");			
			setBreakPointCommand.append(breakpoints[i].getMarker().getAttribute(IMarker.LINE_NUMBER, -1));
			this.println(setBreakPointCommand.toString());
		}

		this.println("cont");
	}

	public void startSocketReader() {

		rubyLoop = new RubyLoop(this.getXpp(), this);
		rubyLoop.start();
	}

	public void resume() {
		rubyLoop.interrupt();
	}

	protected void println(String s) throws IOException {
		System.out.println("Sending debugger: " + s);
		try {
			this.getSocketWriter().println(s);
		} catch (IOException e) {
			System.out.println("Could not send to debugger. Exception occured.");
			e.printStackTrace();
			throw e;
		}
	}

	protected RubyDebugTarget getDebugTarget() {
		return debugTarget;
	}

	protected XmlPullParser getXpp() {
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

	public RubyVariable[] readVariables(RubyStackFrame frame) {
		try {
			this.println("v local " + frame.getIndex());
			return new VariableReader().readVariables(frame, this.getXpp());
		} catch (IOException ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}

	public RubyVariable[] readInstanceVariables(RubyVariable variable) {
		try {
			this.println("v i " + variable.getStackFrame().getIndex() + " " + variable.getQualifiedName());
			return new VariableReader().readVariables(variable, this.getXpp());
		} catch (IOException ioex) {
			ioex.printStackTrace();
			throw new RuntimeException(ioex.getMessage());
		}
	}

	public SuspensionPoint readStepOverEnd(RubyStackFrame stackFrame) {
		try {
			this.println("next " + stackFrame.getIndex());
			return new SuspensionReader().readSuspension(this.getXpp());
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
			return null;
		}
	}

	public SuspensionPoint readStepReturnEnd(RubyStackFrame stackFrame) {
		try {			
			this.println("next " + (stackFrame.getIndex() + 1));
			return new SuspensionReader().readSuspension(this.getXpp());
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
			return null;
		}
	}

	public SuspensionPoint readStepIntoEnd(RubyStackFrame stackFrame) {
		try {
			this.println("step " + stackFrame.getIndex());
			return new SuspensionReader().readSuspension(this.getXpp());
		} catch (Exception e) {
			RdtDebugCorePlugin.log(e);
			return null;
		}
	}


	public RubyStackFrame[] readFrames(RubyThread thread) {
		try {
			this.println("w");
			RubyStackFrame[] frames = new FramesReader().readFrames(thread, xpp);
			return frames ;
		} catch (IOException e) {
			RdtDebugCorePlugin.log(e);
			return null ;
		}

	}
	
	public void closeSocket() throws IOException {
		socket.close();	
	}
}
