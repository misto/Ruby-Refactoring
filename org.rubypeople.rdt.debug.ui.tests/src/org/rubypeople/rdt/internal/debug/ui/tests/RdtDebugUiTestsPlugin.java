package org.rubypeople.rdt.internal.debug.ui.tests;

import org.eclipse.ui.plugin.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;

/**
 * The main plugin class to be used in the desktop.
 */
public class RdtDebugUiTestsPlugin extends AbstractUIPlugin {
	//The shared instance.
	private static RdtDebugUiTestsPlugin plugin;
	
	/**
	 * The constructor.
	 */
	public RdtDebugUiTestsPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	/**
	 * Returns the shared instance.
	 */
	public static RdtDebugUiTestsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the workspace instance.
	 */
	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
}
