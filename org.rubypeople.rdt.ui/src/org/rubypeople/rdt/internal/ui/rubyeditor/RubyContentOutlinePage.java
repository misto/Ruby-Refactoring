package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class RubyContentOutlinePage extends ContentOutlinePage {
	protected IDocument document;

	public RubyContentOutlinePage(IDocument document) {
		this.document = document;
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(new RubyOutlineContentProvider());
		tree.setLabelProvider(new RubyOutlineLabelProvider());
		tree.setInput(document);
	}
}
