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

import org.eclipse.core.runtime.OperationCanceledException;
import org.rubypeople.rdt.core.IProblemRequestor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatus;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;

/**
 * Reconcile a working copy and signal the changes through a delta.
 */
public class ReconcileWorkingCopyOperation extends RubyModelOperation {

    public static boolean PERF = false;
    boolean createAST;
    boolean forceProblemDetection;
    WorkingCopyOwner workingCopyOwner;
    RubyScript ast;

    public ReconcileWorkingCopyOperation(IRubyElement workingCopy,
            boolean forceProblemDetection, WorkingCopyOwner workingCopyOwner) {
        super(new IRubyElement[] { workingCopy});
        this.forceProblemDetection = forceProblemDetection;
        this.workingCopyOwner = workingCopyOwner;
    }

    /**
     * @exception RubyModelException
     *                if setting the source of the original compilation unit
     *                fails
     */
    protected void executeOperation() throws RubyModelException {
        if (this.progressMonitor != null) {
            if (this.progressMonitor.isCanceled()) throw new OperationCanceledException();
            this.progressMonitor.beginTask(org.rubypeople.rdt.internal.core.util.Util
                    .bind("element.reconciling"), 2); //$NON-NLS-1$
        }
        RubyScript workingCopy = getWorkingCopy();
        boolean wasConsistent = workingCopy.isConsistent();
        try {
            if (!wasConsistent) {
                // create the delta builder (this remembers the current content
                // of the cu)
                RubyElementDeltaBuilder deltaBuilder = new RubyElementDeltaBuilder(workingCopy);

                workingCopy.makeConsistent(this.progressMonitor);
                deltaBuilder.buildDeltas();

                if (progressMonitor != null) progressMonitor.worked(2);

                // register the deltas
                RubyElementDelta delta = deltaBuilder.delta;
                if (delta != null) {
                    delta.changedAST(this.ast);
                    addReconcileDelta(workingCopy, delta);
                }
            } else {
                // force problem detection? - if structure was consistent
                if (forceProblemDetection) {
                    IProblemRequestor problemRequestor = workingCopy.getPerWorkingCopyInfo();
                    boolean computeProblems = RubyProject.hasRubyNature(workingCopy
                            .getRubyProject().getProject())
                            && problemRequestor != null && problemRequestor.isActive();
                    if (computeProblems) {

                        char[] contents = workingCopy.getContents();
                        problemRequestor.beginReporting();
                        RubyScriptProblemFinder.process(workingCopy, contents, problemRequestor,
                                progressMonitor);
                        problemRequestor.endReporting();
                        if (progressMonitor != null) progressMonitor.worked(1);
                        // TODO Create AST?
                    }
                }
            }
        } finally {
            if (progressMonitor != null) progressMonitor.done();
        }
    }

    /**
     * Returns the working copy this operation is working on.
     */
    protected RubyScript getWorkingCopy() {
        return (RubyScript) getElementToProcess();
    }

    /**
     * @see RubyModelOperation#isReadOnly
     */
    public boolean isReadOnly() {
        return true;
    }

    protected IRubyModelStatus verify() {
        IRubyModelStatus status = super.verify();
        if (!status.isOK()) { return status; }
        RubyScript workingCopy = getWorkingCopy();
        if (!workingCopy.isWorkingCopy()) { return new RubyModelStatus(
                IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST, workingCopy); // was
        // destroyed
        }
        return status;
    }

}