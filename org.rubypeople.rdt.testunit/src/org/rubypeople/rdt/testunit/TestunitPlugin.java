package org.rubypeople.rdt.testunit;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.testunit.views.TestUnitView;

/**
 * The main plugin class to be used in the desktop.
 */
public class TestunitPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "org.rubypeople.rdt.testunit"; //$NON-NLS-1$

	//The shared instance.
	private static TestunitPlugin plugin;
	//Resource bundle.
	private ResourceBundle resourceBundle;

	private static URL fgIconBaseURL;

	/**
	 * The constructor.
	 */
	public TestunitPlugin() {
		super();
		plugin = this;
		String pathSuffix = "icons/full/"; //$NON-NLS-1$
		try {
			fgIconBaseURL = new URL(Platform.getBundle(PLUGIN_ID).getEntry("/"), pathSuffix); //$NON-NLS-1$
		} catch (MalformedURLException e) {
			// do nothing
		}
		try {
			resourceBundle = ResourceBundle.getBundle("org.rubypeople.rdt.testunit.TestunitPluginResources");
		} catch (MissingResourceException x) {
			resourceBundle = null;
		}
	}

	public static URL makeIconFileURL(String name) throws MalformedURLException {
		if (TestunitPlugin.fgIconBaseURL == null) throw new MalformedURLException();
		return new URL(TestunitPlugin.fgIconBaseURL, name);
	}

	public static ImageDescriptor getImageDescriptor(String relativePath) {
		try {
			return ImageDescriptor.createFromURL(makeIconFileURL(relativePath));
		} catch (MalformedURLException e) {
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	/**
	 * Returns the shared instance.
	 */
	public static TestunitPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns the string from the plugin's resource bundle, or 'key' if not
	 * found.
	 */
	public static String getResourceString(String key) {
		ResourceBundle bundle = TestunitPlugin.getDefault().getResourceBundle();
		try {
			return (bundle != null) ? bundle.getString(key) : key;
		} catch (MissingResourceException e) {
			return key;
		}
	}

	/**
	 * Returns the plugin's resource bundle,
	 */
	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	public static String getPluginId() {
		return PLUGIN_ID;
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getPluginId(), IStatus.ERROR, "Error", e)); //$NON-NLS-1$
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static Shell getActiveWorkbenchShell() {
		IWorkbenchWindow workBenchWindow = getActiveWorkbenchWindow();
		if (workBenchWindow == null) return null;
		return workBenchWindow.getShell();
	}

	/**
	 * Returns the active workbench window
	 * 
	 * @return the active workbench window
	 */
	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		if (plugin == null) return null;
		IWorkbench workBench = plugin.getWorkbench();
		if (workBench == null) return null;
		return workBench.getActiveWorkbenchWindow();
	}

	public static IWorkspace getWorkspace() {
		return RubyPlugin.getWorkspace();
	}

	public static Display getDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow activeWorkbenchWindow = getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) return null;
		return activeWorkbenchWindow.getActivePage();
	}

	public void connectTestRunner(ILaunch launch, int port) {
		TestUnitView testRunnerViewPart = showTestUnitViewInActivePage(findTestUnitViewInActivePage());
		if (testRunnerViewPart != null) testRunnerViewPart.startTestRunListening(port, launch);
	}

	private TestUnitView showTestUnitViewInActivePage(TestUnitView testRunner) {
		IWorkbenchPart activePart = null;
		IWorkbenchPage page = null;
		try {
			// TODO: have to force the creation of view part contents
			// otherwise the UI will not be updated
			if (testRunner != null && testRunner.isCreated()) return testRunner;
			page = getActivePage();
			if (page == null) return null;
			activePart = page.getActivePart();
			//	show the result view if it isn't shown yet
			return (TestUnitView) page.showView(TestUnitView.NAME);
		} catch (PartInitException pie) {
			log(pie);
			return null;
		} finally {
			//restore focus stolen by the creation of the result view
			if (page != null && activePart != null) page.activate(activePart);
		}
	}

	private TestUnitView findTestUnitViewInActivePage() {
		IWorkbenchPage page = getActivePage();
		if (page == null) return null;
		return (TestUnitView) page.findView(TestUnitView.NAME);
	}
}