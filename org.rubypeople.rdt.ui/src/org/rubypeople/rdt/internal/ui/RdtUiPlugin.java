package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public class RdtUiPlugin extends AbstractUIPlugin {
	protected static RdtUiPlugin plugin;
	
	public static final String PLUGIN_ID = "org.rubypeople.rdt.ui"; //$NON-NLS-1$
	public static final String RUBY_RESOURCES_VIEW_ID = PLUGIN_ID + ".ViewRubyResources"; //$NON-NLS-1$

	protected RubyTextTools textTools;

	public RdtUiPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	public static RdtUiPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return RubyPlugin.getWorkspace();
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
	
	public RubyTextTools getTextTools() {
		if (textTools == null) {
			textTools = new RubyTextTools();
		}

		return textTools;
	}
}
