package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.rubypeople.rdt.internal.core.RubyPlugin;

public class RubyViewerFilter extends ViewerFilter {

	public RubyViewerFilter() {
		super();
	}

	public boolean select(Viewer viewer, Object parentElement, Object element) {
		try {
			if (element instanceof IProject)
				if ( ((IProject)element).hasNature(RubyPlugin.RUBY_NATURE_ID) )
					return true;
			if (element instanceof IFile)
				if ( ((IFile)element).getFileExtension().equals("rb") )
					return true;
		} catch(CoreException e) {
			RdtUiPlugin.log(e);
		}
		return false;
	}
}
