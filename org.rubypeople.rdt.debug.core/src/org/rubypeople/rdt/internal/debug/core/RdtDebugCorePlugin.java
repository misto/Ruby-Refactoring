package org.rubypeople.rdt.internal.debug.core;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.internal.core.RubyPlugin;

public class RdtDebugCorePlugin extends Plugin {

	public static final String PLUGIN_ID = "org.rubypeople.rdt.debug.core"; //$NON-NLS-1$
	private boolean isRubyDebuggerVerbose = false;
	protected static RdtDebugCorePlugin plugin;

	public RdtDebugCorePlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
	}

	public static RdtDebugCorePlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return RubyPlugin.getWorkspace();
	}

	public void startup() throws CoreException {
		super.startup();
		String rubyDebuggerVerboseOption = Platform.getDebugOption(RdtDebugCorePlugin.PLUGIN_ID + "/rubyDebuggerVerbose");
		isRubyDebuggerVerbose = rubyDebuggerVerboseOption == null ? false : rubyDebuggerVerboseOption.equalsIgnoreCase("true");
	}

	public static void log(int severity, String message) {
		Status status = new Status(severity, PLUGIN_ID, IStatus.OK, message, null);
		RdtDebugCorePlugin.log(status);
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, "RdtLaunchingPlugin.internalErrorOccurred", e)); //$NON-NLS-1$
	}

	public static void debug(Object message) {
		if (RdtDebugCorePlugin.getDefault() != null) {
			if (RdtDebugCorePlugin.getDefault().isDebugging()) {
				System.out.println(message.toString());
			}

		} else {
			// Called from Unit-Test, Plugin not initialized
			System.out.println(message.toString());
		}
	}

	public static void debug(String message, Throwable e) {
		if (RdtDebugCorePlugin.getDefault() != null) {
			if (RdtDebugCorePlugin.getDefault().isDebugging()) {
				System.out.println(message);
				RdtDebugCorePlugin.log(e);
			}

		} else {
			// Called from Unit-Test, Plugin not initialized
			System.out.println(message);
			e.printStackTrace();
		}

	}

	public boolean isRubyDebuggerVerbose() {
		return isRubyDebuggerVerbose;
	}
}