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
package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.rubypeople.rdt.core.RubyElement;
import org.rubypeople.rdt.internal.ui.resourcesview.RubyResourcesView;

public class RubyViewerFilter extends ViewerFilter {

	private RubyResourcesView rubyResourcesView;

	public RubyViewerFilter(RubyResourcesView rubyResourcesView) {
		this.rubyResourcesView = rubyResourcesView;
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!rubyResourcesView.isRubyFilesOnlyFilterActivated()) { return true; }
		if (element instanceof IFolder) { return true; }

		IAdaptable adaptable = (IAdaptable) element;
		RubyElement rubyElement = (RubyElement) adaptable.getAdapter(RubyElement.class);
		return rubyElement != null;
	}

}