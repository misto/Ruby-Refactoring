package org.rubypeople.rdt.internal.debug.ui;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import java.util.*;

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
