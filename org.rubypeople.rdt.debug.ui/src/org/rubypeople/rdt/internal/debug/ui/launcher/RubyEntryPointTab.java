package org.rubypeople.rdt.internal.debug.ui.launcher;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.ui.utils.RubyFileSelector;
import org.rubypeople.rdt.internal.ui.utils.RubyProjectSelector;

public class RubyEntryPointTab extends AbstractLaunchConfigurationTab {
	protected RubyProjectSelector projectSelector;
	protected RubyFileSelector fileSelector;

	public RubyEntryPointTab() {
		super();
	}

	public void createControl(Composite parent) {
		Composite composite = createPageRoot(parent);
		
		new Label(composite, SWT.NONE).setText("Project:");
		projectSelector = new RubyProjectSelector(composite);
		projectSelector.setBrowseDialogMessage("Choose the project containing the application entry point:");
		projectSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectSelector.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});

		new Label(composite, SWT.NONE).setText("Class:");
		fileSelector = new RubyFileSelector(composite, projectSelector);
		fileSelector.setBrowseDialogMessage("Choose the Ruby file that represents the application entry point:");
		fileSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileSelector.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});
	}

	protected IProject getContext() {
		IWorkbenchPage page = RdtDebugUiPlugin.getActivePage();
		if (page != null) {
			ISelection selection = page.getSelection();
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection ss = (IStructuredSelection)selection;
				if (!ss.isEmpty()) {
					Object obj = ss.getFirstElement();
					if (obj instanceof IResource)
							return ((IResource)obj).getProject();
				}
			}
			IEditorPart part = page.getActiveEditor();
			if (part != null) {
				IEditorInput input = part.getEditorInput();
				IResource file = (IResource) input.getAdapter(IResource.class);
				return file.getProject();
			}
		}
		return null;
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IProject project = getContext();
		if (project != null)
			configuration.setAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, project.getName());
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		String projectName = "", fileName = "";

		try {
			projectName = configuration.getAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, "");
			fileName = configuration.getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "");
		} catch(CoreException e) {}

		projectSelector.setSelectionText(projectName);
		fileSelector.setSelectionText(fileName);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, projectSelector.getSelectionText());
		configuration.setAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, fileSelector.getSelectionText());
	}

	public boolean isValid() {
		setErrorMessage(null);
		setMessage(null);
		
		IProject project = projectSelector.getSelection();
		if (project == null || !project.exists()) {
			setErrorMessage("Invalid project selection.");
			return false;
		}

		if (fileSelector.getSelection() == null) {
			setErrorMessage("Invalid Ruby file.");
			return false;
		}
		
		return true;
	}

	protected Composite createPageRoot(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		composite.setLayout(layout);

		setControl(composite);
		return composite;
	}
}