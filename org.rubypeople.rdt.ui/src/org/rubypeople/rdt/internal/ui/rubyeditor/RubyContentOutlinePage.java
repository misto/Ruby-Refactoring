package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class RubyContentOutlinePage extends ContentOutlinePage {
	protected IEditorInput editor;
	
	public RubyContentOutlinePage(IEditorInput rubyEditor) {
		editor = rubyEditor;
	}
	public void createControl(Composite parent) {
		super.createControl(parent);
		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(new RubyOutlineContentProvider());
		tree.setLabelProvider(new RubyOutlineLabelProvider());
		tree.setInput(editor);
	}

}
