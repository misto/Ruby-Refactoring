/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Mirko Stocker <me@misto.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.rubypeople.rdt.astviewer.views;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.DrillDownAdapter;
import org.eclipse.ui.part.ViewPart;
import org.jruby.ast.Node;
import org.rubypeople.rdt.astviewer.Activator;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;

public class AstView extends ViewPart {
	private TreeViewer viewer;
	private DrillDownAdapter drillDownAdapter;
	private Action refreshAction;
	private Action doubleClickAction;
	private Action clickAction;
	private ViewContentProvider viewContentProvider;
	private SashForm sashForm;
	private SourceViewer detailsViewer;

	public void createPartControl(Composite parent) {
		sashForm = new SashForm(parent, SWT.NONE);
		sashForm.setOrientation(SWT.VERTICAL);
		
		viewer = new TreeViewer(sashForm, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		drillDownAdapter = new DrillDownAdapter(viewer);
		viewContentProvider = new ViewContentProvider(getViewSite());
		viewer.setContentProvider(viewContentProvider);
		viewer.setLabelProvider(new ViewLabelProvider());

		viewer.setInput(getViewSite());
		makeActions();
		hookContextMenu();
		hookClickAction();
		hookDoubleClickAction();
		contributeToActionBars();
		//setupListeners();
		viewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);

		detailsViewer= new SourceViewer(sashForm, null, SWT.V_SCROLL | SWT.H_SCROLL);
		detailsViewer.setEditable(false);
		detailsViewer.setDocument(new Document());
		Control control = detailsViewer.getControl();
		GridData gd = new GridData(GridData.FILL_BOTH);
		control.setLayoutData(gd);
		
		sashForm.setWeights(new int[]{90, 10});
	}
	
//  Tried to automatically update the view, but could not figure out yet how to do it properly.
//	private void setupListeners(){
//		
//		final IPartListener partListener = new IPartListener(){
//			public void partActivated(IWorkbenchPart part) {
//				partWasActivated();
//			}
//			public void partBroughtToTop(IWorkbenchPart part) {}
//			public void partClosed(IWorkbenchPart part) {}
//			public void partDeactivated(IWorkbenchPart part) {}
//			public void partOpened(IWorkbenchPart part) {
//				partWasActivated();
//			}
//		};
//		
//		PlatformUI.getWorkbench().getActiveWorkbenchWindow().addPageListener(new IPageListener(){
//
//			public void pageActivated(final IWorkbenchPage page) {
//				page.addPartListener(partListener);
//			}
//
//			public void pageClosed(IWorkbenchPage page) {}
//
//			public void pageOpened(IWorkbenchPage page) {
//				page.addPartListener(partListener);
//		}});
//	}
//	
//	private void partWasActivated() {
//		if(viewContentProvider.updateContent()) {
//			viewer.setInput(getViewSite());
//			viewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
//		}
//	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				AstView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(refreshAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		manager.add(refreshAction);
		drillDownAdapter.addNavigationActions(manager);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}
	
	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(refreshAction);
		drillDownAdapter.addNavigationActions(manager);
	}

	private void makeActions() {
		refreshAction = new Action() {
			public void run() {
				viewContentProvider.forceUpdateContent();
				viewer.setInput(getViewSite());
				viewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
			}
		};
		
		refreshAction.setText("Refresh the AST View");
		refreshAction.setToolTipText("Performs a complete refresh over the whole AST.");
		refreshAction.setImageDescriptor(Activator.getImageDescriptor("icons/refresh.gif"));
	}
	
	private void setSelection(RubyEditor editor, Node n) {
		if (n == null)
			return;
		editor.selectAndReveal(n.getPosition().getStartOffset(), n.getPosition().getEndOffset() - n.getPosition().getStartOffset() + 1);
	}
	
	private String formatedPosition(Node n) {
		if(n == null)
			return "";
		
		StringBuilder posString = new StringBuilder();
		posString.append("Lines [");
		posString.append(n.getPosition().getStartLine());
		posString.append(":");
		posString.append(n.getPosition().getEndLine());
		posString.append("], Offset [");
		posString.append(n.getPosition().getStartOffset());
		posString.append(":");
		posString.append(n.getPosition().getEndOffset());
		posString.append("]");
		return posString.toString();
	}
	
	private void hookClickAction() {
		
		clickAction = new Action(){
			public void run() {
				detailsViewer.getDocument().set(formatedPosition(getSelectedNode()));
			}
		};
		
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				clickAction.run();
			}
		});
	}
	
	private Node getSelectedNode() {
		TreeItem[] selection = viewer.getTree().getSelection();
		if(selection.length <= 0)
			return null;
		return ((TreeObject) selection[0].getData()).getNode();
	}
	
	private RubyEditor getEditor() {
		return (RubyEditor) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
	}

	private void hookDoubleClickAction() {
		
		doubleClickAction = new Action(){
			public void run() {
				setSelection(getEditor(), getSelectedNode());
			}
		};
		
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});
	}

	public void setFocus() {
		viewer.getControl().setFocus();
	}
}