package org.rubypeople.rdt.launching;

import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;

public class RdtLaunchingPlugin extends Plugin {
	protected static RdtLaunchingPlugin plugin;

	public RdtLaunchingPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	public static RdtLaunchingPlugin getDefault() {
		return plugin;
	}
}
