package org.rubypeople.rdt.internal.debug.ui.launcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.rubypeople.rdt.core.RubyElement;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;
import org.rubypeople.rdt.internal.launching.RubyRuntime;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;

public class RubyApplicationShortcut implements ILaunchShortcut {

	public void launch(ISelection selection, String mode) {

		Object firstSelection = null;
		if (selection instanceof IStructuredSelection) {
			firstSelection = ((IStructuredSelection) selection).getFirstElement();

		}
		if (firstSelection == null) {
			log("Could not find selection.");
			return;
		}
		RubyElement rubyElement = null;
		if (firstSelection instanceof IAdaptable) {
			rubyElement = (RubyElement) ((IAdaptable) firstSelection).getAdapter(RubyElement.class);
		}
		if (rubyElement == null) {
			log("Selection is not a ruby element.");
			return;
		}
		try {
			ILaunchConfiguration config = findOrCreateLaunchConfiguration(rubyElement, mode);
			if (config != null) {
				config.launch(mode, null);
			}
		} catch (CoreException e) {
			log(e);
		}
	}

	public void launch(IEditorPart editor, String mode) {
		IEditorInput input = editor.getEditorInput();
		if (input == null) {
			log("Could not retrieve input from editor: " + editor.getTitle());
			return;
		}
		RubyElement rubyElement = (RubyElement) input.getAdapter(RubyElement.class);
		if (rubyElement == null) {
			log("Editor input is not a ruby file or external ruby file.");
			return;
		}
		try {
			ILaunchConfiguration config = findOrCreateLaunchConfiguration(rubyElement, mode);
			if (config != null) {
				config.launch(mode, null);
			}
		} catch (CoreException e) {
			log(e);
		}
	}

	protected ILaunchConfiguration findOrCreateLaunchConfiguration(RubyElement rubyElement, String mode) throws CoreException {
		IFile rubyFile = (IFile) rubyElement.getUnderlyingResource();
		ILaunchConfigurationType configType = getRubyLaunchConfigType();
		List candidateConfigs = null;

		ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
		candidateConfigs = new ArrayList(configs.length);
		for (int i = 0; i < configs.length; i++) {
			ILaunchConfiguration config = configs[i];
			boolean projectsEqual = config.getAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, "").equals(rubyFile.getProject().getName());
			if (projectsEqual) {
				boolean projectRelativeFileNamesEqual = config.getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "").equals(rubyFile.getProjectRelativePath().toString());
				if (projectRelativeFileNamesEqual) {
					candidateConfigs.add(config);
				}
			}
		}

		switch (candidateConfigs.size()) {
		case 0:
			return createConfiguration(rubyFile);
		case 1:
			return (ILaunchConfiguration) candidateConfigs.get(0);
		default:
			Status status = new Status(Status.WARNING, RdtDebugUiPlugin.PLUGIN_ID, 0, RdtDebugUiMessages.getString("LaunchConfigurationShortcut.Ruby.multipleConfigurationsError"), null);
			throw new CoreException(status);
		}
	}

	protected ILaunchConfiguration createConfiguration(IFile rubyFile) {
		if (RubyRuntime.getDefault().getSelectedInterpreter() == null) {
			this.showNoInterpreterDialog() ;
			return null ;
		}
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getRubyLaunchConfigType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(rubyFile.getName()));
			wc.setAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, rubyFile.getProject().getName());
			wc.setAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, rubyFile.getProjectRelativePath().toString());
			wc.setAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, RubyApplicationShortcut.getDefaultWorkingDirectory(rubyFile.getProject()));
			wc.setAttribute(RubyLaunchConfigurationAttribute.SELECTED_INTERPRETER, RubyRuntime.getDefault().getSelectedInterpreter().getName());
			wc.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, "org.rubypeople.rdt.debug.ui.rubySourceLocator");
			config = wc.doSave();
		} catch (CoreException ce) {
			log(ce);
		}
		return config;
	}

	protected ILaunchConfigurationType getRubyLaunchConfigType() {
		return getLaunchManager().getLaunchConfigurationType(RubyLaunchConfigurationAttribute.RUBY_LAUNCH_CONFIGURATION_TYPE);
	}

	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	protected void log(String message) {
		RdtDebugUiPlugin.log(new Status(Status.INFO, RdtDebugUiPlugin.PLUGIN_ID, Status.INFO, message, null));
	}

	protected void log(Throwable t) {
		RdtDebugUiPlugin.log(t);
	}
	
	protected void showNoInterpreterDialog() {
		MessageDialog.openInformation(RdtUiPlugin.getActiveWorkbenchShell(), RdtDebugUiMessages.getString("Dialog.launchWithoutSelectedInterpreter.title"), RdtDebugUiMessages.getString("Dialog.launchWithoutSelectedInterpreter"));
	}	

	protected static String getDefaultWorkingDirectory(IProject project) {
		if (project != null && project.exists()) {
			return project.getLocation().toOSString() ;
		}
		else {
			// might habe been deleted
			return RdtDebugUiPlugin.getWorkspace().getRoot().getLocation().toOSString() ;		
		}
	}
}