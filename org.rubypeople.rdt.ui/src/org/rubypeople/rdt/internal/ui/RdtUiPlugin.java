package org.rubypeople.rdt.internal.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class RdtUiPlugin extends AbstractUIPlugin {
	protected static RdtUiPlugin plugin;
	protected ResourceBundle resourceBundle;
	
	public static final String PLUGIN_ID = "org.rubypeople.rdt.ui";
	public static final String RUBY_RESOURCES_VIEW_ID = PLUGIN_ID + ".ViewRubyResources";

	public RdtUiPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		try {
			resourceBundle= ResourceBundle.getBundle("org.rubypeople.rdt.ui.UiPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	public static RdtUiPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle= RdtUiPlugin.getDefault().getResourceBundle();
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
