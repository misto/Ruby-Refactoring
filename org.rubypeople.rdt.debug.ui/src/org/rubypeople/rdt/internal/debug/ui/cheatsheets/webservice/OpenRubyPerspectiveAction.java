/*
 * Author: Markus Barchfeld
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT is
 * subject to the "Common Public License (CPL) v 1.0". You may not use RDT except in 
 * compliance with the License. For further information see org.rubypeople.rdt/rdt.license.
 * 
 * The initial version of this file has been copied from org.eclipse.pde.internal.ui.OpenPDEPerspectiveAction.
 * 
 */

package org.rubypeople.rdt.internal.debug.ui.cheatsheets.webservice;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.Action;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.cheatsheets.ICheatSheetAction;
import org.eclipse.ui.cheatsheets.ICheatSheetManager;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;


public class OpenRubyPerspectiveAction extends Action implements ICheatSheetAction {

    /* (non-Javadoc)
     * @see org.eclipse.ui.cheatsheets.ICheatSheetAction#run(java.lang.String[], org.eclipse.ui.cheatsheets.ICheatSheetManager)
     */
    public void run(String[] params, ICheatSheetManager manager) {
        IWorkbenchWindow window = RdtDebugUiPlugin.getActiveWorkbenchWindow();
		IWorkbenchPage page = window.getActivePage();
		IAdaptable input;
		if (page != null)
			input = page.getInput();
		else
			input = ResourcesPlugin.getWorkspace().getRoot();
		try {
			PlatformUI.getWorkbench().showPerspective(
				"org.rubypeople.rdt.ui.PerspectiveRuby", //$NON-NLS-1$
				window,
				input);
			notifyResult(true);
			
		} catch (WorkbenchException e) {
		    RdtDebugUiPlugin.log(e);
			notifyResult(false);
		}

    }

}
