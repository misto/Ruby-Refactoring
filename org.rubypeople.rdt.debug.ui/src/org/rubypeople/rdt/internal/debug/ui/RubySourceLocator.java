package org.rubypeople.rdt.internal.debug.ui;

import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.IPersistableSourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.ui.ISourcePresentation;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.EditorWorkbook;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.internal.registry.EditorDescriptor;
import org.eclipse.ui.part.FileEditorInput;
import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.launching.InterpreterRunnerConfiguration;
import org.rubypeople.rdt.internal.launching.RdtLaunchingPlugin;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;



/**
 * @author Administrator
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class RubySourceLocator implements IPersistableSourceLocator, ISourcePresentation { // ISourcePresentation {
	private String absoluteWorkingDirectory;

	public RubySourceLocator() {

	}
	
	public String getAbsoluteWorkingDirectory() {
		return absoluteWorkingDirectory ;	
	}
	/**
	 * @see org.eclipse.debug.core.model.IPersistableSourceLocator#getMemento()
	 */
	public String getMemento() throws CoreException {
		return null;
	}

	/**
	 * @see org.eclipse.debug.core.model.IPersistableSourceLocator#initializeFromMemento(String)
	 */
	public void initializeFromMemento(String memento) throws CoreException {
	}

	/**
	 * @see org.eclipse.debug.core.model.IPersistableSourceLocator#initializeDefaults(ILaunchConfiguration)
	 */
	public void initializeDefaults(ILaunchConfiguration configuration) throws CoreException {
		this.absoluteWorkingDirectory = configuration.getAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY,"") ;
	}

	/**
	 * @see org.eclipse.debug.core.model.ISourceLocator#getSourceElement(IStackFrame)
	 */
	public Object getSourceElement(IStackFrame stackFrame) {
		return ((RubyStackFrame) stackFrame).getFileName();
	}

	/**
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorId(IEditorInput, Object)
	 */
	public String getEditorId(IEditorInput input, Object element) {
		return PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor((String) element).getId();
	}

	/**
	 * @see org.eclipse.debug.ui.ISourcePresentation#getEditorInput(Object)
	 */
	public IEditorInput getEditorInput(Object element) {

		String filename = (String) element;
		IFile eclipseFile = RdtDebugCorePlugin.getWorkspace().getRoot().getFileForLocation(new Path(filename));
		if (eclipseFile == null) {
			// FileSeparator does not matter here, "/" and "\" should both work
			filename = this.getAbsoluteWorkingDirectory() + "/" + filename;
			eclipseFile = RdtDebugCorePlugin.getWorkspace().getRoot().getFileForLocation(new Path(filename));
			if (eclipseFile == null) {
				RdtDebugCorePlugin.log(IStatus.INFO, "Could not find file \"" + element + "\".");
				return null;
			}
		}
		return new FileEditorInput(eclipseFile);

	}

}
