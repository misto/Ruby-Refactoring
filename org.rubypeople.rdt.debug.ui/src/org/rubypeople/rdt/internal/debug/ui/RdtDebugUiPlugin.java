package org.rubypeople.rdt.internal.debug.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class RdtDebugUiPlugin extends AbstractUIPlugin {
	private static RdtDebugUiPlugin plugin;
	private ResourceBundle resourceBundle;
	
	public static final String PLUGIN_ID = "org.rubypeople.rdt.debug.ui";

	public RdtDebugUiPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle(RdtDebugUiPlugin.class.getName());
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}	
	
	public static IWorkbenchPage getActivePage() {
		return getDefault().getActiveWorkbenchWindow().getActivePage();
	}

	public static RdtDebugUiPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle= RdtDebugUiPlugin.getDefault().getResourceBundle();
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
