/*******************************************************************************
 * Author: Markus Barchfeld
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT
 * is subject to the "Common Public License (CPL) v 1.0". You may not use RDT
 * except in compliance with the License. For further information see
 * org.rubypeople.rdt/rdt.license. This source code is based on
 * org.eclipse.jdt.internal.debug.ui.console.JavaConsoleTracker /
 * 
 * /*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Common Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ******************************************************************************/
package org.rubypeople.rdt.internal.debug.ui.console;


import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IHyperlink;

/**
 * Provides links for stack traces, eg:
 * /data/EMAILTOSMSBRIDGE/configFile.rb:1:in `require': No such file to load -- inifile (LoadError) from /data/EMAILTOSMSBRIDGE/configFile.rb:1 from
 * /data/EMAILTOSMSBRIDGE/fetchmail.rb:2:in `require' from
 * /data/EMAILTOSMSBRIDGE/fetchmail.rb:2 from
 * /data/EMAILTOSMSBRIDGE/startEmailToSms.rb:1:in `require' from
 * /data/EMAILTOSMSBRIDGE/startEmailToSms.rb:1
 *  
 */
public class RubyConsoleTracker implements IConsoleLineTracker {
	
	
	/**
	 * The console associated with this line tracker
	 */
	private IConsole fConsole;

	/**
	 * @see org.eclipse.debug.ui.console.IConsoleLineTracker#init(org.eclipse.debug.ui.console.IConsole)
	 */
	public void init(IConsole pConsole) {
		fConsole = pConsole;
	}

	
	private StackFrameInfo detectStackFrame(String pLine) {
		// FIXME This is hardcoded to only recognize .rb endings!
		int startOfSuffix = pLine.indexOf(".rb:") ;
		if (startOfSuffix == -1) {
			return null ;
		}
		int startLineNumber = startOfSuffix + 4 ;
		int endLineNumber = pLine.indexOf(":", startLineNumber) ;
		if (endLineNumber == -1) {
			endLineNumber = pLine.length() ;
		}
		String lineNumber = pLine.substring(startLineNumber, endLineNumber) ;	
		for (int i = 0; i < lineNumber.length(); i++) {
			char c = lineNumber.charAt(i) ;
			if (c < '0' || c > '9') {
				return null ;
			}				
		} 
		
		return new StackFrameInfo(0, endLineNumber, startOfSuffix+3, lineNumber) ;	
	}

	private void setStartPos(String pLine, StackFrameInfo pStackFrameInfo) {
		int fromOffset = pLine.indexOf("from ") ;
		if (fromOffset == -1) {
			return ;
		}
		for (int i = 0; i < fromOffset; i++) {
			char charBeforeFrom = pLine.charAt(i) ;
			if (!(charBeforeFrom == ' ' || charBeforeFrom == '\t')) {
				return ;
			}
		}
		pStackFrameInfo.start = 5 + fromOffset ;
	}
	
	/**
	 * @see org.eclipse.debug.ui.console.IConsoleLineTracker#lineAppended(org.eclipse.jface.text.IRegion)
	 */
	public void lineAppended(IRegion line) {
		try {
			int offset = line.getOffset();
			int length = line.getLength();
			String text = fConsole.getDocument().get(offset, length);
			StackFrameInfo sfi = this.detectStackFrame(text)	;
			if (sfi == null) {
				return ;
			}
			this.setStartPos(text, sfi) ;
			IHyperlink link = new RubyStackTraceHyperlink(fConsole, sfi);
			fConsole.addLink(link, offset + sfi.start, sfi.end - sfi.start);			
		} catch (BadLocationException e) {
		}
	}

	/**
	 * @see org.eclipse.debug.ui.console.IConsoleLineTracker#dispose()
	 */
	public void dispose() {
		fConsole = null;
	}

	public class StackFrameInfo {
		
		public StackFrameInfo(int pPosStart, int pPosEnd, int pNameEnd, String pLineNumber) {
			start = pPosStart ;
			end = pPosEnd ;
			lineNumber = pLineNumber;
			nameEnd = pNameEnd ;
		}
		public int start ;
		public int nameEnd ;
		public int end ;
		public String lineNumber ;
	}
	
	
}
