package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class RubyOutlineContentProvider implements ITreeContentProvider {
	public Object[] getChildren(Object parentElement) {
		return new Object[] {"getElements"};
	}

	public Object getParent(Object element) {
		return new Object[] {"getParent"};
	}

	public boolean hasChildren(Object element) {
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return new Object[] {"getElements"};
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		System.out.println("inputChanged called");
	}

}
