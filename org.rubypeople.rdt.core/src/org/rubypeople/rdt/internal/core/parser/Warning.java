/**
 * 
 */
package org.rubypeople.rdt.internal.core.parser;

import org.jruby.lexer.yacc.ISourcePosition;


/**
 * @author Chris
 *
 */
public class Warning extends DefaultProblem {

	public Warning(ISourcePosition position, String message) {
		super(position, message);
	}
	
	public boolean isWarning() {
		return true;
	}
	
	public boolean isError() {
		return false;
	}
	
	public boolean isTask() {
		return false;
	}
}
