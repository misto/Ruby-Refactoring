package org.rubypeople.rdt.internal.debug.core;


public class DebuggerNotFoundException extends RuntimeException {
	public DebuggerNotFoundException() {
		super("Could not connect to debugger.") ;
	}
}
