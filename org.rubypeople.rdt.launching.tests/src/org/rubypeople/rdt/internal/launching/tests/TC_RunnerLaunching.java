package org.rubypeople.rdt.internal.launching.tests;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.boot.BootLoader;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.rubypeople.eclipse.testutils.ResourceTools;
import org.rubypeople.rdt.internal.launching.DebuggerRunner;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;
import org.rubypeople.rdt.internal.launching.RubyRuntime;

public class TC_RunnerLaunching extends TestCase {

	private final static String PROJECT_NAME = "Simple Project";
	private final static String RUBY_LIB_DIR = "someRubyDir"; // dir inside project 
	private final static String RUBY_FILE_NAME = "rubyFile.rb";
	private final static String INTERPRETER_ARGUMENTS = "interpreterArguments";
	private final static String PROGRAM_ARGUMENTS = "programArguments";
	private final static String RUBY_COMMAND = "rubyw";
	public TC_RunnerLaunching(String name) {
		super(name);
	}

	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	protected String getCommandLine(IProject project, boolean debug) {
		String quotation = BootLoader.getOS().equals(BootLoader.OS_WIN32) ? "\"" : "";
		StringBuffer commandLine = new StringBuffer();
		if (debug) {
			commandLine.append(" -reclipseDebug");
		}
		commandLine.append(" -I " + quotation + project.getLocation().toOSString() + quotation);
		if (debug) {
			commandLine.append(
				" -I "
					+ quotation
					+ DebuggerRunner.getDirectoryOfRubyDebuggerFile().replace('/', File.separatorChar)
					+ quotation);
		}
		commandLine.append(" " + INTERPRETER_ARGUMENTS + " -- ");
		// use always forward slashes for path relative to project dir
		commandLine.append(
			quotation + project.getLocation().toOSString() + "/" + RUBY_LIB_DIR + "/" + RUBY_FILE_NAME + quotation);
		commandLine.append(" " + PROGRAM_ARGUMENTS);
		return commandLine.toString();
	}

	public void testDebugEnabled() throws Exception {
		// check if debugging is enabled in plugin.xml
		ILaunchConfigurationType launchConfigurationType =
			getLaunchManager().getLaunchConfigurationType(
				RubyLaunchConfigurationAttribute.RUBY_LAUNCH_CONFIGURATION_TYPE);
		assertEquals("Ruby Application", launchConfigurationType.getName());
		assertTrue(
			"LaunchConfiguration supports debug",
			launchConfigurationType.supportsMode(ILaunchManager.DEBUG_MODE));
	}

	public void launch(boolean debug) throws Exception {

		IProject project = ResourceTools.createProject(PROJECT_NAME);

		ShamInterpreter interpreter = new ShamInterpreter("", new Path(""));
		List installedInterpreters = new ArrayList();
		installedInterpreters.add(interpreter);
		RubyRuntime.getDefault().setInstalledInterpreters(installedInterpreters);
		RubyRuntime.getDefault().setSelectedInterpreter(interpreter);
		ILaunchConfiguration configuration = new ShamLaunchConfiguration();
		ILaunch launch = new Launch(configuration, debug ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE, null);
		ILaunchConfigurationType launchConfigurationType =
			getLaunchManager().getLaunchConfigurationType(
				RubyLaunchConfigurationAttribute.RUBY_LAUNCH_CONFIGURATION_TYPE);
		launchConfigurationType.getDelegate().launch(
			configuration,
			debug ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE,
			launch,
			null);

		assertEquals("One process has been spawned", 1, launch.getProcesses().length);
		assertEquals("Assembled command line.", getCommandLine(project, debug), interpreter.getArguments());
		assertEquals(
			"Process label.",
			"Ruby " + RUBY_COMMAND + " : " + RUBY_LIB_DIR + "/" + RUBY_FILE_NAME,
			launch.getProcesses()[0].getLabel());
	}

	public void testRunInDebugMode() throws Exception {
		launch(true);
	}

	public void testRunInRunMode() throws Exception {
		launch(false);
	}

	public class ShamLaunchConfiguration implements ILaunchConfiguration {
		public boolean contentsEqual(ILaunchConfiguration configuration) {
			return false;
		}

		public ILaunchConfigurationWorkingCopy copy(String name) throws CoreException {
			return null;
		}

		public void delete() throws CoreException {
		}

		public boolean exists() {
			return true;
		}

		public boolean getAttribute(String attributeName, boolean defaultValue) throws CoreException {
			return false;
		}

		public int getAttribute(String attributeName, int defaultValue) throws CoreException {
			return 0;
		}

		public List getAttribute(String attributeName, List defaultValue) throws CoreException {
			return null;
		}

		public Map getAttribute(String attributeName, Map defaultValue) throws CoreException {
			return null;
		}

		public String getAttribute(String attributeName, String defaultValue) throws CoreException {
			if (attributeName.equals(RubyLaunchConfigurationAttribute.PROJECT_NAME)) {
				return PROJECT_NAME;
			} else if (attributeName.equals(RubyLaunchConfigurationAttribute.FILE_NAME)) {
				return RUBY_LIB_DIR + File.separator + RUBY_FILE_NAME;
			} else if (attributeName.equals(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY)) {
				return "C:\\Working Dir";
			} else if (attributeName.equals(RubyLaunchConfigurationAttribute.INTERPRETER_ARGUMENTS)) {
				return INTERPRETER_ARGUMENTS;
			} else if (attributeName.equals(RubyLaunchConfigurationAttribute.PROGRAM_ARGUMENTS)) {
				return PROGRAM_ARGUMENTS;
			}

			return null;
		}

		public IFile getFile() {
			return null;
		}

		public IPath getLocation() {
			return null;
		}

		public String getMemento() throws CoreException {
			return null;
		}

		public String getName() {
			return null;
		}

		public ILaunchConfigurationType getType() throws CoreException {
			return null;
		}

		public ILaunchConfigurationWorkingCopy getWorkingCopy() throws CoreException {
			return null;
		}

		public boolean isLocal() {
			return false;
		}

		public boolean isWorkingCopy() {
			return false;
		}

		public ILaunch launch(String mode, IProgressMonitor monitor) throws CoreException {
			return null;
		}

		public boolean supportsMode(String mode) throws CoreException {
			return false;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

		public String getCategory() throws CoreException {
			return null;
		}

		public Map getAttributes() throws CoreException {
			return null;
		}

		public ILaunch launch(String mode, IProgressMonitor monitor, boolean build) throws CoreException {
			return null;
		}
	}

	public class ShamInterpreter extends RubyInterpreter {
		public ShamInterpreter(String aName, IPath validInstallLocation) {
			super(aName, validInstallLocation);
		}
		private String arguments;
		public String getArguments() {
			return arguments;
		}
		public String getCommand() {
			return RUBY_COMMAND;
		}
		public Process exec(String args, File workingDirectory) {
			arguments = args;
			return new ShamProcess();
		}
	}

	public class ShamProcess extends Process {

		public void destroy() {
		}

		public int exitValue() {
			return 0;
		}

		public InputStream getErrorStream() {
			return null;
		}

		public InputStream getInputStream() {
			return null;
		}

		public OutputStream getOutputStream() {
			return null;
		}

		public int waitFor() throws InterruptedException {
			return 0;
		}

	}

}
