package org.rubypeople.rdt.internal.debug.ui.launcher;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.rubypeople.eclipse.shams.debug.core.ShamLaunchManager;
import org.rubypeople.eclipse.shams.resources.ShamIFile;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;

public class TC_RubyApplicationShortcut extends TestCase {
	protected ILaunchManager launchManager;

	public TC_RubyApplicationShortcut(String name) {
		super(name);
	}

	public void testLaunchWithSelection() {
		ShamIFile rubyFile = new ShamIFile("myFile.rb");
		ShamLaunchConfiguration rubyConfiguration = new ShamLaunchConfiguration(rubyFile);

		ShamIFile nonRubyFile = new ShamIFile("myFile.java");
		ShamLaunchConfiguration javaConfiguration = new ShamLaunchConfiguration(nonRubyFile);

		launchManager = new ShamLaunchManager();
		launchManager.addLaunch(new Launch(rubyConfiguration, ILaunchManager.RUN_MODE, null));
		launchManager.addLaunch(new Launch(javaConfiguration, ILaunchManager.RUN_MODE, null));

		ShamRubyApplicationShortcut shortcut = new ShamRubyApplicationShortcut();
		IStructuredSelection selection = new StructuredSelection(new ShamIFile("myFile.rb"));
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		assertTrue("The configuration for myFile.rb should be launched.", rubyConfiguration.wasLaunched());
		
		selection = new StructuredSelection(new ShamIFile("myFile.java"));
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		assertTrue("The configuration for myFile.rb should not be launched.", !rubyConfiguration.wasLaunched());
		assertTrue("The configuration for myFile.java should not be launched.", !javaConfiguration.wasLaunched());
		assertTrue("The shortcut should log a message when asked to launch the wrong file type.", shortcut.didLog());
		
		selection = new StructuredSelection(new Object[] { new ShamIFile("myFile.rb"), new ShamIFile("yourFile.rb") });
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		assertTrue("The configuration for myFile.rb should be launched when multiple files selected.", rubyConfiguration.wasLaunched());
		
		ShamLaunchConfiguration anotherRubyConfigurationForSameFile = new ShamLaunchConfiguration(rubyFile);
		launchManager.addLaunch(new Launch(anotherRubyConfigurationForSameFile, ILaunchManager.RUN_MODE, null));
		selection = new StructuredSelection(new ShamIFile("myFile.rb"));
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		assertTrue("The configuration for myFile.rb should not be launched when multiple configurations exist.", !rubyConfiguration.wasLaunched());
		assertTrue("The second configuration for myFile.rb should not be launched when multiple configurations exist.", !anotherRubyConfigurationForSameFile.wasLaunched());
		assertTrue("The shortcut should log a message when asked to launch a file that has multiple configurations.", shortcut.didLog());
	}

	public void testLaunchWithSelectionNoConfigurationExists() {
		ShamRubyApplicationShortcut shortcut = new ShamRubyApplicationShortcut();
		IStructuredSelection selection = new StructuredSelection(new ShamIFile("myFile.rb"));
		
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		fail("what to do");
	}
	// test with two files of the same name

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
			return RubyLaunchConfigurationAttribute.FILE_NAME.equals(attributeName) ? fileConfigurationIsFor.getName() : defaultValue;
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
