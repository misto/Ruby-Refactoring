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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IPropertyListener;
import org.rubypeople.rdt.core.RubyElement;
import org.rubypeople.rdt.internal.ui.resourcesview.RubyResourcesView;

/**
 * The filter can be switched on and off using rubyResourcesView.isRubyFilesOnlyFilterActivated
 */
public class RubyViewerFilter extends ViewerFilter {

	private final RubyResourcesView rubyResourcesView ;

	private IPropertyListener propertyListener = new IPropertyListener() {
		public void propertyChanged(Object source, int property) { 
			if (property == RubyFileMatcher.PROP_MATCH_CRITERIA && source instanceof RubyFileMatcher) {
				rubyResourcesView.getViewer().refresh() ;
			}
		}
	} ;

	public RubyViewerFilter(RubyResourcesView rubyResourcesView) {
		this.rubyResourcesView = rubyResourcesView;
		RdtUiPlugin.getDefault().getRubyFileMatcher().addPropertyChangeListener(propertyListener) ;
	}


	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (!rubyResourcesView.isRubyFilesOnlyFilterActivated()) { return true; }
		if (element instanceof IFolder) { return true; }
		if (element instanceof IFile) {			
			IFile file = (IFile) element ;
			return RdtUiPlugin.getDefault().getRubyFileMatcher().hasRubyEditorAssociation(file) ;
		}
		
		// the rest e.g. projects
		IAdaptable adaptable = (IAdaptable) element;
		RubyElement rubyElement = (RubyElement) adaptable.getAdapter(RubyElement.class);
		return rubyElement != null;
	}

}