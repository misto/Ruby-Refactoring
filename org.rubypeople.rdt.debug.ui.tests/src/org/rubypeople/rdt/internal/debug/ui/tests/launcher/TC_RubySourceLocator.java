package org.rubypeople.rdt.internal.debug.ui.tests.launcher;

import java.io.ByteArrayInputStream;
import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.internal.resources.Project;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.debug.ui.RubySourceLocator;

public class TC_RubySourceLocator extends TestCase {

	public TC_RubySourceLocator(String name) {
		super(name);
	}

	protected void setUp() {
	}

	public void testWorkspaceInternalFile() throws Exception {

		Workspace workspace = (Workspace) RdtDebugUiPlugin.getWorkspace() ;
		// Create a project called 'SourceLocatorTest'
		Project p = new TestProject("/SourceLocatorTest", workspace) ; //$NON-NLS-1$
		
		p.create(null) ;
		p.open(null) ;
		IPath filePath = new Path("/SourceLocatorTest/test.rb") ; //$NON-NLS-1$
		IFile file =RdtDebugUiPlugin.getWorkspace().getRoot().getFile(filePath) ;
		file.create(new ByteArrayInputStream(new byte[0]) ,true, null) ;
		
		// using slashes for the workspace internal path is platform independent
		String fullPath = workspace.getRoot().getLocation().toOSString() + File.separator + "SourceLocatorTest/test.rb" ; //$NON-NLS-1$

		RubySourceLocator sourceLocator = new RubySourceLocator() ;
		RubyStackFrame rubyStackFrame = new RubyStackFrame(null,fullPath, 5, 1) ;
		Object sourceElement = sourceLocator.getSourceElement(rubyStackFrame) ;
		IEditorInput input = sourceLocator.getEditorInput(sourceElement) ;
		assertNotNull(input) ;
		assertTrue(input.exists()) ;
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, sourceLocator.getEditorId(input, sourceElement)) ;
		
	}
	
	public void testWorkspaceExternalFile() throws Exception {
		
		// external File
		File tmpFile = File.createTempFile("rubyfile", null) ; //$NON-NLS-1$
		RubyStackFrame rubyStackFrame = new RubyStackFrame(null,tmpFile.getAbsolutePath(), 5, 1) ;
		
		RubySourceLocator sourceLocator = new RubySourceLocator() ;
		Object sourceElement = sourceLocator.getSourceElement(rubyStackFrame) ;
		IEditorInput input = sourceLocator.getEditorInput(sourceElement) ;
		assertNotNull(input) ;
		assertTrue(input.exists()) ;
		
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, sourceLocator.getEditorId(input, sourceElement)) ;
	}
	
	public void testNotExistingFile() throws Exception {
		RubyStackFrame rubyStackFrame = new RubyStackFrame(null,"/tmp/nonexistingtestfile", 5, 1) ; //$NON-NLS-1$
		
		RubySourceLocator sourceLocator = new RubySourceLocator() ;
		Object sourceElement = sourceLocator.getSourceElement(rubyStackFrame) ;
		IEditorInput input = sourceLocator.getEditorInput(sourceElement) ;
		assertNull(input) ;
		
	}


	
	public class TestProject extends Project {
	  public TestProject(String aName, Workspace aWorkspace) {
	  	super(new Path(aName), aWorkspace) ;
	  }
	}
}
