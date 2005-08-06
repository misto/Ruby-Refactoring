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

	private int startLine;
	private int endLine;
	
	private int startOffset;
	private int endOffset;
	
	public RdtPosition( int startLine, int startOffset, int endOffset ){
		this.startLine = startLine;
		this.endLine = startLine;
		this.startOffset = startOffset;
		this.endOffset = endOffset; 		
	}

	/**
	 * 
	 */
	public RdtPosition( int startLine, int endLine, int startOffset, 
			int endOffset ){
		this.startLine = startLine;
		this.endLine = endLine;
		this.startOffset = startOffset;
		this.endOffset = endOffset; }
		
	/* (non-Javadoc)
	 * @see org.jruby.lexer.yacc.ISourcePosition#getFile()
	 */
	public String getFile() {
		return null;
	}


	/* (non-Javadoc)
	 * @see org.jruby.lexer.yacc.ISourcePosition#getStartOffset()
	 */
	public int getStartOffset() {
		return startOffset;
	}

	/* (non-Javadoc)
	 * @see org.jruby.lexer.yacc.ISourcePosition#getEndOffset()
	 */
	public int getEndOffset() {
		return endOffset;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "start line: " + getStartLine() + ", end line: " + getEndLine()
			+ ", (" + getStartOffset() + ".." + getEndOffset() + ")";
	}

	public int getStartLine() {
		// TODO Auto-generated method stub
		return startLine;
	}

	public int getEndLine() {
		// TODO Auto-generated method stub
		return endLine;
	}
	
}
