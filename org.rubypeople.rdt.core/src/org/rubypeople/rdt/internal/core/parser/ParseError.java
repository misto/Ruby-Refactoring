/*
 * Created on Mar 13, 2004
 * 
 * To change the template for this generated file go to Window - Preferences -
 * Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.internal.core.parser;

import org.eclipse.core.resources.IMarker;
import org.rubypeople.rdt.internal.core.parser.ast.RubyElement;

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
	private int severity;

	public static final int WARNING = IMarker.SEVERITY_WARNING;
	public static final int INFO = IMarker.SEVERITY_INFO;
	public static final int ERROR = IMarker.SEVERITY_INFO;

	/**
	 * @param error
	 * @param lineNum
	 * @param start
	 * @param end
	 */
	public ParseError(String error, int lineNum, int start, int end, int severity) {
		this.error = error;
		this.lineNum = lineNum;
		this.start = start;
		this.end = end;
		this.severity = severity;
	}

	/**
	 * @param string
	 * @param element
	 */
	public ParseError(String string, RubyElement element, int severity) {
		this(string, element.getStart().getLineNumber(), element.getStart().getOffset(), element.getStart().getOffset() + element.getName().length(), severity);
	}

	/**
	 * @param error
	 * @param line
	 * @param token
	 * @param severity
	 */
	public ParseError(String error, int line, RubyToken token, int severity) {
		this(error, line, token.getOffset(), token.getText().length(), severity);
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

	/**
	 * @return
	 */
	public Integer getSeverity() {
		return new Integer(severity);
	}

}