package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.RubyElement;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class RubyPlugin extends Plugin {

	public final static String PLUGIN_ID = "org.rubypeople.rdt.core";
	public final static String RUBY_NATURE_ID = PLUGIN_ID + ".rubynature";

	private boolean isCodeFormatterDebugging = false;

	protected static RubyPlugin plugin;

	public RubyPlugin() {
		super();
		plugin = this;
	}

	public static RubyPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		IAdapterManager manager = Platform.getAdapterManager();
		manager.registerAdapters(new RubyElementAdapterFactory(), RubyElement.class);
		manager.registerAdapters(new ResourceAdapterFactory(), IResource.class);
		String codeFormatterOption = Platform.getDebugOption(RubyPlugin.PLUGIN_ID + "/codeformatter");
		isCodeFormatterDebugging = codeFormatterOption == null ? false : codeFormatterOption.equalsIgnoreCase("true");
		String rubyParserOption = Platform.getDebugOption(RubyPlugin.PLUGIN_ID + "/rubyparser");
		RubyParser.setDebugging(rubyParserOption == null ? false : rubyParserOption.equalsIgnoreCase("true"));
	
	}

	public static void log(Exception runtimeException) {
		getDefault().getLog().log(new Status(Status.INFO, RubyPlugin.PLUGIN_ID, 0, runtimeException.getMessage(), runtimeException));
	}

	/**
	 * @param string
	 */
	public static void log(String string) {
		log(new Exception(string));
	}

	public boolean isCodeFormatterDebugging() {
		return isCodeFormatterDebugging;
	}
}