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
package org.rubypeople.rdt.testunit.views;

import org.eclipse.jface.action.Action;
import org.rubypeople.rdt.testunit.TestunitPlugin;

/**
 * Action to enable/disable stack trace filtering.
 */
public class CompareResultsAction extends Action {

	private FailureTrace fView;

	public CompareResultsAction(FailureTrace view) {
		super(TestUnitMessages.getString("CompareResultsAction.label")); //$NON-NLS-1$
		setDescription(TestUnitMessages.getString("CompareResultsAction.description")); //$NON-NLS-1$
		setToolTipText(TestUnitMessages.getString("CompareResultsAction.tooltip")); //$NON-NLS-1$

		setDisabledImageDescriptor(TestunitPlugin.getImageDescriptor("dlcl16/compare.gif")); //$NON-NLS-1$
		setHoverImageDescriptor(TestunitPlugin.getImageDescriptor("elcl16/compare.gif")); //$NON-NLS-1$
		setImageDescriptor(TestunitPlugin.getImageDescriptor("elcl16/compare.gif")); //$NON-NLS-1$
		fView = view;
	}

	/*
	 * @see Action#actionPerformed
	 */
	public void run() {
		// TODO Allow comparison of results
//		CompareResultDialog dialog = new CompareResultDialog(fView.getShell(), fView.getFailedTest());
//		dialog.create();
//		dialog.open();
	}
}