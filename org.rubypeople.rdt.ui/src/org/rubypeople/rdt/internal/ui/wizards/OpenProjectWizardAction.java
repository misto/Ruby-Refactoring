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
package org.rubypeople.rdt.internal.ui.wizards;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.help.WorkbenchHelp;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;

public class OpenProjectWizardAction extends AbstractOpenWizardAction {

	public OpenProjectWizardAction() {
		WorkbenchHelp.setHelp(this, IRubyHelpContextIds.OPEN_PROJECT_WIZARD_ACTION);
//		 FIXME Uncomment and delete the above line when we move to 3.1+
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.OPEN_PROJECT_WIZARD_ACTION);
	}
	
	public OpenProjectWizardAction(String label, Class[] acceptedTypes) {
		super(label, acceptedTypes, true);
		WorkbenchHelp.setHelp(this, IRubyHelpContextIds.OPEN_PROJECT_WIZARD_ACTION);
		// FIXME Uncomment and delete the above line when we move to 3.1+
		//PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.OPEN_PROJECT_WIZARD_ACTION);
	}
	
	protected Wizard createWizard() { 
		return new NewProjectCreationWizard(); 
	}	
	/*
	 * @see AbstractOpenWizardAction#showWorkspaceEmptyWizard()
	 */
	protected boolean checkWorkspaceNotEmpty() {
		return true;
	}

}
