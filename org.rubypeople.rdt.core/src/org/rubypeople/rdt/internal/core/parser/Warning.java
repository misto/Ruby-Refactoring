/*
 * Created on Feb 20, 2005
 */
package org.rubypeople.rdt.internal.core.parser;

import org.jruby.lexer.yacc.ISourcePosition;

/**
 * @author Chris
 */
class Warning {

	private ISourcePosition position;
	private String message;

	/**
	 * @param position
	 * @param message
	 */
	public Warning(ISourcePosition position, String message) {
		this.position = position;
		this.message = message;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return Returns the position.
	 */
	public ISourcePosition getPosition() {
		return position;
	}
}
