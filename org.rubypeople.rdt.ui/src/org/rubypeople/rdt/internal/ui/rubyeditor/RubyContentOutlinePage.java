package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;

public class RubyContentOutlinePage extends ContentOutlinePage {
	protected IEditorInput input;
	
	public RubyContentOutlinePage(IEditorInput theInput) {
		input = theInput;
	}
	public void createControl(Composite parent) {
		super.createControl(parent);
	}

}
