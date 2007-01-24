package org.rubypeople.rdt.internal.launching;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.rubypeople.eclipse.shams.debug.core.ShamLaunchConfigurationType;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.tests.ModifyingResourceTest;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallType;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.launching.VMStandin;

public class TC_RunnerLaunching extends ModifyingResourceTest {

	private final static String PROJECT_NAME = "Simple Project";
	private final static String RUBY_LIB_DIR = "someRubyDir"; // dir inside project 
	private final static String RUBY_FILE_NAME = "rubyFile.rb"; 
	private final static String INTERPRETER_ARGUMENTS = "interpreter Arguments";
	private final static String PROGRAM_ARGUMENTS = "programArguments";
	private final static String RUBY_COMMAND = "rubyw";
	
	private static final String VM_TYPE_ID = "org.rubypeople.rdt.launching.StandardVMType";
	private IVMInstallType vmType;
	private IRubyProject project;
	
	public TC_RunnerLaunching(String name) {
		super(name);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		vmType = RubyRuntime.getVMInstallType(VM_TYPE_ID);
		VMStandin standin = new VMStandin(vmType, "fake");
		standin.setName("fake");
		standin.setInstallLocation(new File("C:\ruby"));
		IVMInstall real = standin.convertToRealVM();
		RubyRuntime.setDefaultVMInstall(real, null, true);
		project = createRubyProject(PROJECT_NAME);
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		deleteProject(PROJECT_NAME);
	}
	
	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	protected List getCommandLine(IProject project, boolean debug) {
		List commandLine = new ArrayList();
		if (debug) {
			commandLine.add("-reclipseDebug");
		}
		// The include paths and the executed ruby file is quoted on windows		 
		if (debug) {
			String dirOfRubyDebuggerFile = getDirectoryOfRubyDebuggerFile().replace('/', File.separatorChar) ;
			if (dirOfRubyDebuggerFile.startsWith("\\")) {
				dirOfRubyDebuggerFile = dirOfRubyDebuggerFile.substring(1) ;
			}
			commandLine.add("-I");
			commandLine.add(dirOfRubyDebuggerFile);
		}		
        commandLine.add("-I");
        commandLine.add(project.getLocation().toOSString());
        commandLine.add("-I");
        commandLine.add(project.getLocation().toOSString() + File.separator + RUBY_LIB_DIR ) ;
		commandLine.addAll(Arrays.asList(INTERPRETER_ARGUMENTS.split("\\s+")));
		commandLine.add("--");
		// use always forward slashes for path relative to project dir
		commandLine.add(project.getLocation().toOSString() + "/" + RUBY_LIB_DIR + "/" + RUBY_FILE_NAME );
		commandLine.add(PROGRAM_ARGUMENTS);
		return commandLine;
	}

	private String getDirectoryOfRubyDebuggerFile() {
		return RubyCore.getOSDirectory(LaunchingPlugin.getDefault()) + "ruby";
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
		IVMInstall interpreter = new VMStandin(vmType, "");

		ILaunchConfiguration configuration = new ShamLaunchConfiguration();
		ILaunch launch = new Launch(configuration, debug ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE, null);
		ILaunchConfigurationType launchConfigurationType =
			getLaunchManager().getLaunchConfigurationType(
				RubyLaunchConfigurationAttribute.RUBY_LAUNCH_CONFIGURATION_TYPE);
		launchConfigurationType.getDelegate(debug ? "debug" : "run").launch(
			configuration,
			debug ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE,
			launch,
			null);

		assertEquals("One process has been spawned", 1, launch.getProcesses().length);
		List expected = getCommandLine(project.getProject(), debug);
		String[] actual = interpreter.getVMArguments();
		if (debug) {
			// we must cheat with the first argument, because it is a temporary file which
			// contains is different for every call
			expected.add(0, actual[0]) ;
		}
		assertEquals("Assembled command line.", expected, actual);
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
			return new ShamLaunchConfigurationType();
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

		/* (non-Javadoc)
		 * @see org.eclipse.debug.core.ILaunchConfiguration#launch(java.lang.String, org.eclipse.core.runtime.IProgressMonitor, boolean, boolean)
		 */
		public ILaunch launch(String mode, IProgressMonitor monitor, boolean build, boolean register) throws CoreException {
			// TODO Auto-generated method stub
			return null;
		}

        public IResource[] getMappedResources() throws CoreException {
            // TODO Auto-generated method stub
            return null;
        }

        public boolean isMigrationCandidate() throws CoreException {
            // TODO Auto-generated method stub
            return false;
        }

        public void migrate() throws CoreException {
            // TODO Auto-generated method stub
            
        }
	}

}
