package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class RubyContentOutlinePage extends ContentOutlinePage {
	protected IEditorInput editorInput;
	
	public RubyContentOutlinePage(IEditorInput editorInput) {
		this.editorInput = editorInput;
	}

	public void setEditorInput(IEditorInput editorInput) {
		this.editorInput = editorInput;
		getTreeViewer().setInput(this.editorInput);
	}

	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(new RubyOutlineContentProvider());
		tree.setLabelProvider(new RubyOutlineLabelProvider());
		tree.setInput(editorInput);
	}
}
