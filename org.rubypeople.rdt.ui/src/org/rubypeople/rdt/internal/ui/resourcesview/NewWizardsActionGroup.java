/*
 * Created on Mar 2, 2004
 * 
 * To change the template for this generated file go to Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.internal.ui.resourcesview;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.NewWizardMenu;

/**
 * @author Chris
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code Generation - Code and Comments
 */
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

		// TODO Find out why the context is null. Code is commented out until we can fix that. Should check to see if we're right-clicking on an IRubyElement
		//		ISelection selection = getContext().getSelection();
		//		if (selection instanceof IStructuredSelection) {
		//			IStructuredSelection sel = (IStructuredSelection) selection;
		//			if (sel.size() <= 1 && isNewTarget(sel.getFirstElement())) {
		IMenuManager newMenu = new MenuManager("New");
		menu.appendToGroup("group.new", newMenu);
		new NewWizardMenu(newMenu, fSite.getWorkbenchWindow(), false);
		//			}
		//		}

	}

	//	private boolean isNewTarget(Object element) {
	//		if (element == null)
	//			return true;
	//		if (element instanceof IResource) {
	//			return true;
	//		}
	//		if (element instanceof IRubyElement) {
	//			return true;
	//		}
	//		return false;
	//	}

}
