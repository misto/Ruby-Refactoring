package org.rubypeople.rdt.internal.ui.preferences;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class RubyLibraryContentProvider implements IStructuredContentProvider {
	protected List libraries;

	public RubyLibraryContentProvider() {
		super();
	}

	public Object[] getElements(Object inputElement) {
		return libraries.toArray();
	}

	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		libraries = (List) newInput;
	}
}
