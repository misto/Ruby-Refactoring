/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.Assert;
import org.eclipse.ui.IEditorInput;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.IWorkingCopyManagerExtension;

/**
 * This working copy manager works together with a given compilation unit
 * document provider and additionally offers to "overwrite" the working copy
 * provided by this document provider.
 */
public class WorkingCopyManager implements IWorkingCopyManager, IWorkingCopyManagerExtension {

	private RubyDocumentProvider fDocumentProvider;
	private Map fMap;
	private boolean fIsShuttingDown;

	/**
	 * Creates a new working copy manager that co-operates with the given
	 * compilation unit document provider.
	 * 
	 * @param provider
	 *            the provider
	 */
	public WorkingCopyManager(RubyDocumentProvider provider) {
		Assert.isNotNull(provider);
		fDocumentProvider = provider;
	}

	/*
	 * @see org.eclipse.jdt.ui.IWorkingCopyManager#connect(org.eclipse.ui.IEditorInput)
	 */
	public void connect(IEditorInput input) throws CoreException {
		fDocumentProvider.connect(input);
	}

	/*
	 * @see org.eclipse.jdt.ui.IWorkingCopyManager#disconnect(org.eclipse.ui.IEditorInput)
	 */
	public void disconnect(IEditorInput input) {
		fDocumentProvider.disconnect(input);
	}

	/*
	 * @see org.eclipse.jdt.ui.IWorkingCopyManager#shutdown()
	 */
	public void shutdown() {
		if (!fIsShuttingDown) {
			fIsShuttingDown = true;
			try {
				if (fMap != null) {
					fMap.clear();
					fMap = null;
				}
				fDocumentProvider.shutdown();
			} finally {
				fIsShuttingDown = false;
			}
		}
	}

	/*
	 * @see org.eclipse.jdt.ui.IWorkingCopyManager#getWorkingCopy(org.eclipse.ui.IEditorInput)
	 */
	public IRubyScript getWorkingCopy(IEditorInput input) {
		IRubyScript unit = fMap == null ? null : (IRubyScript) fMap.get(input);
		return unit != null ? unit : fDocumentProvider.getWorkingCopy(input);
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IWorkingCopyManagerExtension#setWorkingCopy(org.eclipse.ui.IEditorInput,
	 *      org.eclipse.jdt.core.ICompilationUnit)
	 */
	public void setWorkingCopy(IEditorInput input, IRubyScript workingCopy) {
		if (fDocumentProvider.getDocument(input) != null) {
			if (fMap == null) fMap = new HashMap();
			fMap.put(input, workingCopy);
		}
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.IWorkingCopyManagerExtension#removeWorkingCopy(org.eclipse.ui.IEditorInput)
	 */
	public void removeWorkingCopy(IEditorInput input) {
		fMap.remove(input);
		if (fMap.isEmpty()) fMap = null;
	}
}