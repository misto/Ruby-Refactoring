package org.rubypeople.rdt.internal.debug.ui.launcher;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.ILaunchShortcut;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;

public class RubyApplicationShortcut implements ILaunchShortcut {
	public RubyApplicationShortcut() {
	}

	public void launch(ISelection selection, String mode)  {
		if (selection instanceof IStructuredSelection) {
			Object firstSelection = ((IStructuredSelection)selection).getFirstElement();
			if (firstSelection instanceof IFile) {
				if (((IFile) firstSelection).getFileExtension().equals("rb")) {
					ILaunchConfiguration config = findLaunchConfiguration((IFile)firstSelection, mode);
					try {
						if (config != null)
							config.launch(mode, null);
					} catch (CoreException e) {
						log(e);
					}
					return;
				}
			}
		}

		log("The resource selected is not a Ruby file.");
	}

	public void launch(IEditorPart editor, String mode)  {
		IEditorInput input = editor.getEditorInput();
		ISelection selection = new StructuredSelection(input.getAdapter(IFile.class));
		launch(selection, mode);
	}

	protected ILaunchConfiguration findLaunchConfiguration(IFile rubyFile, String mode) {
		ILaunchConfigurationType configType = getRubyLaunchConfigType();
		List candidateConfigs = null;
		try {
			ILaunchConfiguration[] configs = getLaunchManager().getLaunchConfigurations(configType);
			candidateConfigs = new ArrayList(configs.length);
			for (int i = 0; i < configs.length; i++) {
				ILaunchConfiguration config = configs[i];
				if (config.getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "").equals(rubyFile.getFullPath().toString())) {
						candidateConfigs.add(config);
				}
			}
		} catch (CoreException e) {
			RdtDebugUiPlugin.getDefault().log(e);
		}
		
		switch (candidateConfigs.size()) {
			case 0 :
				return createConfiguration(rubyFile);
			case 1 :
				return (ILaunchConfiguration) candidateConfigs.get(0);
			default :
				log(new RuntimeException(RdtDebugUiMessages.getString("LaunchConfigurationShortcut.Ruby.multipleConfigurationsError")));
				return null;
		}
	}

	protected ILaunchConfiguration createConfiguration(IFile rubyFile) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = getRubyLaunchConfigType();
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, getLaunchManager().generateUniqueLaunchConfigurationNameFrom(rubyFile.getName())); 
			wc.setAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, rubyFile.getFullPath().toString());
			config = wc.doSave();		
		} catch (CoreException ce) {
			RdtDebugUiPlugin.getDefault().log(ce);			
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
		RdtDebugUiPlugin.getDefault().log(new Status(Status.INFO, RdtDebugUiPlugin.PLUGIN_ID, Status.INFO, message, null));
	}
	
	protected void log(Throwable t) {
		RdtDebugUiPlugin.getDefault().log(t);
	}
}
