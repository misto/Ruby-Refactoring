package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.rubypeople.rdt.core.RubyElement;

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

	public void startup() throws CoreException {
		super.startup();
		IAdapterManager manager= Platform.getAdapterManager();
		manager.registerAdapters(new RubyElementAdapterFactory(), RubyElement.class);
		manager.registerAdapters(new ResourceAdapterFactory(), IResource.class);
	}

}
