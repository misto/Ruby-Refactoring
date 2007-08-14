/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.ui.actions;

import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.core.IOpenable;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.IRubyHelpContextIds;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.actions.SelectionConverter;
import org.rubypeople.rdt.internal.ui.resourcesview.RubyResourcesView;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
/**
 * This action reveals the currently selected Ruby element in the 
 * package explorer. 
 * <p>
 * The action is applicable to selections containing elements of type
 * <code>IRubyElement</code>.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * @since 2.0
 */
public class ShowInRubyResourcesViewAction extends SelectionDispatchAction {
	
	private RubyEditor fEditor;
	
	/**
	 * Creates a new <code>ShowInPackageViewAction</code>. The action requires 
	 * that the selection provided by the site's selection provider is of type 
	 * <code>org.eclipse.jface.viewers.IStructuredSelection</code>.
	 * 
	 * @param site the site providing context information for this action
	 */
	public ShowInRubyResourcesViewAction(IWorkbenchSite site) {
		super(site);
		setText(ActionMessages.ShowInPackageViewAction_label); 
		setDescription(ActionMessages.ShowInPackageViewAction_description); 
		setToolTipText(ActionMessages.ShowInPackageViewAction_tooltip); 
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, IRubyHelpContextIds.SHOW_IN_PACKAGEVIEW_ACTION);	
	}
	
	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param editor the Ruby editor
	 */
	public ShowInRubyResourcesViewAction(RubyEditor editor) {
		this(editor.getEditorSite());
		fEditor= editor;
		setEnabled(SelectionConverter.canOperateOn(fEditor));
	}
	
	/* (non-Rubydoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(ITextSelection selection) {
	}

	/* (non-Rubydoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(checkEnabled(selection));
	}
	
	private boolean checkEnabled(IStructuredSelection selection) {
		if (selection.size() != 1)
			return false;
		return selection.getFirstElement() instanceof IRubyElement;
	}
	
	/* (non-Rubydoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(ITextSelection selection) {
		try {
			IRubyElement element= SelectionConverter.getElementAtOffset(fEditor);
			if (element != null)
				run(element);
		} catch (RubyModelException e) {
			RubyPlugin.log(e);
			String message= ActionMessages.ShowInPackageViewAction_error_message; 
			ErrorDialog.openError(getShell(), getDialogTitle(), message, e.getStatus());
		}	
	}
	
	/* (non-Rubydoc)
	 * Method declared on SelectionDispatchAction.
	 */
	public void run(IStructuredSelection selection) {
		if (!checkEnabled(selection))
			return;
		run((IRubyElement)selection.getFirstElement());
	}
	
	/*
	 * No Rubydoc. The method should be internal but can't be changed since
	 * we shipped it with a public visibility 
	 */
	public void run(IRubyElement element) {
		if (element == null)
			return;
			
		// reveal the top most element only
		IOpenable openable= element.getOpenable();
		if (openable instanceof IRubyElement)
			element= (IRubyElement)openable;
		
		RubyResourcesView view= RubyResourcesView.openInActivePerspective();
		view.tryToReveal(element);
	}

	private static String getDialogTitle() {
		return ActionMessages.ShowInPackageViewAction_dialog_title; 
	}
}