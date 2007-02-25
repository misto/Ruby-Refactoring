package org.rubypeople.rdt.internal.debug.core.commands;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import org.rubypeople.rdt.internal.debug.core.DebuggerNotFoundException;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.model.RubyProcessingException;
import org.rubypeople.rdt.internal.debug.core.parsing.AbstractReadStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.MultiReaderStrategy;
import org.rubypeople.rdt.internal.debug.core.parsing.SuspensionReader;
import org.xmlpull.v1.XmlPullParser;

public class RubyDebugConnection extends AbstractDebuggerConnection {

	private Socket controlSocket ;
	private MultiReaderStrategy controlReadStrategy;
	private PrintWriter controlWriter;
	private String rdebugExtensionPath ;
	public RubyDebugConnection(String rdebugExtensionPath, int port) {
		super(port);
		this.rdebugExtensionPath = rdebugExtensionPath +  File.separatorChar + "rdebugExtension.rb";
	}

	@Override
	public void connect() throws DebuggerNotFoundException, IOException{
		createControlConnection() ;
		
		String expression = "eval require '" + rdebugExtensionPath + "'";
		EvalCommand command = new EvalCommand(expression, true) ;
		command.execute(this) ;
		String evalResult = null;
		try {
			evalResult = command.getEvalReader().readEvalResult();
		} catch (RubyProcessingException e) {
			RdtDebugCorePlugin.log(e) ;
		}
		if (evalResult == null || !evalResult.equals("true")) {
			// TODO: go on ?
			throw new DebuggerNotFoundException("Could not add extension to ruby debug") ;
		}
		// set trace: show stack trace if evaluation fails
		new GenericCommand("set trace", true).execute(this) ;
	}

	@Override
	public SuspensionReader start() throws DebuggerNotFoundException, IOException {
		createCommandConnection() ;
		return new SuspensionReader(getCommandReadStrategy()) ;
	}
	
	@Override
	public AbstractReadStrategy sendCommand(AbstractCommand command) throws DebuggerNotFoundException, IOException {
		AbstractReadStrategy result = null ;
		if (command.isControl()) {
			result =sendControlCommand(command) ; 
		} else {
			result = super.sendCommand(command);
		}
		return result ;
	}
	
	private AbstractReadStrategy sendControlCommand(AbstractCommand command) throws IOException {
		if (!isControlPortConnected()) {
			throw new IllegalStateException(command + " could not be executed since control socket is not opened.") ;
		}
		RdtDebugCorePlugin.debug("Sending control command: " + command.getCommand()) ;
		getControlWriter().println(command.getCommand()) ;
		return getControlReadStrategy() ;
	}
	
	private PrintWriter getControlWriter() throws IOException {
		if (controlWriter == null) {
			controlWriter = new PrintWriter(getControlSocket().getOutputStream(), true);
		}
		return controlWriter;
	}
	
	protected boolean isControlPortConnected() {
		return controlReadStrategy != null;  
	}
	
	protected void createControlConnection() throws DebuggerNotFoundException, IOException {
		Socket socket = getControlSocket() ;
		XmlPullParser xpp = createXpp(socket) ;
		controlReadStrategy = new MultiReaderStrategy(xpp) ;
	}
	
	private Socket getControlSocket() throws IOException {
		if (controlSocket == null) {
			controlSocket = acquireSocket(getCommandPort() +  1) ;
		}
		return controlSocket ;
	}

	public MultiReaderStrategy getControlReadStrategy() {
		return controlReadStrategy;
	}
	
	@Override
	public void exit() throws IOException {
		super.exit();
		GenericCommand command = new GenericCommand("exit", true) ;
		command.execute(this) ;
		controlSocket.close() ;
	}

}
