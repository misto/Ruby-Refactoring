package org.rubypeople.rdt.internal.ui;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class RdtUiTestsPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static RdtUiTestsPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public RdtUiTestsPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static RdtUiTestsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
}
