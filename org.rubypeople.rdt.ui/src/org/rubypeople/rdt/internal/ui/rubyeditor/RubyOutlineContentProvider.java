package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.internal.core.parser.RubyParsedComponent;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class RubyOutlineContentProvider implements ITreeContentProvider {
	public Object[] getChildren(Object parentElement) {
		return ((RubyParsedComponent)parentElement).getChildren().toArray();
	}

	public Object getParent(Object element) {
		return new Object[] {element + "Parent"};
	}

	public boolean hasChildren(Object element) {
		if (element instanceof RubyParsedComponent) {
			RubyParsedComponent component = (RubyParsedComponent) element;
			return component.getChildren().size() > 0;
		}

		return false;
	}

	public Object[] getElements(Object inputElement) {
		FileEditorInput input = (FileEditorInput)inputElement;
		RubyParsedComponent root = new RubyParser().getComponentHierarchy(input.getFile());
		return new Object[] { root };
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
}
