package org.rubypeople.rdt.launching;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;

public class RdtLaunchingPlugin extends Plugin {
	public static final String PLUGIN_ID = "org.rubypeople.rdt.launching";

	protected static RdtLaunchingPlugin plugin;

	public RdtLaunchingPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	public static RdtLaunchingPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
}
