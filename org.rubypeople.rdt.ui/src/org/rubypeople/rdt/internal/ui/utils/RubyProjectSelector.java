package org.rubypeople.rdt.internal.ui.utils;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.rubypeople.rdt.internal.core.RubyCore;
import org.rubypeople.rdt.internal.ui.dialog.ElementListSelectionDialog;

public class RubyProjectSelector extends ResourceSelector {

	public RubyProjectSelector(Composite parent) {
		super(parent);
		
		browseDialogTitle = "Project Selection";
	}

	public IProject getSelection() {
		String projectName = getSelectionText();
		if (projectName != null && !projectName.equals(""))
			return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			
		return null;
	}

	protected void handleBrowseSelected() {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new WorkbenchLabelProvider());
		dialog.setTitle(browseDialogTitle);
		dialog.setMessage(browseDialogMessage);
		dialog.setElements(RubyCore.getRubyProjects());

		if (dialog.open() == dialog.OK) {
			textField.setText(((IProject) dialog.getFirstResult()).getName());
		}
	}

	protected String validateResourceSelection() {
		IProject project = getSelection();
		return project == null ? EMPTY_STRING : project.getName();
	}
}