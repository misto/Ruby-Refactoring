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
 * Toggles console auto-scroll
 */
public class ScrollLockAction extends Action {

	private TestUnitView fRunnerViewPart;

	public ScrollLockAction(TestUnitView viewer) {
		super(TestUnitMessages.getString("ScrollLockAction.action.label")); //$NON-NLS-1$
		fRunnerViewPart = viewer;
		setToolTipText(TestUnitMessages.getString("ScrollLockAction.action.tooltip")); //$NON-NLS-1$
		setDisabledImageDescriptor(TestunitPlugin.getImageDescriptor("dlcl16/lock.gif")); //$NON-NLS-1$
		setHoverImageDescriptor(TestunitPlugin.getImageDescriptor("elcl16/lock.gif")); //$NON-NLS-1$
		setImageDescriptor(TestunitPlugin.getImageDescriptor("elcl16/lock.gif")); //$NON-NLS-1$
		// TODO Re-enable help text
		//		WorkbenchHelp.setHelp(
		//			this,
		//			IJUnitHelpContextIds.OUTPUT_SCROLL_LOCK_ACTION);
		setChecked(false);
	}

	/**
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		fRunnerViewPart.setAutoScroll(!isChecked());
	}
}

