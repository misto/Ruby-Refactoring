package org.rubypeople.rdt.internal.launching;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;

public class RdtLaunchingPlugin extends Plugin {
	public static final String PLUGIN_ID = "org.rubypeople.rdt.launching"; //$NON-NLS-1$

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

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, RdtLaunchingMessages.getString("RdtLaunchingPlugin.internalErrorOccurred"), e)); //$NON-NLS-1$
	}
}
