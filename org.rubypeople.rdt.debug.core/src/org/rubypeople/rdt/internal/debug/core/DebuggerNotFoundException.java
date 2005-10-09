package org.rubypeople.rdt.internal.debug.core;


public class DebuggerNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -7048656572730937337L;

	public DebuggerNotFoundException() {
		super("Could not connect to debugger.") ;
	}
}
