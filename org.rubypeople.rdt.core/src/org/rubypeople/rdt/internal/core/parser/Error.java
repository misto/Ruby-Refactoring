/**
 * 
 */
package org.rubypeople.rdt.internal.core.parser;

import org.jruby.lexer.yacc.ISourcePosition;


/**
 * @author Chris
 *
 */
public class Error extends DefaultProblem {

	public Error(ISourcePosition position, String message) {
		super(position, message);
	}
	
	public boolean isError() {
		return true;
	}
	
	public boolean isWarning() {
		return false;
	}
	
	public boolean isTask() {
		return false;
	}

}
