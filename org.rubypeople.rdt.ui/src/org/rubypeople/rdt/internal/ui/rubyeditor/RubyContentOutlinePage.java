package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;

public class RubyContentOutlinePage extends ContentOutlinePage implements IResourceChangeListener {
	protected IEditorInput editorInput;

	public RubyContentOutlinePage(IEditorInput editorInput) {
		this.editorInput = editorInput;
	}

	public void setEditorInput(IEditorInput editorInput) {
		this.editorInput = editorInput;
		getTreeViewer().setInput(editorInput);
	}

	public void createControl(Composite parent) {
		super.createControl(parent);

		TreeViewer tree = getTreeViewer();
		tree.setContentProvider(new RubyOutlineContentProvider());
		tree.setLabelProvider(new RubyOutlineLabelProvider());
		tree.setInput(editorInput);
	}

	public void resourceChanged(IResourceChangeEvent event) {
		try {
			event.getDelta().accept(new Visitor());
		} catch (CoreException e) {
			RdtUiPlugin.log(e);
		}
	}

	protected class Visitor implements IResourceDeltaVisitor {
		public boolean visit(IResourceDelta delta) throws CoreException {
			if (delta.getKind() == IResourceDelta.CHANGED) {
				IFile file = (IFile) delta.getResource().getAdapter(IFile.class);
				if (file != null && file.getName().equals(editorInput.getName())) {
					if (getTreeViewer().getContentProvider() != null) {
						getTreeViewer().setInput(editorInput);
						getTreeViewer().expandAll();
					}
					return false;
				}
			}
					
			return true;
		}
	}
}
