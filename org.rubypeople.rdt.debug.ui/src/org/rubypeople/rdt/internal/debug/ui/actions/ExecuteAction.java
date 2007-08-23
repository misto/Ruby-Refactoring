/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.debug.ui.actions;


import java.text.MessageFormat;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.swt.widgets.Display;
import org.rubypeople.rdt.internal.debug.core.model.IEvaluationResult;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.debug.ui.display.IDataDisplay;

public class ExecuteAction extends EvaluateAction {

	/**
	 * @see org.eclipse.jdt.internal.debug.ui.actions.EvaluateAction#displayResult(org.eclipse.jdt.debug.eval.IEvaluationResult)
	 */
	protected void displayResult(final IEvaluationResult result) {
		if (result.hasErrors()) {
			final Display display = RdtDebugUiPlugin.getStandardDisplay();
			display.asyncExec(new Runnable() {
				public void run() {
					if (display.isDisposed()) {
						return;
					}
					reportErrors(result);
					evaluationCleanup();
				}
			});
		} else {		
			IValue value = result.getValue();
			IDataDisplay dataDisplay= getDirectDataDisplay();
			if (dataDisplay != null) {
				try {
					dataDisplay.displayExpressionValue(value.getValueString());
				} catch (DebugException e) {
					RdtDebugUiPlugin.log(e);
				} 
			}
			evaluationCleanup();
		}
	}

	/**
	 * @see org.eclipse.jdt.internal.debug.ui.actions.EvaluateAction#getDataDisplay()
	 */
	protected IDataDisplay getDataDisplay() {
		return super.getDirectDataDisplay();
	}

}
