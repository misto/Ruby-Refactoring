package org.rubypeople.rdt.internal.ui.rubyeditor.outline;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.texteditor.ExtendedTextEditor;
import org.rubypeople.rdt.internal.core.parser.ast.IRubyElement;

public class RubyOutlineContentProvider implements ITreeContentProvider, IDocumentListener {

	protected Viewer viewer;
	private ExtendedTextEditor fTextEditor;

	/**
	 * @param textEditor
	 */
	public RubyOutlineContentProvider(ExtendedTextEditor textEditor) {
		this.fTextEditor = textEditor;
	}

	public Object[] getChildren(Object parentElement) {
		IRubyElement rubyElement = (IRubyElement) parentElement;
		return rubyElement.getElements();
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IRubyElement) { return ((IRubyElement) element).hasElements(); }
		return false;
	}

	public Object[] getElements(Object inputElement) {
		IRubyElement element = (IRubyElement) inputElement;
		return element.getElements();
	}
	
	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
	}

	public void documentAboutToBeChanged(DocumentEvent event) {}

	public void documentChanged(DocumentEvent event) {
		viewer.refresh();
	}
}
