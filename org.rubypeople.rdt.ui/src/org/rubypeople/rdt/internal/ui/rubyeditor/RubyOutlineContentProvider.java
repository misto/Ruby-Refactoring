package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.rubypeople.rdt.internal.core.parser.IRubyElement;
import org.rubypeople.rdt.internal.core.parser.RubyScript;

public class RubyOutlineContentProvider implements ITreeContentProvider, IDocumentListener {
	protected Viewer viewer;
	
	public Object[] getChildren(Object parentElement) {
		IRubyElement rubyElement = (IRubyElement) parentElement;
		return rubyElement.getElements();
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		IRubyElement rubyElement = (IRubyElement) element;
		return rubyElement.hasElements();
	}

	public Object[] getElements(Object inputElement) {
		return new RubyScript(((IDocument) inputElement).get()).getElements();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		if (oldInput != null) 
			((IDocument)oldInput).removeDocumentListener(this);
		if (newInput != null) 
			((IDocument)newInput).addDocumentListener(this);
	}

	public void documentAboutToBeChanged(DocumentEvent event) {
	}

	public void documentChanged(DocumentEvent event) {
		viewer.refresh();
	}
}
