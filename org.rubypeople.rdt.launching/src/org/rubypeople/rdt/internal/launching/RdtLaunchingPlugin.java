package org.rubypeople.rdt.internal.launching;

import java.io.File;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.internal.core.RubyPlugin;

public class RdtLaunchingPlugin extends Plugin {
	public static final String PLUGIN_ID = "org.rubypeople.rdt.launching"; //$NON-NLS-1$
    public static String osDependentPath(String aPath) {
        if (Platform.getOS().equals(Platform.OS_WIN32)) {
            if (aPath.startsWith(File.separator)) {
                aPath = aPath.substring(1) ;
            }
            aPath = "\"" + aPath + "\"";
        }

        return aPath;
    }
	protected static RdtLaunchingPlugin plugin;

	public RdtLaunchingPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	public static RdtLaunchingPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return RubyPlugin.getWorkspace();
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, RdtLaunchingMessages.getString("RdtLaunchingPlugin.internalErrorOccurred"), e)); //$NON-NLS-1$
	}
	
	public static void debug(String message) {
		if (RdtLaunchingPlugin.getDefault().isDebugging()) {
			System.out.println(message) ;
		}
	}
}
