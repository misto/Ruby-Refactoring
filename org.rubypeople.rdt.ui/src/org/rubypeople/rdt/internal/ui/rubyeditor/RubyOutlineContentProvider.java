package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.part.FileEditorInput;

public class RubyOutlineContentProvider implements ITreeContentProvider {
	public Object[] getChildren(Object parentElement) {
		return new Object[] {"getElements"};
	}

	public Object getParent(Object element) {
		return new Object[] {element + "Parent"};
	}

	public boolean hasChildren(Object element) {
		if ("elementTwo".equals(element))
			return true;
		return false;
	}

	public Object[] getElements(Object inputElement) {
		return new Object[] {((FileEditorInput)inputElement).getName(), "elementTwo", "elementThree"};
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
