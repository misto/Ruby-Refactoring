package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.core.RubyParsedComponent;
import org.rubypeople.rdt.core.RubyParser;

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

	protected class ShamRubyParsedComponent extends RubyParsedComponent {
		protected String name;
		protected int offset, length;

		protected ShamRubyParsedComponent(String name, int offset, int length) {
			super(name);
			this.offset = offset;
			this.length = length;
		}
		
		protected void setChildren(List children) {
			this.children = children;
		}

		public int nameLength() {
			return length;
		}

		public int nameOffset() {
			return offset;
		}

		public int length() {
			return length;
		}

		public int offset() {
			return offset;
		}

	}
}
