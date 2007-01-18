package org.rubypeople.rdt.internal.ui.rdocexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.rubypeople.rdt.internal.launching.RdtLaunchingMessages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyUIMessages;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.RubyRuntime;
import org.rubypeople.rdt.ui.PreferenceConstants;

/**
 * A utility class which will generate RDoc for a resource. If teh resource is
 * not a project, we will search up its hierarchy for a project. We then run
 * rdoc on the project (if possible) or the selected resource).
 * 
 * @author Chris
 */
public class RDocUtility {

	private static Set listeners = new HashSet();
	private static boolean isDebug = false;

	public static void addRdocListener(RdocListener listener) {
		listeners.add(listener);
	}

	public static void removeRdocListener(RdocListener listener) {
		listeners.remove(listener);
	}

	public static void setDebugging(boolean isDebug) {
		RDocUtility.isDebug = isDebug;
	}

	public static void generateDocumentation(IResource resource) {
		RubyInvoker invoker = new RubyInvoker(resource);
		invoker.invoke();
		notifyListeners();
	}

	private static class RubyInvoker {

		private IResource resource;

		private RubyInvoker(IResource resource) {
			this.resource = resource;
			findParentProject(resource);
		}

		private void findParentProject(IResource resource) {
			IResource project = resource;
			while (!isProject(project)) {
				project = project.getParent();
				if (project == null) break;
			}
			if (project != null && isProject(project)) this.resource = project;
		}

		private boolean isProject(IResource resource) {
			return (resource instanceof IProject);
		}

		private void log(String message) {
			if (RDocUtility.isDebug) {
				System.out.println(message);
			}
		}

		public final void invoke() {
			log("Generating RDoc for " + resource.getName());

			IVMInstall interpreter = RubyRuntime.getDefault().getSelectedInterpreter();
			if (interpreter == null) {			
				MessageDialog.openInformation(RubyPlugin.getActiveWorkbenchShell(), RdtLaunchingMessages.RdtLaunchingPlugin_noInterpreterSelectedTitle, RdtLaunchingMessages.RdtLaunchingPlugin_noInterpreterSelected);
				return ;
			}

			IPath rdocPath = new Path(RubyPlugin.getDefault().getPreferenceStore().getString(PreferenceConstants.RDOC_PATH));

			// check the rdoc path for existence. It might have been
			// unconfigured
			// and set to the default value or the file could have been removed
			File file = new File(rdocPath.toOSString());

			// If we can't find it ourselves then display an error to the user
			if (!file.exists() || !file.isFile()) {
				MessageDialog.openError(RubyPlugin.getActiveWorkbenchShell(), RubyUIMessages.getString("RDocPathErrorTitle"), RubyUIMessages.getString("RDocPathError")) ;
				return;
			}

			List args = new ArrayList();
			args.add(rdocPath.toString());
			args.add("-r");
			args.add(resource.getLocation().toOSString());
			try {
				final Process p = interpreter.exec(args, null);
				handleOutput(p, args);
			} catch (CoreException e) {
				RubyPlugin.log(e);
				log(e.getMessage());
				ErrorDialog.openError(RubyPlugin.getActiveWorkbenchShell(), RubyUIMessages.getString("ErrorRunningRdocTitle"), e.getMessage(), new StatusInfo(StatusInfo.ERROR, e.getMessage()));
			}
		}

		/**
		 * Get a handle on the process' error stream and pipe it to our Plugin
		 * to log. Then wait for the process to finish and log a message that
		 * we're done generating the Rdoc.
		 * 
		 * @param p
		 *            The Process.
		 */
		private void handleOutput(Process p, List cmdLine) {
			BufferedReader reader = null;
			String lastLine = null;
			try {
				reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					log(line);
					lastLine = line;
				}
			} catch (Exception e) {
				log(e.getMessage());
			}
			try {
				p.waitFor();
				if (p.exitValue() != 0) {
					String message = RubyUIMessages.getFormattedString("RDocExecutionError", Integer.toString(p.exitValue())) ; 
					String additionalMessage = null;
					if (lastLine != null) {
						additionalMessage = RubyUIMessages.getFormattedString("RDocExecutionErrorAdditionalMessageWithStderr", new String[] { cmdLine.toString(), lastLine });
					} else {
						additionalMessage = RubyUIMessages.getFormattedString("RDocExecutionErrorAdditionalMessage", cmdLine.toString());
					}
					String title = RubyUIMessages.getFormattedString("RDocExecutionErrorTitle", Integer.toString(p.exitValue())) ;
					ErrorDialog.openError(RubyPlugin.getActiveWorkbenchShell(), title, message, new StatusInfo(StatusInfo.ERROR, additionalMessage));
				}
			} catch (InterruptedException e) {
				log("InterruptedException while waiting for rdoc process to finish." + e.getMessage());
			}
			log("Done generating RDoc");
		}
	}

	/**
	 * Notify registered listeners that the rdoc has changed
	 * 
	 */
	public static void notifyListeners() {
		for (Iterator iter = listeners.iterator(); iter.hasNext();) {
			RdocListener listener = (RdocListener) iter.next();
			listener.rdocChanged();
		}
	}

}
