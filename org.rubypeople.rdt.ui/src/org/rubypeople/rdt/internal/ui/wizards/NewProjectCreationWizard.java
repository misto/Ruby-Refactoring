package org.rubypeople.rdt.internal.ui.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.rubypeople.rdt.internal.core.RubyCore;
import org.rubypeople.rdt.internal.ui.RdtUiMessages;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;

public class NewProjectCreationWizard extends BasicNewResourceWizard implements INewWizard, IExecutableExtension {
	protected WizardNewProjectCreationPage projectPage;
	protected IConfigurationElement configurationElement;
	protected IProject newProject;
	protected String defaultProjectName = "";
	
	public NewProjectCreationWizard() {
		setWindowTitle(RdtUiMessages.getString("NewProjectCreationWizard.windowTitle"));
	}

	public boolean performFinish() {
		IRunnableWithProgress projectCreationOperation = new WorkspaceModifyDelegatingOperation(getProjectCreationRunnable());

		try {
			getContainer().run(false, true, projectCreationOperation);
		} catch (Exception e) {
			RdtUiPlugin.log(e);
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(configurationElement);
		selectAndReveal(newProject);

		return true;
	}

	protected IRunnableWithProgress getProjectCreationRunnable() {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				int remainingWorkUnits = 10;
				monitor.beginTask(RdtUiMessages.getString("NewProjectCreationWizard.projectCreationMessage"), remainingWorkUnits);

				IWorkspace workspace = RdtUiPlugin.getWorkspace();
				newProject = projectPage.getProjectHandle();
				
				IProjectDescription description = workspace.newProjectDescription(newProject.getName());
				IPath path = Platform.getLocation();
				IPath customPath = projectPage.getLocationPath();
				if (!path.equals(customPath)) {
					path = customPath;
					description.setLocation(path);
				}

				try {
					if (!newProject.exists()) {
						newProject.create(description, new SubProgressMonitor(monitor, 1));
						remainingWorkUnits--;
					}
					if (!newProject.isOpen()) {
						newProject.open(new SubProgressMonitor(monitor, 1));
						remainingWorkUnits--;
					}
					RubyCore.addRubyNature(newProject, new SubProgressMonitor(monitor, remainingWorkUnits));
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			}
		};
	}

	public void addPages() {
		super.addPages();

		projectPage = new WizardNewProjectCreationPage(RdtUiMessages.getString("WizardNewProjectCreationPage.pageName"));
		projectPage.setTitle(RdtUiMessages.getString("WizardNewProjectCreationPage.pageTitle"));
		projectPage.setDescription(RdtUiMessages.getString("WizardNewProjectCreationPage.pageDescription"));
		projectPage.setInitialProjectName(this.getDefaultProjectName()) ;

		addPage(projectPage);
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		configurationElement = config;
	}


	public String getDefaultProjectName() {
		return defaultProjectName;
	}

	public void setDefaultProjectName(String defaultProjectName) {
		this.defaultProjectName = defaultProjectName;
	}
}