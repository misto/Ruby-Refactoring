package org.rubypeople.rdt.internal.ui.dialog;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.rubypeople.rdt.internal.core.RubyCore;

public class RubyProjectListSelectionDialog extends SelectionDialog {
	protected IProject[] rubyProjects;

	public RubyProjectListSelectionDialog(Shell parentShell) {
		super(parentShell);
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		setTitle("Project Selection");
		setMessage("Select the working directory...");

		CheckboxTableViewer projectListViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		projectListViewer.getTable().setLayoutData(data);

		projectListViewer.setLabelProvider(getLabelProvider());
		projectListViewer.setContentProvider(getContentProvider());
		projectListViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				setSelectedProject((IProject)event.getElement());
			}
		});

		projectListViewer.setInput(RubyCore.getRubyProjects());

		return composite;
	}

	protected void setSelectedProject(IProject aRubyProject) {
		setInitialSelections(new Object[] { aRubyProject });
	}

	protected ITableLabelProvider getLabelProvider() {
		return new ITableLabelProvider() {
			public Image getColumnImage(Object element, int columnIndex) {
				return null;
			}

			public String getColumnText(Object element, int columnIndex) {
				return ((IProject)element).getName();
			}

			public void addListener(ILabelProviderListener listener) {}

			public void dispose() {}

			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			public void removeListener(ILabelProviderListener listener) {}

		};
	}
	
	protected IStructuredContentProvider getContentProvider() {
		return new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				return rubyProjects;
			}

			public void dispose() {}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				rubyProjects = (IProject[]) newInput;
			}
		};
	}
	protected void okPressed() {
		setResult(getInitialSelections());
		super.okPressed();
	}

}