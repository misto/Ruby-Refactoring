package org.rubypeople.rdt.internal.debug.ui.launcher;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.rubypeople.eclipse.shams.debug.core.ShamLaunchConfigurationType;
import org.rubypeople.eclipse.shams.debug.core.ShamLaunchManager;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;

public class TC_RubyApplicationShortcut extends TestCase {
	protected ILaunchManager launchManager;
	protected ShamRubyApplicationShortcut shortcut;
	protected ShamFile rubyFile, nonRubyFile;
	protected ShamLaunchConfiguration rubyConfiguration, nonRubyConfiguration;
	protected ISelection selection;

	public TC_RubyApplicationShortcut(String name) {
		super(name);
	}

	protected void setUp() {
		launchManager = new ShamLaunchManager();
		shortcut = new ShamRubyApplicationShortcut();

		rubyFile = new ShamFile("folderOne/myFile.rb");
		rubyConfiguration = new ShamLaunchConfiguration(rubyFile);

		nonRubyFile = new ShamFile("folderOne/myFile.java");
		nonRubyConfiguration = new ShamLaunchConfiguration(nonRubyFile);

		launchManager.addLaunch(new Launch(rubyConfiguration, ILaunchManager.RUN_MODE, null));
		launchManager.addLaunch(new Launch(nonRubyConfiguration, ILaunchManager.RUN_MODE, null));

		selection = new StructuredSelection(rubyFile);
	}

	public void testLaunchWithSelection() {
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		assertTrue("The configuration for myFile.rb should be launched.", rubyConfiguration.wasLaunched());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());
		
		selection = new StructuredSelection(nonRubyFile);
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		assertTrue("The configuration for rubyFile should not be launched when not selected.", !rubyConfiguration.wasLaunched());
		assertTrue("The configuration for nonRubyFile should not be launched when selected.", !nonRubyConfiguration.wasLaunched());
		assertTrue("The shortcut should log a message when asked to launch the wrong file type.", shortcut.didLog());
	}

	public void testLaunchWithSelectionMultipleConfigurationsExist() {
		ShamLaunchConfiguration anotherRubyConfigurationForSameFile = new ShamLaunchConfiguration(rubyFile);
		launchManager.addLaunch(new Launch(anotherRubyConfigurationForSameFile, ILaunchManager.RUN_MODE, null));

		shortcut.launch(selection, ILaunchManager.RUN_MODE);

		assertTrue("The configuration for myFile.rb should not be launched when multiple configurations exist.", !rubyConfiguration.wasLaunched());
		assertTrue("The second configuration for myFile.rb should not be launched when multiple configurations exist.", !anotherRubyConfigurationForSameFile.wasLaunched());
		assertTrue("The shortcut should log a message when asked to launch a file that has multiple configurations.", shortcut.didLog());
	}

	public void testLaunchWithSelectionMultipleSelections() {
		selection = new StructuredSelection(new Object[] { rubyFile, new ShamFile("folderOne/yourFile.rb") });
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		assertTrue("The configuration for myFile.rb should be launched when multiple files selected.", rubyConfiguration.wasLaunched());
	}

	public void testLaunchWithSelectionNoConfigurationExists() {
		selection = new StructuredSelection(new ShamFile("folderOne/nonExtantFile.rb"));
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		
		ShamLaunchConfigurationType configType = (ShamLaunchConfigurationType) launchManager.getLaunchConfigurationType(RubyLaunchConfigurationAttribute.RUBY_LAUNCH_CONFIGURATION_TYPE);
		assertTrue("The shortcut should create a new LaunchConfiguration.", configType.wasNewInstanceCreated());
	}
	
	public void testLaunchWithSelectionWhenFileNamesSameInDifferentDirectory() {
		IFile anotherRubyFileWithSameNameInDifferentFolder = new ShamFile("folderTwo/myFile.rb");
		ShamLaunchConfiguration anotherRubyConfiguration = new ShamLaunchConfiguration(anotherRubyFileWithSameNameInDifferentFolder);
		launchManager.addLaunch(new Launch(anotherRubyConfiguration, ILaunchManager.RUN_MODE, null));

		selection = new StructuredSelection(rubyFile);
		shortcut.launch(selection, ILaunchManager.RUN_MODE);

		assertTrue("The configuration for rubyFile should be launched.", rubyConfiguration.wasLaunched());
		assertTrue("The configuration for anotherRubyFile should not be launched.", !anotherRubyConfiguration.wasLaunched());
		assertTrue("The shortcut should not log a message.", !shortcut.didLog());
	}

	protected class ShamRubyApplicationShortcut extends RubyApplicationShortcut {
		protected boolean didLog;

		protected ILaunchManager getLaunchManager() {
			return launchManager;
		}

		protected void log(String message) {
			didLog = true;
		}

		protected void log(Throwable t) {
			didLog = true;
		}
		
		protected boolean didLog() {
			return didLog;
		}
	}

	protected class ShamLaunchConfiguration implements ILaunchConfiguration {
		protected IFile fileConfigurationIsFor;
		protected boolean launched;

		protected ShamLaunchConfiguration(IFile theFileConfigurationIsFor) {
			fileConfigurationIsFor = theFileConfigurationIsFor;
		}

		public boolean contentsEqual(ILaunchConfiguration configuration) {
			return false;
		}

		public ILaunchConfigurationWorkingCopy copy(String name) throws CoreException {
			return null;
		}

		public void delete() throws CoreException {
		}

		public boolean exists() {
			return false;
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
			return RubyLaunchConfigurationAttribute.FILE_NAME.equals(attributeName) ? fileConfigurationIsFor.getFullPath().toString() : defaultValue;
		}

		public IFile getFile() {
			return fileConfigurationIsFor;
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
			launched = true;
			return null;
		}

		public boolean supportsMode(String mode) throws CoreException {
			return false;
		}

		public Object getAdapter(Class adapter) {
			return null;
		}

		protected boolean wasLaunched() {
			boolean temp = launched;
			launched = false;
			return temp;
		}
	}
}
