package org.rubypeople.rdt.internal.debug.ui.tests.launcher;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.debug.ui.RubySourceLocator;
import org.rubypeople.rdt.internal.debug.ui.launcher.RubyApplicationShortcut;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;
import org.rubypeople.rdt.internal.launching.RubyRuntime;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.IRubyConstants;

public class TC_RubyApplicationShortcut extends TestCase {

	protected ShamRubyApplicationShortcut shortcut;
	protected IFile rubyFile, nonRubyFile;
	private static String SHAM_LAUNCH_CONFIG_TYPE = "org.rubypeople.rdt.debug.ui.tests.launching.LaunchConfigurationTypeSham";

	public TC_RubyApplicationShortcut(String name) {
		super(name);
	}

	protected IProject getOrCreateProject(String pName) throws CoreException {
		IProject p = RdtDebugUiPlugin.getWorkspace().getRoot().getProject(pName);
		if (!p.exists()) {
			p.create(null);
			p.open(null);
		}
		return p;
	}

	protected IFile getOrCreateFile(String pName) throws CoreException {
		IFile f = RdtDebugUiPlugin.getWorkspace().getRoot().getFile(new Path(pName));
		if (!f.exists()) {
			f.create(new ByteArrayInputStream(new byte[0]), true, null);
		}
		return f;
	}

	protected IFolder getOrCreateDir(String pName) throws CoreException {
		IFolder f = RdtDebugUiPlugin.getWorkspace().getRoot().getFolder(new Path(pName));
		if (!f.exists()) {
			f.create(true, true, null);
		}
		return f;
	}

