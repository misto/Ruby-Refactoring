package org.rubypeople.rdt.testunit.launcher;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.SocketUtil;
import org.rubypeople.rdt.internal.testunit.ui.TestUnitMessages;
import org.rubypeople.rdt.internal.testunit.ui.TestunitPlugin;
import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;
import org.rubypeople.rdt.launching.RubyLaunchDelegate;

public class TestUnitLaunchConfigurationDelegate extends RubyLaunchDelegate {
	/**
	 * The single test type, or "" iff running a launch container.
	 */
	public static final String TESTTYPE_ATTR = TestunitPlugin.PLUGIN_ID + ".TESTTYPE"; //$NON-NLS-1$
	/**
	 * The test method, or "" iff running the whole test type.
	 */
	public static final String TESTNAME_ATTR = TestunitPlugin.PLUGIN_ID + ".TESTNAME"; //$NON-NLS-1$
	/**
	 * The launch container, or "" iff running a single test type.
	 */
	public static final String LAUNCH_CONTAINER_ATTR = TestunitPlugin.PLUGIN_ID + ".CONTAINER"; //$NON-NLS-1$

	public static final String ID_TESTUNIT_APPLICATION = "org.rubypeople.rdt.testunit.launchconfig"; //$NON-NLS-1$
	
	private int port = -1;

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {	
		IType[] testTypes = findTestTypes(configuration, monitor);
		
//		setDefaultSourceLocator(launch, configuration);
		launch.setAttribute(TestunitPlugin.TESTUNIT_PORT_ATTR, Integer.toString(getPort()));
		if (testTypes.length > 0) launch.setAttribute(TESTTYPE_ATTR, testTypes[0].getHandleIdentifier());

		
		super.launch(configuration, mode, launch, monitor);
	}
	
	protected IType[] findTestTypes(ILaunchConfiguration configuration, IProgressMonitor pm) throws CoreException {
		IRubyProject javaProject= getRubyProject(configuration);
		if ((javaProject == null) || !javaProject.exists()) {
			informAndAbort(TestUnitMessages.TestUnitBaseLaunchConfiguration_error_invalidproject, null, IRubyLaunchConfigurationConstants.ERR_NOT_A_RUBY_PROJECT); 
		}
//		if (!TestSearchEngine.hasTestCaseType(javaProject)) {
//			informAndAbort(TestUnitMessages.JUnitBaseLaunchConfiguration_error_junitnotonpath, null, ITestUnitStatusConstants.ERR_JUNIT_NOT_ON_PATH);
//		}

		String containerHandle = configuration.getAttribute(LAUNCH_CONTAINER_ATTR, ""); //$NON-NLS-1$
		if (containerHandle.length() > 0) {
			IRubyElement element = RubyCore.create(containerHandle);
			IRubyScript script = (IRubyScript) element;
			if (script != null) return new IType[] { script.findPrimaryType() };
		}
		String testTypeName= configuration.getAttribute(TESTTYPE_ATTR, (String) null);
		if (testTypeName != null && testTypeName.length() > 0) {
			return new IType[] {javaProject.findType(testTypeName, pm)};
		}
		return new IType[0];
	}
	
	protected void informAndAbort(String message, Throwable exception, int code) throws CoreException {
		IStatus status= new Status(IStatus.INFO, TestunitPlugin.PLUGIN_ID, code, message, exception);
		if (showStatusMessage(status))
			throw new CoreException(status);
		abort(message, exception, code);
	}
	
	private boolean showStatusMessage(final IStatus status) {
		final boolean[] success= new boolean[] { false };
		getDisplay().syncExec(
				new Runnable() {
					public void run() {
						Shell shell= TestunitPlugin.getActiveWorkbenchShell();
						if (shell == null)
							shell= getDisplay().getActiveShell();
						if (shell != null) {
							MessageDialog.openInformation(shell, TestUnitMessages.JUnitBaseLaunchConfiguration_dialog_title, status.getMessage());
							success[0]= true;
						}
					}
				}
		);
		return success[0];
	}
	
	private Display getDisplay() {
		Display display;
		display= Display.getCurrent();
		if (display == null)
			display= Display.getDefault();
		return display;		
	}
	
	public static String getTestRunnerPath() {
		String directory = RubyCore.getOSDirectory(TestunitPlugin.getDefault());
		File pluginDirFile  = new File(directory, "ruby");
		
		if (!pluginDirFile.exists()) 
			throw new RuntimeException("Expected directory of RemoteTestRunner.rb does not exist: " + pluginDirFile.getAbsolutePath()); 
	
		return pluginDirFile.getAbsolutePath() + File.separator + TestUnitLaunchShortcut.TEST_RUNNER_FILE;
	}
	
	private int getPort() {
		if (port == -1) {
			port  = SocketUtil.findFreePort();
		}
		return port;
	}

	@Override
	public String getProgramArguments(ILaunchConfiguration configuration) throws CoreException {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getLaunchContainerPath(configuration));
		buffer.append(' ');
		buffer.append(Integer.toString(getPort()));
		buffer.append(' ');
		buffer.append(Boolean.toString(false));
		buffer.append(' ');
		buffer.append(configuration.getAttribute(TestUnitLaunchConfigurationDelegate.TESTTYPE_ATTR, ""));
		buffer.append(' ');
		buffer.append(configuration.getAttribute(TestUnitLaunchConfigurationDelegate.TESTNAME_ATTR, ""));
		return buffer.toString();
	}

	private String getLaunchContainerPath(ILaunchConfiguration configuration) throws CoreException {
		String container = configuration.getAttribute(TestUnitLaunchConfigurationDelegate.LAUNCH_CONTAINER_ATTR, "");
		IRubyElement element = (IRubyElement) RubyCore.create(container);
		if (element != null)
		return element.getResource().getLocation().toFile().getAbsolutePath();
		// otherwise it may be an actual path!
		return container;
	}
}