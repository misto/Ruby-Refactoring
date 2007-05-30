/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Sebastian Davids: sdavids@gmx.de bug 37333, 26653 
 * 	   David Corbin: dcorbin@users.sourceforge.net - editor opening
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.ui.actions.OpenEditorActionGroup;



public class StackTraceLine {
	// for better matching with 1.8, append /:in `(.*)'/ to the regex
	private static Pattern OPEN_TRACE_LINE_PATTERN = Pattern.compile("\\s*(\\S.*?):(\\d+)(:|$)");
	private static Pattern BRACKETED_TRACE_LINE_PATTERN = Pattern.compile("\\[(.*):(\\d+)\\]:");
	private static Pattern OPTIONAL_PREFIX = Pattern.compile("^[ \\t^]*from ");
	private String fFilename;
	private int fLineNumber;
	private int length;
	private int offset;

	public static boolean isTraceLine(String line) {
		Matcher bracketedMatcher = BRACKETED_TRACE_LINE_PATTERN.matcher(line);
		Matcher openMatcher = OPEN_TRACE_LINE_PATTERN.matcher(line);
		return bracketedMatcher.find() || openMatcher.find(); 
	}
	
	public StackTraceLine(String traceLine) {
		this(traceLine, null);
	}
    
    public StackTraceLine(String traceLine, IRubyProject launchedProject) {
    	int prefix = 0;
		Matcher matcher = OPTIONAL_PREFIX.matcher(traceLine);
		if (matcher.find()) {
			traceLine = traceLine.substring(matcher.group(0).length());
			prefix = matcher.group(0).length();
		}
		
		matcher = BRACKETED_TRACE_LINE_PATTERN.matcher(traceLine);
		if (!matcher.find()) {
			matcher = OPEN_TRACE_LINE_PATTERN.matcher(traceLine);
			if (!matcher.find())  
				return;
		}
		
		fFilename = matcher.group(1);	
		if (fFilename.startsWith("./") && launchedProject != null) {
			fFilename = launchedProject.getPath().toPortableString() + fFilename.substring(1);
		}
		String lineNumber = matcher.group(2);
		fLineNumber = Integer.parseInt(lineNumber);
		
		offset = matcher.start(1) + prefix;
		length = fFilename.length()+lineNumber.length()+1;
	}

	public void openEditor() {
        if (fFilename == null)
            return;
        new LineBasedEditorOpener(fFilename, fLineNumber).open();
	}

	public int getLineNumber() {
		return fLineNumber;
	}

	public String getFilename() {
		return fFilename;
	}

	public int offset() {
		return offset;
	}

	public int length() {
		return length;
	}
}
