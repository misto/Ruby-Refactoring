/*
 * Created on Mar 13, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.internal.core.parser;

/**
 * @author Chris
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class ParseError {

	private String error;
	private int lineNum;
	private int start;
	private int end;

	/**
	 * @param error
	 * @param lineNum
	 * @param start
	 * @param end
	 */
	public ParseError(String error, int lineNum, int start, int end) {
		this.error = error;
		this.lineNum = lineNum;
		this.start = start;
		this.end = end;
	}

	/**
	 * @return
	 */
	public int getLine() {
		return lineNum;
	}

	/**
	 * @return
	 */
	public int getStart() {
		return start;
	}

	/**
	 * @return
	 */
	public int getEnd() {
		return end;
	}

	/**
	 * @return
	 */
	public String getError() {
		return error;
	}

}
