package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.rubypeople.rdt.core.RubyElement;

public class RubyViewerFilter extends ViewerFilter {

	public RubyViewerFilter() {
		super();
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IFolder)
			return true;

		IAdaptable adaptable = (IAdaptable) element;
		RubyElement rubyElement = (RubyElement) adaptable.getAdapter(RubyElement.class);
		if (rubyElement != null)
			return true;

		return false;
	}
}
