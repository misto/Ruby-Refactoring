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
import org.eclipse.core.runtime.OperationCanceledException;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;

/**
 * Reconcile a working copy and signal the changes through a delta.
 */
public class ReconcileWorkingCopyOperation { // TODO extends RubyModelOperation

	int astLevel;
	boolean forceProblemDetection;
	WorkingCopyOwner workingCopyOwner;
	private IRubyElement workingCopy;
	private IProgressMonitor progressMonitor;

	public ReconcileWorkingCopyOperation(IRubyElement workingCopy, WorkingCopyOwner workingCopyOwner) {
		this.workingCopy = workingCopy;
		this.workingCopyOwner = workingCopyOwner;
	}
	
	/**
	 * Returns the working copy this operation is working on.
	 */
	protected RubyScript getWorkingCopy() {
		return (RubyScript)workingCopy;
	}

	/**
	 * @exception RubyModelException
	 *                if setting the source of the original compilation unit
	 *                fails
	 */
	protected void executeOperation() throws RubyModelException {
		if (this.progressMonitor != null) {
			if (this.progressMonitor.isCanceled()) throw new OperationCanceledException();
			this.progressMonitor.beginTask(org.rubypeople.rdt.internal.core.util.Util.bind("element.reconciling"), 2); //$NON-NLS-1$
		}
		RubyScript workingCopy = getWorkingCopy();
		boolean wasConsistent = workingCopy.isConsistent();
		try {
			if (!wasConsistent) {
				// TODO create the delta builder (this remembers the current
				// content
				// of the cu)
				// update the element infos with the content of the working copy
				workingCopy.makeConsistent(this.progressMonitor);
				// deltaBuilder.buildDeltas();

				if (progressMonitor != null) progressMonitor.worked(2);

				// TODO register the deltas
				// if (deltaBuilder.delta != null) {
				// addReconcileDelta(workingCopy, deltaBuilder.delta);
				// }
			} else {
//				 force problem detection? - if structure was consistent
				if (forceProblemDetection) {
					IProblemRequestor problemRequestor = workingCopy.getPerWorkingCopyInfo();
					if (problemRequestor != null && problemRequestor.isActive()) {
						problemRequestor.beginReporting();
						RubyScriptProblemFinder.process(workingCopy, workingCopy.getContents(), problemRequestor, progressMonitor);					
						problemRequestor.endReporting();
						if (progressMonitor != null) progressMonitor.worked(1);
					}
				}
			}
		} finally {
			if (progressMonitor != null) progressMonitor.done();
		}
	}

	protected void runOperation(IProgressMonitor progressMonitor) throws RubyModelException {
		this.progressMonitor = progressMonitor;
		executeOperation();
	}

	/**
	 * @see RubyModelOperation#isReadOnly
	 */
	public boolean isReadOnly() {
		return true;
	}

}