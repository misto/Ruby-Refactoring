package org.rubypeople.rdt.internal.debug.core;

/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SuspensionPoint {
	private String file ;
	private int line ;
	private int framesNumber ;
	
	public SuspensionPoint() {
		
	}
	
	public SuspensionPoint(String file, int line) {
		this.file = file ;
		this.line = line ;
	}
	/**
	 * Returns the file.
	 * @return String
	 */
	public String getFile() {
		return file;
	}

	/**
	 * Returns the line.
	 * @return int
	 */
	public int getLine() {
		return line;
	}

	/**
	 * Sets the file.
	 * @param file The file to set
	 */
	public void setFile(String file) {
		this.file = file;
	}

	/**
	 * Sets the line.
	 * @param line The line to set
	 */
	public void setLine(int line) {
		this.line = line;
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Breakpoint at " + file + ":" + line;
	}

	/**
	 * Returns the framesNumber.
	 * @return int
	 */
	public int getFramesNumber() {
		return framesNumber;
	}

	/**
	 * Sets the framesNumber.
	 * @param framesNumber The framesNumber to set
	 */
	public void setFramesNumber(int framesNumber) {
		this.framesNumber = framesNumber;
	}

}
