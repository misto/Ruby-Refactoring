/*
 * Created on Feb 20, 2005
 */
package org.rubypeople.rdt.internal.core.parser;

import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.core.parser.IProblem;

/**
 * @author Chris
 */
abstract class DefaultProblem implements IProblem {

	private ISourcePosition position;
	private String message;

	/**
	 * @param position
	 * @param message
	 */
	public DefaultProblem(ISourcePosition position, String message) {
		this.position = position;
		this.message = message;
	}

	/**
	 * @return Returns the message.
	 */
	public String getMessage() {
		return message;
	}

	public char[] getOriginatingFileName() {
		return position.getFile().toCharArray();
	}

	public int getSourceEnd() {
		return position.getEndOffset();
	}

	public int getSourceLineNumber() {
		return position.getLine();
	}

	public int getSourceStart() {
		return position.getStartOffset();
	}
}
