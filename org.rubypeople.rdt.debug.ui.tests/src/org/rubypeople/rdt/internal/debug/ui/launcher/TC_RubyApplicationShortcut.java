package org.rubypeople.rdt.internal.debug.ui.launcher;

import java.io.File;
import java.util.Arrays;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
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
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.tests.ModifyingResourceTest;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.debug.ui.RubySourceLocator;
import org.rubypeople.rdt.internal.launching.RubyInterpreter;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;
import org.rubypeople.rdt.internal.launching.RubyRuntime;
import org.rubypeople.rdt.launching.IInterpreter;
import org.rubypeople.rdt.ui.IRubyConstants;

public class TC_RubyApplicationShortcut extends ModifyingResourceTest {

	protected ShamRubyApplicationShortcut shortcut;
	protected IFile rubyFile, nonRubyFile;
	private static String SHAM_LAUNCH_CONFIG_TYPE = "org.rubypeople.rdt.debug.ui.tests.launching.LaunchConfigurationTypeSham";

	public TC_RubyApplicationShortcut(String name) {
		super(name);
	}

	protected ILaunchConfiguration createConfiguration(IFile pFile) {
		ILaunchConfiguration config = null;
		try {
			ILaunchConfigurationType configType = DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(SHAM_LAUNCH_CONFIG_TYPE);
			ILaunchConfigurationWorkingCopy wc = configType.newInstance(null, pFile.getName());
			wc.setAttribute(RubyLaunchConfigurationAttribute.PROJECT_NAME, pFile.getProject().getName());
			wc.setAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, pFile.getProjectRelativePath().toString());
			wc.setAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, "");
			wc.setAttribute(RubyLaunchConfigurationAttribute.SELECTED_INTERPRETER, RubyRuntime.getDefault().getSelectedInterpreter().getName());
			wc.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, "org.rubypeople.rdt.debug.ui.rubySourceLocator");
			config = wc.doSave();
		} catch (CoreException ce) {
			//ignore
		}
		return config;
	}

	protected ILaunchConfiguration[] getLaunchConfigurations() throws CoreException {
		ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
		return launchManager.getLaunchConfigurations(launchManager.getLaunchConfigurationType(SHAM_LAUNCH_CONFIG_TYPE));
	}

	protected void setUp() throws Exception {
		shortcut = new ShamRubyApplicationShortcut();

//		createProject("project1");
		createRubyProject("project1");
		createFolder("project1/folderOne");
		nonRubyFile = createFile("project1/folderOne/myFile.java", "");
		rubyFile = createFile("project1/folderOne/myFile.rb", "");

		ILaunchConfiguration[] configs = this.getLaunchConfigurations();
		for (int i = 0; i < configs.length; i++) {
			configs[i].delete();
		}
		Assert.assertEquals("All configurations deleted.", 0, this.getLaunchConfigurations().length);

		ShamApplicationLaunchConfigurationDelegate.resetLaunches();
		IInterpreter interpreterOne = new RubyInterpreter("InterpreterOne", new File("C:/RubyInstallRootOne"));
		RubyRuntime.getDefault().setInstalledInterpreters(Arrays.asList(new IInterpreter[] { interpreterOne}));
	   
		super.setUp();
	}
	
	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		deleteProject("project1");
	}

	
	public void testNoInterpreterInstalled() throws Exception {
		RubyRuntime.getDefault().setInstalledInterpreters(Arrays.asList(new IInterpreter[] { }));
		ISelection selection = new StructuredSelection(rubyFile);
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		
		assertTrue("A dialog has been shown.", shortcut.didShowDialog);
	}
	
	public void testLaunchWithSelectedRubyFile() throws Exception {
		ISelection selection = new StructuredSelection(rubyFile);
		
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		
		assertEquals("A configuration has been created", 1, this.getLaunchConfigurations().length);
		assertEquals("A launch took place.", 1, shortcut.launchCount());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());
	}

	public void testLaunchWithSelectedNonRubyFile() throws Exception {
		ISelection selection = new StructuredSelection(nonRubyFile);
		
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		
		assertEquals("There is no configuration.", 0, this.getLaunchConfigurations().length);
		assertEquals("There was no launch.", 0, shortcut.launchCount());
		assertTrue("The shortcut should log a message when asked to launch the wrong file type.", shortcut.didLog());
	}

	public void testLaunchWithSelectionMultipleConfigurationsExist() throws Exception {
		this.createConfiguration(rubyFile);
		this.createConfiguration(rubyFile);
		ISelection selection = new StructuredSelection(rubyFile);

		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		
		assertEquals("A new configuration for myFile.rb should not be created when one ore more configurations already exist.", 1, this.getLaunchConfigurations().length);
		assertEquals("The configuration for myFile.rb should have be launched.", 1, shortcut.launchCount());
	}

	public void testLaunchWithSelectionMultipleSelections() throws Exception {
		ISelection selection = new StructuredSelection(new Object[] { rubyFile, createFile("project1/folderOne/yourFile.rb", "")});
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		ILaunchConfiguration[] configurations = this.getLaunchConfigurations();
		assertEquals("A configuration has been created", 1, configurations.length);
		assertEquals("A launch took place.", 1, shortcut.launchCount());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());

		String launchedFileName = configurations[0].getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "");
		assertEquals("folderOne/myFile.rb", launchedFileName);
	}

	public void testLaunchWithSelectionTwice() throws Exception {
		ISelection selection = new StructuredSelection(rubyFile);
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		assertEquals("Only one configuration has been created", 1, this.getLaunchConfigurations().length);
		assertEquals("Two launches took place.", 2, shortcut.launchCount());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());
	}

	public void testLaunchWithSelectionWhenFileNamesSameInDifferentDirectory() throws Exception {
		IFile anotherRubyFileWithSameNameInDifferentFolder = createFile("project1/myFile.rb", "");
		ISelection selection = new StructuredSelection(rubyFile);

		shortcut.launch(selection, ILaunchManager.RUN_MODE);
		
		ILaunchConfiguration[] configurations = this.getLaunchConfigurations();
		assertEquals("A configuration has been created", 1, configurations.length);
		assertEquals("A launch took place.", 1, shortcut.launchCount());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());

		String launchedFileName = configurations[0].getAttribute(RubyLaunchConfigurationAttribute.FILE_NAME, "");
		assertEquals("folderOne/myFile.rb", launchedFileName);
	}

	public void testLaunchFromEditorWithRubyFile() throws Exception {
		IFile file = createFile("project1/test.rb", "");
		RubySourceLocator sourceLocator = new RubySourceLocator();
		String fullPath = RdtDebugUiPlugin.getWorkspace().getRoot().getLocation().toOSString() + File.separator + file.getFullPath().toOSString();
		Object sourceElement = sourceLocator.getSourceElement(fullPath);
		IEditorInput input = sourceLocator.getEditorInput(sourceElement);
		IEditorPart rubyEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, IRubyConstants.EDITOR_ID);

		shortcut.launch(rubyEditor, ILaunchManager.RUN_MODE);
		
		assertEquals("A configuration has been created", 1, this.getLaunchConfigurations().length);
		assertEquals("A launch took place.", 1, shortcut.launchCount());
		assertTrue("The shortcut should not log a message when asked to launch the correct file type.", !shortcut.didLog());
	}

	public void testLaunchFromEditorWithTxtFile() throws Exception {
		IFile file = createFile("project1/test.txt", "");
		IEditorInput input = new FileEditorInput(file);
		IEditorPart txtEditor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, "org.eclipse.ui.DefaultTextEditor");

		shortcut.launch(txtEditor, ILaunchManager.RUN_MODE);

		assertEquals("No configuration has been created", 0, this.getLaunchConfigurations().length);
		assertEquals("No launch took place.", 0, shortcut.launchCount());
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
		assertEquals("No launch took place.", 0, shortcut.launchCount());
		assertTrue("The shortcut should must have logged a message.", shortcut.didLog());
	}

	protected class ShamRubyApplicationShortcut extends RubyApplicationShortcut {

		protected boolean didLog;
		protected boolean didShowDialog=false;
        private boolean expectingException;
		private int launches = 0;

		protected void log(String message) {
			didLog = true;
		}

		public void expectException() {
		    expectingException = true;
        }

        protected void log(Throwable t) {
            if (!expectingException)
                throw new RuntimeException("Unexpected throwable: " + t.getMessage(), t);
			didLog = true;
		}

		protected boolean didLog() {
			return didLog;
		}

		protected void doLaunch(IRubyElement rubyElement, String mode) throws CoreException {
			ILaunchConfiguration config = findOrCreateLaunchConfiguration(rubyElement, mode);
			if (config != null) {
				launches++;
			}
		}
		
		public int launchCount() {
			return launches;
		}
		
		protected ILaunchConfigurationType getRubyLaunchConfigType() {
			return DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(SHAM_LAUNCH_CONFIG_TYPE);
		}
		
		protected void showNoInterpreterDialog() {
			didShowDialog = true ;
		}
	}

}