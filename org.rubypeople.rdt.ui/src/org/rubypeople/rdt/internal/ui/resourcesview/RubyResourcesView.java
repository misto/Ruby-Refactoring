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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.RubyViewerFilter;

public class RubyResourcesView extends ViewPart implements ISetSelectionTarget, IMenuListener {
	protected TreeViewer viewer;
	protected MainActionGroup mainActionGroup;

	public RubyResourcesView() {

	}

	public void createPartControl(Composite parent) {
		this.setViewer(new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL));

		this.getViewer().addFilter(new RubyViewerFilter());
		this.getViewer().setContentProvider(new WorkbenchContentProvider());
		this.getViewer().setLabelProvider(new WorkbenchLabelProvider());
		this.getViewer().setInput(getInitialInput());

		this.getViewer().addOpenListener(new IOpenListener() {
			public void open(OpenEvent event) {
				getMainActionGroup().runDefaultAction((IStructuredSelection) event.getSelection());
			}
		});

		MenuManager menuMgr = new MenuManager("org.rubypeople.rdt.ui.RubyPopupMenu"); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(this);

		Menu fContextMenu = menuMgr.createContextMenu(this.getViewer().getTree());
		this.getViewer().getTree().setMenu(fContextMenu);

		// Register viewer with site. This must be done before making the actions.
		IWorkbenchPartSite site = getSite();
		site.registerContextMenu(menuMgr, viewer);
	}

	public void selectReveal(ISelection selection) {
		viewer.setSelection(selection, true);
	}

	protected IContainer getInitialInput() {
		IAdaptable input = getSite().getPage().getInput();
		IResource resource = null;
		if (input instanceof IResource) {
			resource = (IResource) input;
		} else {
			resource = (IResource) input.getAdapter(IResource.class);
		}
		if (resource != null) {
			switch (resource.getType()) {
				case IResource.FILE :
					return resource.getParent();
				case IResource.FOLDER :
				case IResource.PROJECT :
				case IResource.ROOT :
					return (IContainer) resource;
				default :
					break;
			}
		}
		return RdtUiPlugin.getWorkspace().getRoot();
	}

	public void setFocus() {
		this.getViewer().getControl().setFocus();
	}

	public void menuAboutToShow(IMenuManager manager) {
		IStructuredSelection selection = (IStructuredSelection) viewer.getSelection();

		if (manager.isEmpty())
			manager.add(new Separator("group.new"));
		
		this.getMainActionGroup().setContext(new ActionContext(selection));
		this.getMainActionGroup().fillContextMenu(manager);
		this.getMainActionGroup().setContext(null);
	}

	public MainActionGroup getMainActionGroup() {
		if (mainActionGroup == null) {
			// lazy initialization, because this.getSite() delivers null in constructor
			mainActionGroup = new MainActionGroup(this);
		}
		return mainActionGroup;
	}

	public void setMainActionGroup(MainActionGroup mainActionGroup) {
		this.mainActionGroup = mainActionGroup;
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	protected void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
	}

}