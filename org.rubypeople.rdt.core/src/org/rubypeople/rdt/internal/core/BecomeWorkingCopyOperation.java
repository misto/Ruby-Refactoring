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
package org.rubypeople.rdt.internal.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * Switch and ICompilationUnit to working copy mode and signal the working copy
 * addition through a delta.
 */
public class BecomeWorkingCopyOperation {

	private RubyScript workingCopy;
	private IProgressMonitor monitor;
	private IProblemRequestor problemRequestor;

	/*
	 * Creates a BecomeWorkingCopyOperation for the given working copy.
	 * perOwnerWorkingCopies map is not null if the working copy is a shared
	 * working copy.
	 */
	public BecomeWorkingCopyOperation(RubyScript workingCopy, IProblemRequestor problemRequestor) {
		this.workingCopy = workingCopy;
		this.problemRequestor = problemRequestor;
	}

	protected void executeOperation() throws RubyModelException {
		// open the working copy now to ensure contents are that of the current
		// state of this element
		RubyModelManager.getRubyModelManager().getPerWorkingCopyInfo(workingCopy, true/*
																						 * create
																						 * if
																						 * needed
																						 */, true/*
																													 * record
																													 * usage
																													 */, problemRequestor);
		workingCopy.openWhenClosed(workingCopy.createElementInfo(), monitor);
	}

	/*
	 * @see RubyModelOperation#isReadOnly
	 */
	public boolean isReadOnly() {
		return true;
	}

	/**
	 * @param monitor
	 * @throws RubyModelException 
	 */
	public void runOperation(IProgressMonitor monitor) throws RubyModelException {
		this.monitor = monitor;
		executeOperation();
	}

}
