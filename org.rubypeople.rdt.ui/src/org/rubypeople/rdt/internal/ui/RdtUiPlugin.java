package org.rubypeople.rdt.internal.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class RdtUiPlugin extends AbstractUIPlugin {
	protected static RdtUiPlugin plugin;
	
	public static final String PLUGIN_ID = "org.rubypeople.rdt.ui"; //$NON-NLS-1$
	public static final String RUBY_RESOURCES_VIEW_ID = PLUGIN_ID + ".ViewRubyResources"; //$NON-NLS-1$

	public RdtUiPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	public static RdtUiPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}
	
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, RdtUiMessages.getString("RdtUiPlugin.internalErrorOccurred"), e)); //$NON-NLS-1$
	}
	
	public static Shell getActiveWorkbenchShell() {
		return getActiveWorkbenchWindow().getShell();
	}
}
