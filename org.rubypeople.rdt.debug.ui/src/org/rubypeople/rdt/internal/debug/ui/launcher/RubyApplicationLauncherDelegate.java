package org.rubypeople.rdt.internal.debug.ui.launcher;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILauncher;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.core.model.ILauncherDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.debug.internal.core.RuntimeProcess;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.launching.RubyInterpreter;
import org.rubypeople.rdt.launching.RubyRuntime;
import sun.security.krb5.internal.i;

public class RubyApplicationLauncherDelegate implements ILauncherDelegate {

	public RubyApplicationLauncherDelegate() {}

	public String getLaunchMemento(Object element) {
		if (element instanceof IFile) {
			return ((IFile)element).getFullPath().toString();
		}
		return null;
	}

	public Object getLaunchObject(String memento) {
		return RdtDebugUiPlugin.getWorkspace().getRoot().getFile(new Path(memento));
	}

	public boolean launch(Object[] elements, String mode, ILauncher launcher) {
		IFile rubyFile = getRubyFile(elements);
		if (rubyFile == null)
			return false;

		ISourceLocator sourceLocator = new ISourceLocator() {
			public Object getSourceElement(IStackFrame stackFrame) {
				return null;
			};
		};

		String commandLine = renderCommandLine(rubyFile);
		if ("".equals(commandLine))
			return false;

		IProcess[] rubyProcess = new IProcess[1];
		try {
			Process nativeProcess = Runtime.getRuntime().exec(commandLine, null, rubyFile.getProject().getLocation().toFile());
			ILaunch processLaunch = new Launch(launcher, mode, rubyFile, sourceLocator, rubyProcess, null);
			rubyProcess[0] = new RuntimeProcess(processLaunch, nativeProcess, "Ruby Launch");
			registerLaunch(processLaunch);
		} catch (IOException e) {
			throw new RuntimeException("Unable to execute interpreter");
		}

		return true;
	}

	protected String renderCommandLine(IFile rubyFile) {
		RubyInterpreter interpreter = RubyRuntime.getDefault().getSelectedInterpreter();
		if (interpreter == null) {
			System.out.println("No interpreter specified.");
			return "";
		}

		IProject project = rubyFile.getProject();

		ExecutionArguments arguments = ExecutionArguments.getExecutionArguments(rubyFile);

		StringBuffer buffer = new StringBuffer();
		buffer.append(interpreter.getCommand());
		buffer.append(renderLoadPath(project));
		buffer.append(" ");
		buffer.append(arguments.interpreterArguments != null ? arguments.interpreterArguments : "");
		buffer.append(interpreter.endOfOptionsDelimeter);
		buffer.append(rubyFile.getName());
		buffer.append(" ");
		buffer.append(arguments.rubyFileArguments != null ? arguments.rubyFileArguments : "");

		return buffer.toString();
	}

	protected String renderLoadPath(IProject project) {
		RubyProject rubyProject = new RubyProject();
		rubyProject.setProject(project);

		StringBuffer loadPath = new StringBuffer();
		addToLoadPath(loadPath, project);

		Iterator referencedProjects = rubyProject.getReferencedProjects().iterator();
		while (referencedProjects.hasNext())
			addToLoadPath(loadPath, (IProject) referencedProjects.next());

		return loadPath.toString();
	}
	
	protected void addToLoadPath(StringBuffer loadPath, IProject project) {
		loadPath.append(" -I " + project.getLocation().toOSString());
	}

	protected void registerLaunch(final ILaunch launch) {
		getStandardDisplay().syncExec(new Runnable() {
			public void run() {
				DebugPlugin.getDefault().getLaunchManager().addLaunch(launch);
			}
		});
	}
	
	protected Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null)
			display= Display.getDefault();

		return display;		
	}
	
	protected IFile getRubyFile(Object[] selectedElements) {
		for (int i = 0; i < selectedElements.length; i++) {
			Object selected = selectedElements[i];
			if (selected instanceof IEditorInput) {
				IResource file = (IResource) ((IEditorInput)selected).getAdapter(IResource.class);
				selected = file;
			}

			if (selected instanceof IFile) {
				IFile fileSelected = (IFile) selected;
				if ("rb".equals(fileSelected.getFileExtension()))
					return fileSelected;
			}
		}

		return null;
	}
}