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
import org.rubypeople.rdt.core.RubyModelException;

/**
 * Discards a working copy (decrement its use count and remove its working copy
 * info if the use count is 0) and signal its removal through a delta.
 */
public class DiscardWorkingCopyOperation {

	private RubyScript workingCopy;

	public DiscardWorkingCopyOperation(RubyScript workingCopy) {
		this.workingCopy = workingCopy;
	}

	protected void runOperation(IProgressMonitor monitor) throws RubyModelException {
		int useCount = RubyModelManager.getRubyModelManager().discardPerWorkingCopyInfo(workingCopy);
		if (useCount == 0) {
			// TODO Create RubyElementDeltas
		}
	}

	/**
	 * @see RubyModelOperation#isReadOnly
	 */
	public boolean isReadOnly() {
		return true;
	}
}
