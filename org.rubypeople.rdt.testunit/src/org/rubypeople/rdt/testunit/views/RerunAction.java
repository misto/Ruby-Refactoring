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

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;

/**
 * Requests to rerun a test.
 */
public class RerunAction extends Action {
	private String fTestId;
	private String fClassName;
	private String fTestName;
	private TestUnitView fTestRunner;
	private String fLaunchMode;
	
	/**
	 * Constructor for RerunAction.
	 */
	public RerunAction(TestUnitView runner, String testId, String className, String testName, String launchMode) {
		super(); 
		if (launchMode.equals(ILaunchManager.RUN_MODE))
			setText(TestUnitMessages.getString("RerunAction.label.run")); //$NON-NLS-1$
		else if (launchMode.equals(ILaunchManager.DEBUG_MODE))
			setText(TestUnitMessages.getString("RerunAction.label.debug")); //$NON-NLS-1$
		// TODO Re-enable help text
		//WorkbenchHelp.setHelp(this, ITestUnitHelpContextIds.RERUN_ACTION);
		fTestRunner= runner;
		fTestId= testId;
		fClassName= className;
		fTestName= testName;
		fLaunchMode= launchMode;
	}

	/*
	 * @see IAction#run()
	 */
	public void run() {
		fTestRunner.rerunTest(fTestId, fClassName, fTestName, fLaunchMode);
	}
}
