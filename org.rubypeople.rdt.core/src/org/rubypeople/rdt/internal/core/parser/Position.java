/*
 * Created on Mar 8, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.internal.core.parser;


/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class Position {

	private int lineNum;
	private int offset;
	
	/**
	 * @param lineNum
	 * @param offset
	 */
	public Position(int lineNum, int offset) {
		this.lineNum = lineNum;
		this.offset = offset;
	}

	/**
	 * @return
	 */
	public int getLineNumber() {
		return lineNum;
	}

	/**
	 * @return
	 */
	public int getOffset() {
		return offset;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof Position) {
			Position pos = (Position) obj;
			return pos.getLineNumber() == this.getLineNumber() && pos.getOffset() == this.getOffset();
		}
		return false;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return toString().hashCode();
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Line #: " + getLineNumber() + ", offset: " + getOffset();
	}

}
