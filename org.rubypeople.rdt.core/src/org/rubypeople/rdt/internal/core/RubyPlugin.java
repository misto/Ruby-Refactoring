package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Plugin;

public class RubyPlugin extends Plugin {
	public final static String PLUGIN_ID = "org.rubypeople.rdt";
	public final static String RUBY_NATURE_ID = PLUGIN_ID + ".rubynature";

	protected static RubyPlugin plugin;

	public RubyPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	public static RubyPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
}
