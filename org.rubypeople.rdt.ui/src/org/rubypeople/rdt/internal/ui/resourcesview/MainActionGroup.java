/*
 * Author: C.Williams
 * Author: A.Williams
 * Author: M.Barchfeld
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

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.actions.RefreshAction;
import org.eclipse.ui.dialogs.PropertyDialogAction;
import org.eclipse.ui.views.framelist.FrameList;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.eclipse.ui.views.navigator.OpenActionGroup;
import org.eclipse.ui.views.navigator.RefactorActionGroup;
import org.eclipse.ui.views.navigator.ResourcePatternFilter;
import org.eclipse.ui.views.navigator.ResourceSorter;

public class MainActionGroup extends ActionGroup {

	private NewWizardsActionGroup newWizardsActionGroup;
	private OpenActionGroup openActionGroup;
	private RefactorActionGroup refactorActionGroup;
	private PropertyDialogAction propertyDialogAction ;
	private RefreshAction refreshAction ;

	public MainActionGroup(RubyResourcesView rubyResourcesView) {
		ResourceNavigatorAdapter adapter = new ResourceNavigatorAdapter(rubyResourcesView) ;
		this.newWizardsActionGroup = new NewWizardsActionGroup(rubyResourcesView.getSite());
		this.openActionGroup = new OpenActionGroup(adapter);
		this.refactorActionGroup = new RefactorActionGroup(adapter);
		Shell shell = rubyResourcesView.getSite().getShell();
		propertyDialogAction = new PropertyDialogAction(shell, rubyResourcesView.getViewer());
		refreshAction = new RefreshAction(shell) ;
	}

	public void fillContextMenu(IMenuManager menu) {
		this.newWizardsActionGroup.fillContextMenu(menu);
		this.openActionGroup.fillContextMenu(menu);
		menu.add(new Separator());
		this.refactorActionGroup.fillContextMenu(menu);
		menu.add(new Separator());
		menu.add(refreshAction) ;		
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menu.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS + "-end")); //$NON-NLS-1$ 
		menu.add(propertyDialogAction) ;

	}

	public void setContext(ActionContext actionContext) {
		this.openActionGroup.setContext(actionContext);
		this.newWizardsActionGroup.setContext(actionContext);
		this.refactorActionGroup.setContext(actionContext);
	}

	public void runDefaultAction(IStructuredSelection selection) {
		this.openActionGroup.runDefaultAction(selection);
	}

	class ResourceNavigatorAdapter implements IResourceNavigator {
		RubyResourcesView rubyResourcesView;
		ResourceNavigatorAdapter(RubyResourcesView rubyResourcesView) {
			this.rubyResourcesView = rubyResourcesView;
		}

		public FrameList getFrameList() {
			return null;
		}

		public ResourcePatternFilter getPatternFilter() {
			return null;
		}

		public ResourceSorter getSorter() {
			return null;
		}

		public TreeViewer getViewer() {
			return rubyResourcesView.getViewer();
		}

		public IWorkingSet getWorkingSet() {
			return null;
		}

		public void setFiltersPreference(String[] patterns) {
		}

		public void setSorter(ResourceSorter sorter) {
		}

		public void setWorkingSet(IWorkingSet workingSet) {
		}

		public IViewSite getViewSite() {
			return rubyResourcesView.getViewSite();
		}

		public void init(IViewSite arg0, IMemento arg1) throws PartInitException {
		}

		public void init(IViewSite arg0) throws PartInitException {
		}

		public void saveState(IMemento arg0) {
		}

		public void addPropertyListener(IPropertyListener arg0) {
		}

		public void createPartControl(Composite arg0) {
		}

		public void dispose() {
		}

		public IWorkbenchPartSite getSite() {
			return rubyResourcesView.getSite();
		}

		public String getTitle() {
			return rubyResourcesView.getTitle();
		}

		public Image getTitleImage() {
			return rubyResourcesView.getTitleImage();
		}

		public String getTitleToolTip() {
			return rubyResourcesView.getTitleToolTip();
		}

		public void removePropertyListener(IPropertyListener arg0) {
		}

		public void setFocus() {
		}

		public Object getAdapter(Class arg0) {
			return rubyResourcesView.getAdapter(arg0);
		}

		public boolean isLinkingEnabled() {
			// TODO Auto-generated method stub
			return false;
		}

		public void setLinkingEnabled(boolean arg0) {
			// TODO Auto-generated method stub

		}

	}

}