package org.rubypeople.rdt.internal.debug.ui;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.rubypeople.rdt.debug.ui.RdtDebugUiConstants;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.debug.core.model.RubyVariable;

public class RdtDebugUiPlugin extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "org.rubypeople.rdt.debug.ui"; //$NON-NLS-1$
	protected static RdtDebugUiPlugin plugin;

	public RdtDebugUiPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static IWorkbenchPage getActivePage() {
		return RdtDebugUiPlugin.getActiveWorkbenchWindow().getActivePage();
	}

	public static RdtDebugUiPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return RubyPlugin.getWorkspace();
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, RdtDebugUiMessages.getString("RdtDebugUiPlugin.internalErrorOccurred"), e)); //$NON-NLS-1$
	}

	protected void initializeDefaultPreferences(IPreferenceStore store) {
		super.initializeDefaultPreferences(store);
		
		store.setDefault(RdtDebugUiConstants.PREFERENCE_KEYWORDS, getDefaultKeywords());
	}

	protected String getDefaultKeywords() {
		return "class,def,end,if,module,new,puts,require,rescue,throw,while";
	}
    public void startup() throws CoreException {

        super.startup();
		IAdapterManager manager= Platform.getAdapterManager();
		ActionFilterAdapterFactory actionFilterAdapterFactory= new ActionFilterAdapterFactory();
		manager.registerAdapters(actionFilterAdapterFactory, RubyVariable.class);
    }

}
