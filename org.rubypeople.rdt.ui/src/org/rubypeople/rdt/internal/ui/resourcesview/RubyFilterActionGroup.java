/*
 * Author: Adam Williams, Markus Barchfeld
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT
 * is subject to the "Common Public License (CPL) v 1.0". You may not use RDT
 * except in compliance with the License. For further information see
 * org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.internal.ui.resourcesview;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.views.navigator.MainActionGroup;
import org.eclipse.ui.views.navigator.ResourceNavigator;

public class RubyFilterActionGroup extends MainActionGroup {

	private RubyFilesOnlyFilterAction rubyFilesOnlyAction;

	public RubyFilterActionGroup(ResourceNavigator navigator) {
		super(navigator);
	}

	protected void makeActions() {
		super.makeActions();
		rubyFilesOnlyAction = new RubyFilesOnlyFilterAction(navigator, false);
	}

	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);
		IMenuManager menu = actionBars.getMenuManager();
		menu.add(rubyFilesOnlyAction);
	}

	public RubyFilesOnlyFilterAction getRubyFilesOnlyAction() {
		return rubyFilesOnlyAction;
	}
}