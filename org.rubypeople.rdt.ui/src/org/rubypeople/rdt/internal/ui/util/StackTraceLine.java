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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.ExternalRubyFileEditorInput;


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
		String lineNumber = matcher.group(2);
		fLineNumber = Integer.parseInt(lineNumber);
		
		offset = matcher.start(1) + prefix;
		length = fFilename.length()+lineNumber.length()+1;
	}

	public void openEditor() {
		try {
			if (fFilename == null)
				return;
			IEditorInput fileEditorInput = createEditorInput(fFilename);
			IWorkbench workbench = PlatformUI.getWorkbench();
			IEditorRegistry editorRegistry = workbench.getEditorRegistry();
			IWorkbenchPage page = workbench.getActiveWorkbenchWindow().getActivePage();
			IEditorDescriptor descriptor = editorRegistry.getDefaultEditor(fFilename);
			if (descriptor == null)
				return;
			ITextEditor editor = (ITextEditor) page.openEditor(fileEditorInput, editorId(descriptor));
			setEditorPosition(fLineNumber, editor);
		} catch (PartInitException e) {
			RubyPlugin.log(e);
		}
	}

	private void setEditorPosition(int lineNumber, ITextEditor editor) {
		try {
			if (lineNumber > 0) {
				IDocument document= editor.getDocumentProvider().getDocument(editor.getEditorInput());
				editor.selectAndReveal(document.getLineOffset(lineNumber-1), document.getLineLength(lineNumber-1));
			}
		} catch (BadLocationException doNothing) {
		}
	}

	private IEditorInput createEditorInput(String filename) {
		IFile file = getWorkspaceFile(filename);
		if (file == null) 
			return new ExternalRubyFileEditorInput(new java.io.File(filename));
		return new FileEditorInput(file);
	}

	private IFile getWorkspaceFile(String filename) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IPath filepath = new Path(filename);
		IFile file = root.getFileForLocation(filepath);
		
		return file;
	}

	private static String editorId(IEditorDescriptor descriptor)
    {
        String editorId;
        if (descriptor == null)
        {
            editorId = "org.eclipse.ui.DefaultTextEditor";                         //$NON-NLS-1$
        }
        else
        {
            editorId = descriptor.getId();
        }
        return editorId;
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
