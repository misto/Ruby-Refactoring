package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.IRubyElement;
import org.rubypeople.rdt.internal.core.parser.ParseException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

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
		try {
			return RubyParser.parse(((IDocument) inputElement).get()).getElements();
		}
		catch (ParseException e) {
			RubyPlugin.log(new RuntimeException(e));
		}
		return new Object[0];
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
