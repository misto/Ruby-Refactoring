package org.rubypeople.rdt.internal.ui;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.actions.WorkspaceModifyDelegatingOperation;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.eclipse.ui.wizards.newresource.BasicNewProjectResourceWizard;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import sun.security.krb5.internal.crypto.e;

public class NewRubyProjectWizard extends BasicNewResourceWizard implements IExecutableExtension {
	protected WizardNewProjectCreationPage projectPage;
	protected IConfigurationElement configurationElement;
	protected IProject newProject;
	
	public NewRubyProjectWizard() {
		setWindowTitle("New");
	}

	public boolean performFinish() {
		IRunnableWithProgress projectCreationOperation = new WorkspaceModifyDelegatingOperation(getProjectCreationRunnable());

		try {
			getContainer().run(false, true, projectCreationOperation);
		} catch (InvocationTargetException e) {
			return false;
		} catch (InterruptedException e) {
			return false;
		}

		BasicNewProjectResourceWizard.updatePerspective(configurationElement);
		selectAndReveal(newProject);

		return true;
	}

	protected IRunnableWithProgress getProjectCreationRunnable() {
		return new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
				if (monitor == null) {
					monitor = new NullProgressMonitor();
				}

				int remainingWorkUnits = 10;
				monitor.beginTask("Creating new Ruby Project", remainingWorkUnits);

				IWorkspace workspace = ResourcesPlugin.getWorkspace();
				newProject = projectPage.getProjectHandle();
				try {
					if (!newProject.exists()) {
						newProject.create(new SubProgressMonitor(monitor, 1));
						remainingWorkUnits--;
					}
					if (!newProject.isOpen()) {
						newProject.open(new SubProgressMonitor(monitor, 1));
						remainingWorkUnits--;
					}

					IProjectDescription description = newProject.getDescription();

					String[] prevNatures = description.getNatureIds();
					String[] newNatures = new String[prevNatures.length + 1];
					System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
					newNatures[prevNatures.length] = RubyPlugin.RUBY_NATURE_ID;
					description.setNatureIds(newNatures);

					newProject.setDescription(description, new SubProgressMonitor(monitor, remainingWorkUnits));
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

		projectPage = new WizardNewProjectCreationPage("Create Ruby Project");
		projectPage.setTitle("Ruby Project");
		projectPage.setDescription("Create a new Ruby Project");

		addPage(projectPage);
	}

	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		configurationElement = config;
	}

}