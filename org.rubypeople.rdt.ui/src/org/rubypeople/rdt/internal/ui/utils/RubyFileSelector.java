package org.rubypeople.rdt.internal.ui.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.rubypeople.rdt.internal.ui.dialog.ElementListSelectionDialog;

public class RubyFileSelector extends ResourceSelector {
	protected RubyProjectSelector rubyProjectSelector;

	public RubyFileSelector(Composite parent, RubyProjectSelector aProjectSelector) {
		super(parent);
		Assert.isNotNull(aProjectSelector);
		rubyProjectSelector = aProjectSelector;
		
		browseDialogTitle = "File Selection";
	}

	protected Object[] getRubyFiles() {
		IProject rubyProject = rubyProjectSelector.getSelection();
		if (rubyProject == null)
			return new Object[0];

		RubyElementVisitor visitor = new RubyElementVisitor();
		try {
			rubyProject.accept(visitor);
		} catch(CoreException e) {
			System.out.println("RubyFileSelector.getRubyFiles(): " + e);
		}
		return visitor.getCollectedRubyFiles();
	}

	public IFile getSelection() {
		String fileName = getSelectionText();
		if (fileName != null && !fileName.equals("")) {
			IPath filePath = new Path(fileName);
			IProject project = rubyProjectSelector.getSelection();
			if (project.exists(filePath))
				return project.getFile(filePath);
		}
			
		return null;
	}

	protected void handleBrowseSelected() {
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new WorkbenchLabelProvider());
		dialog.setTitle(browseDialogTitle);
		dialog.setMessage(browseDialogMessage);
		dialog.setElements(getRubyFiles());

		if (dialog.open() == dialog.OK) {
			textField.setText(((IResource) dialog.getFirstResult()).getProjectRelativePath().toString());
		}
	}

}
