/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_0.4.0/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.internal.ui.resourcesview;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.NewWizardMenu;
import org.rubypeople.rdt.internal.core.parser.ast.IRubyElement;

public class NewWizardsActionGroup extends ActionGroup {

	private IWorkbenchPartSite fSite;

	/**
	 * @param site
	 */
	public NewWizardsActionGroup(IWorkbenchPartSite site) {
		this.fSite = site;
	}

	/*
	 * (non-Javadoc) Method declared in ActionGroup
	 */
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);

		// TODO Find out why the context is null. Code is commented out until we
		// can fix that. Should check to see if we're right-clicking on an
		// IRubyElement
		ISelection selection = getContext().getSelection();
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			if (sel.size() <= 1 && isNewTarget(sel.getFirstElement())) {
				IMenuManager newMenu = new MenuManager("New");
				menu.appendToGroup("group.new", newMenu);
				new NewWizardMenu(newMenu, fSite.getWorkbenchWindow(), false);
			}
		}

	}

	private boolean isNewTarget(Object element) {
		if (element == null) return true;
		if (element instanceof IResource) { return true; }
		if (element instanceof IRubyElement) { return true; }
		return false;
	}

}