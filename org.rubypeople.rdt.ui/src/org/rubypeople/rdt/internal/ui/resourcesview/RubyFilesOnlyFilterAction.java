/*
 * Author: Markus Barchfeld
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. RDT
 * is subject to the "Common Public License (CPL) v 1.0". You may not use RDT
 * except in compliance with the License. For further information see
 * org.rubypeople.rdt/rdt.license.
 */

package org.rubypeople.rdt.internal.ui.resourcesview;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.views.navigator.IResourceNavigator;
import org.eclipse.ui.views.navigator.ResourceNavigatorAction;
import org.rubypeople.rdt.internal.ui.RdtUiMessages;
import org.rubypeople.rdt.internal.ui.RubyViewerFilter;

public class RubyFilesOnlyFilterAction extends ResourceNavigatorAction {

	private RubyViewerFilter rubyViewerFilter;

	public RubyFilesOnlyFilterAction(IResourceNavigator navigator, boolean sortByType) {
		super(navigator, RdtUiMessages.getString("ToggleMenuRubyFilesOnly"));
		this.setToolTipText(RdtUiMessages.getString("ToggleMenuRubyFilesOnly.Tooltip"));
		this.setChecked(((RubyResourcesView) this.getNavigator()).isRubyFilesOnlyFilterActivated()) ;

	}

	public void run() {
		((RubyResourcesView) this.getNavigator()).setRubyFilesOnlyFilterActivated(this.isChecked());
		Viewer viewer = getViewer();
		viewer.getControl().setRedraw(false);
		viewer.refresh();
		viewer.getControl().setRedraw(true);
	}

}