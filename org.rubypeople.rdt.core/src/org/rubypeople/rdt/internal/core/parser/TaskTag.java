/**
 * 
 */
package org.rubypeople.rdt.internal.core.parser;

/**
 * @author Chris
 * 
 */
public class TaskTag {

	private String message;
	private int priority;
	private int line;
	private int start;
	private int end;

	public TaskTag(String message, int priority, int lineNumber, int start, int end) {
		this.message = message;
		this.priority = priority;
		this.line = lineNumber;
		this.start = start;
		this.end = end;
	}

	public int getEnd() {
		return end;
	}

	public int getLine() {
		return line;
	}

	public String getMessage() {
		return message;
	}

	public int getPriority() {
		return priority;
	}

	public int getStart() {
		return start;
	}

}
