package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ISetSelectionTarget;
import org.eclipse.ui.part.ViewPart;

public class RubyResourcesView extends ViewPart implements ISetSelectionTarget {
	protected TreeViewer viewer;

	public RubyResourcesView() {
	}

	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent);

		viewer.addFilter(new RubyViewerFilter());
		viewer.setContentProvider(new WorkbenchContentProvider());
		viewer.setLabelProvider(new WorkbenchLabelProvider());
		viewer.setInput(getInitialInput());
	}

	public void selectReveal(ISelection selection) {
	 	viewer.setSelection(selection, true);
	}

	protected IContainer getInitialInput() {
		IAdaptable input = getSite().getPage().getInput();
		IResource resource = null;
		if (input instanceof IResource) {
			resource = (IResource) input;
		} else {
			resource = (IResource) input.getAdapter(IResource.class);
		}
		if (resource != null) {
			switch (resource.getType()) {
				case IResource.FILE :
					return resource.getParent();
				case IResource.FOLDER :
				case IResource.PROJECT :
				case IResource.ROOT :
					return (IContainer) resource;
				default :
					break;
			}
		}
		return RdtUiPlugin.getWorkspace().getRoot();
	}
	
	public void setFocus() {
		viewer.getControl().setFocus();
	}
}