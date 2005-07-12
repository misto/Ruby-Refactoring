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


import java.io.File;

import org.eclipse.debug.ui.console.IConsole;
import org.eclipse.debug.ui.console.IConsoleLineTracker;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.console.IHyperlink;
import org.rubypeople.rdt.internal.ui.util.StackTraceLine;

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
	
	
		public interface FileExistanceChecker {
			boolean fileExists(String filename);
		}
		
		public static class StandardFileExistanceChecker implements FileExistanceChecker{
	
			public boolean fileExists(String filename) {
				File file = new File(filename);
				return file.exists();
			}
		}
		private final FileExistanceChecker existanceChecker;
	 	
		public RubyConsoleTracker() {
			this(new StandardFileExistanceChecker());
		}
	
		public RubyConsoleTracker(FileExistanceChecker existance) {
			this.existanceChecker = existance;
		}
	
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

	
	

	
	/**
	 * @see org.eclipse.debug.ui.console.IConsoleLineTracker#lineAppended(org.eclipse.jface.text.IRegion)
	 */
	public void lineAppended(IRegion line) {
		try {
			int offset = line.getOffset();
			int length = line.getLength();
			int prefix = 0;
						
			String text = fConsole.getDocument().get(offset, length);
			while (StackTraceLine.isTraceLine(text)) {
				StackTraceLine stackTraceLine = new StackTraceLine(text);
				if (! existanceChecker.fileExists(stackTraceLine.getFilename()))
					return;
				IHyperlink link = new RubyStackTraceHyperlink(fConsole, stackTraceLine);
				fConsole.addLink(link, line.getOffset() + prefix + stackTraceLine.offset() , stackTraceLine.length());
								
				prefix = stackTraceLine.offset() + stackTraceLine.length();
				text = text.substring(stackTraceLine.offset() + stackTraceLine.length());
				if (text.startsWith(":in `require':")) {
					text = text.substring(14);
					prefix += 14;
				}
			}
		} catch (BadLocationException e) {
		}
	}

	/**
	 * @see org.eclipse.debug.ui.console.IConsoleLineTracker#dispose()
	 */
	public void dispose() {
		fConsole = null;
	}
	
}
