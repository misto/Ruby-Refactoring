/*
 * Created on Feb 27, 2005
 *
 */
package org.rubypeople.rdt.internal.core.parser;

import org.jruby.lexer.yacc.ISourcePosition;


/**
 * @author Chris
 **/
public class RdtPosition implements ISourcePosition {
	
	private int start;
	private int end;
	private int line;

	/**
	 * 
	 */
	public RdtPosition(int line, int startOffset, int endOffset) {
		this.line = line;
		this.start = startOffset;
		this.end = endOffset;
	}
	
	/* (non-Javadoc)
	 * @see org.jruby.lexer.yacc.ISourcePosition#getFile()
	 */
	public String getFile() {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jruby.lexer.yacc.ISourcePosition#getLine()
	 */
	public int getLine() {
		return line;
	}

	/* (non-Javadoc)
	 * @see org.jruby.lexer.yacc.ISourcePosition#getStartOffset()
	 */
	public int getStartOffset() {
		return start;
	}

	/* (non-Javadoc)
	 * @see org.jruby.lexer.yacc.ISourcePosition#getEndOffset()
	 */
	public int getEndOffset() {
		return end;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "line " + getLine() + ": (" + getStartOffset() +".." + getEndOffset() + ")";
	}
	
}