	protected void createLaunchConfiguration(String pName, IFile pFile) throws Exception {
		ILaunchConfigurationType configType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(SHAM_LAUNCH_CONFIG_TYPE);
		ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, pName);
		wc.setAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, pFile.getProject().getName());
		wc.setAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, pFile.getProjectRelativePath().toString());
		wc.setAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, "");
		wc.setAttribute(RubyLaunchConfigurationAttribute.SELECTED_INTERPRETER, RubyRuntime.getDefault().getSelectedInterpreter().getName());
		wc.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, "org.rubypeople.rdt.debug.ui.rubySourceLocator");
		wc.doSave();
	}

	protected ILaunchConfiguration[] getLaunchConfigurations() throws CoreException {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		return launchManager.getLaunchConfigurations(launchManager.getLaunchConfigurationType(SHAM_LAUNCH_CONFIG_TYPE));
	}

	protected void setUp() throws CoreException {
		shortcut = new ShamRubyApplicationShortcut();

		this.getOrCreateProject("/project1");
		this.getOrCreateDir("/project1/folderOne");
		nonRubyFile = this.getOrCreateFile("/project1/folderOne/myFile.java");
		rubyFile = this.getOrCreateFile("/project1/folderOne/myFile.rb");

		ILaunchConfiguration[] configs = this.getLaunchConfigurations();
		for (int i = 0; i < configs.length; i++) {
			configs[i].delete();
		}
		Assert.assertEquals("All configurations deleted.", 0, this.getLaunchConfigurations().length);

		ShamApplicationLaunchConfigurationDelegate.resetLaunches();
		RubyInterpreter interpreterOne = new RubyInterpreter("InterpreterOne", new Path("C:/RubyInstallRootOne"));
		RubyRuntime.getDefault().setInstalledInterpreters(Arrays.asList(new Object[] { interpreterOne}));

	}

	
	public void testNoInterpreterInstalled() throws Exception {
		RubyRuntime.getDefault().setInstalledInterpreters(Arrays.asList(new Object[] { }));
		ISelection selection = new StructuredSelection(rubyFile);
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		
		assertTrue("A dialog has been shown.", shortcut.didShowDialog);

	}
	
	public void testLaunchWithSelectedRubyFile() throws Exception {
		ISelection selection = new StructuredSelection(rubyFile);
		
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		
		assertEquals("A configuration has been created", 1, this.getLaunchConfigurations().length);
		assertEquals("A launch took place.", 1, ShamApplicationLaunchConfigurationDelegate.getLaunches());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());
	}

	public void testLaunchWithSelectedNonRubyFile() throws Exception {
		ISelection selection = new StructuredSelection(nonRubyFile);
		
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		
		assertEquals("There is no configuration.", 0, this.getLaunchConfigurations().length);
		assertEquals("There was no launch.", 0, ShamApplicationLaunchConfigurationDelegate.getLaunches());
		assertTrue("The shortcut should log a message when asked to launch the wrong file type.", shortcut.didLog());
	}

	public void testLaunchWithSelectionMultipleConfigurationsExist() throws Exception {
		this.createLaunchConfiguration("id1", rubyFile);
		this.createLaunchConfiguration("id2", rubyFile);
		ISelection selection = new StructuredSelection(rubyFile);

		shortcut.launch(selection, ILaunchManager.RUN_MODE);

		assertEquals("A new configuration for myFile.rb should not be created when one ore more configurations already exist.", 2, this.getLaunchConfigurations().length);
		assertEquals("The configuration for myFile.rb should not be launched when multiple configurations exist.", 0, ShamApplicationLaunchConfigurationDelegate.getLaunches());
		assertTrue("The shortcut should log a message when asked to launch a file that has multiple configurations.", shortcut.didLog());

	}

	public void testLaunchWithSelectionMultipleSelections() throws Exception {
		ISelection selection = new StructuredSelection(new Object[] { rubyFile, this.getOrCreateFile("project1/folderOne/yourFile.rb")});
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		ILaunchConfiguration[] configurations = this.getLaunchConfigurations();
		assertEquals("A configuration has been created", 1, configurations.length);
		assertEquals("A launch took place.", 1, ShamApplicationLaunchConfigurationDelegate.getLaunches());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());

		String launchedFileName = configurations[0].getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "");
		assertEquals("folderOne/myFile.rb", launchedFileName);
	}

	public void testLaunchWithSelectionTwice() throws Exception {
		ISelection selection = new StructuredSelection(rubyFile);
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		assertEquals("Only one configuration has been created", 1, this.getLaunchConfigurations().length);
		assertEquals("Two launches took place.", 2, ShamApplicationLaunchConfigurationDelegate.getLaunches());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());
	}

	public void testLaunchWithSelectionWhenFileNamesSameInDifferentDirectory() throws Exception {
		IFile anotherRubyFileWithSameNameInDifferentFolder = this.getOrCreateFile("project1/myFile.rb");
		ISelection selection = new StructuredSelection(rubyFile);

		shortcut.launch(selection, ILaunchManager.RUN_MODE);

		ILaunchConfiguration[] configurations = this.getLaunchConfigurations();
		assertEquals("A configuration has been created", 1, configurations.length);
		assertEquals("A launch took place.", 1, ShamApplicationLaunchConfigurationDelegate.getLaunches());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());

		String launchedFileName = configurations[0].getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "");
		assertEquals("folderOne/myFile.rb", launchedFileName);
	}

	public void testLaunchFromEditorWithRubyFile() throws Exception {

		IFile file = this.getOrCreateFile("/project1/test.rb");
		RubySourceLocator sourceLocator = new RubySourceLocator();
		String fullPath = RdtDebugUiPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator + file.getFullPath().toOSString();
		Object sourceElement = sourceLocator.getSourceElement(fullPath);
		IEditorInput input = sourceLocator.getEditorInput(sourceElement);
		IEditorPart rubyEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, IRubyConstants.EDITOR_ID);

		shortcut.launch(rubyEditor, ILaunchManager.RUN_MODE);

		assertEquals("A configuration has been created", 1, this.getLaunchConfigurations().length);
		assertEquals("A launch took place.", 1, ShamApplicationLaunchConfigurationDelegate.getLaunches());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());
	}

	public void testLaunchFromEditorWithTxtFile() throws Exception {

		IFile file = this.getOrCreateFile("/project1/test.txt");
		IEditorInput input = new FileEditorInput(file);
		IEditorPart txtEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, "org.eclipse.ui.DefaultTextEditor");

		shortcut.launch(txtEditor, ILaunchManager.RUN_MODE);

		assertEquals("No configuration has been created", 0, this.getLaunchConfigurations().length);
		assertEquals("No launch took place.", 0, ShamApplicationLaunchConfigurationDelegate.getLaunches());
		assertTrue("The shortcut should must have logged a message.", shortcut.didLog());
	}

	public void testLaunchFromExternalRubyFileEditor() throws Exception {
		File tmpFile = File.createTempFile("rubyfile.rb", null); //$NON-NLS-1$
		RubySourceLocator sourceLocator = new RubySourceLocator();
		Object sourceElement = sourceLocator.getSourceElement(tmpFile.getAbsolutePath());
		IEditorInput input = sourceLocator.getEditorInput(sourceElement);

		IEditorPart rubyExternalEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, IRubyConstants.EXTERNAL_FILES_EDITOR_ID);

		shortcut.launch(rubyExternalEditor, ILaunchManager.RUN_MODE);

		assertEquals("No configuration has been created", 0, this.getLaunchConfigurations().length);
		assertEquals("No launch took place.", 0, ShamApplicationLaunchConfigurationDelegate.getLaunches());
		assertTrue("The shortcut should must have logged a message.", shortcut.didLog());
	}

	protected class ShamRubyApplicationShortcut extends RubyApplicationShortcut {

		protected boolean didLog;
		protected boolean didShowDialog=false;

		protected void log(String message) {
			didLog = true;
		}

		protected void log(Throwable t) {
			didLog = true;
		}

		protected boolean didLog() {
			return didLog;
		}

		protected ILaunchConfigurationType getRubyLaunchConfigType() {
			return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(SHAM_LAUNCH_CONFIG_TYPE);
		}
		
		protected void showNoInterpreterDialog() {
			didShowDialog = true ;
		}
	}

}