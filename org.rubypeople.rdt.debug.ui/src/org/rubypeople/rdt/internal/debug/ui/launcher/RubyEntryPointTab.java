package org.rubypeople.rdt.internal.debug.ui.launcher;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.rubypeople.rdt.internal.core.RubyCore;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;
import org.rubypeople.rdt.internal.ui.RdtUiImages;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.utils.RubyFileSelector;
import org.rubypeople.rdt.internal.ui.utils.RubyProjectSelector;

public class RubyEntryPointTab extends AbstractLaunchConfigurationTab {
	protected String originalFileName, originalProjectName;
	protected RubyProjectSelector projectSelector;
	protected RubyFileSelector fileSelector;

	public RubyEntryPointTab() {
		super();
	}

	public void createControl(Composite parent) {
		Composite composite = createPageRoot(parent);

		new Label(composite, SWT.NONE).setText(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyEntryPoint.projectLabel"));
		projectSelector = new RubyProjectSelector(composite);
		projectSelector.setBrowseDialogMessage(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyEntryPoint.projectSelectorMessage"));
		projectSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectSelector.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});

		new Label(composite, SWT.NONE).setText(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyEntryPoint.fileLabel"));
		fileSelector = new RubyFileSelector(composite, projectSelector);
		fileSelector.setBrowseDialogMessage(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyEntryPoint.fileSelectorMessage"));
		fileSelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		fileSelector.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});
	}



	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		IResource selectedResource = RdtUiPlugin.getDefault().getSelectedResource() ;
		if (!RdtUiPlugin.getDefault().isRubyFile(selectedResource)) {
			return ;
		}
		IProject project = selectedResource.getProject() ;
		if (project == null || !RubyCore.isRubyProject(project)) {
			return ;
		}
		configuration.setAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, project.getName());
		configuration.setAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, selectedResource.getProjectRelativePath().toString()) ;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			originalProjectName = configuration.getAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, "");
			originalFileName = configuration.getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "");
		} catch (CoreException e) {
			log(e);
		}

		projectSelector.setSelectionText(originalProjectName);
		if (originalFileName.length() != 0) {
			fileSelector.setSelectionText(new Path(originalFileName).toOSString());
		}
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, projectSelector.getSelectionText());
		IFile file = fileSelector.getSelection();
		configuration.setAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, file == null ? "" : file.getProjectRelativePath().toString());
	}

	protected Composite createPageRoot(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		composite.setLayout(layout);

		setControl(composite);
		return composite;
	}

	public String getName() {
		return RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyEntryPoint.name");
	}

	public boolean isValid(ILaunchConfiguration launchConfig) {
		try {
				
			String projectName = launchConfig.getAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, "");
			if (projectName.length() == 0) {
				setErrorMessage(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyEntryPoint.invalidProjectSelectionMessage"));
				return false;
			}

			String fileName = launchConfig.getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "");
			if (fileName.length() == 0) {
				setErrorMessage(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyEntryPoint.invalidFileSelectionMessage"));
				return false;
			}
		} catch (CoreException e) {
			log(e);
		}
		
		setErrorMessage(null);
		return true;
	}

	protected void log(Throwable t) {
		RdtDebugUiPlugin.log(t);
	}

	public boolean canSave() {
		return getErrorMessage() == null;
	}

	public Image getImage() {
		return RdtUiImages.get(RdtUiImages.IMG_CTOOLS_RUBY_PAGE);
	}

}