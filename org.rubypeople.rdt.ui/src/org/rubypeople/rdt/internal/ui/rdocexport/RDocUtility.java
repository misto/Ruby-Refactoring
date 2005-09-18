package org.rubypeople.rdt.internal.ui.rdocexport;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.rubypeople.rdt.internal.launching.RubyRuntime;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.dialogs.StatusInfo;
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

	public static void addRdocListener(RdocListener listener) {
		listeners.add(listener);
	}
	
	public static void removeRdocListener(RdocListener listener) {
		listeners.remove(listener);
	}

	public static void generateDocumentation(IResource resource) {
		RubyPlugin.log("Generating RDoc for " + resource.getName());
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
				if (project == null)
					break;
			}
			if (project != null && isProject(project))
				this.resource = project;
		}

		private boolean isProject(IResource resource) {
			return (resource instanceof IProject);
		}

		protected String getArgString() {
			return " -r " + resource.getLocation().toOSString();
		}

		public final void invoke() {
			IPath rubyPath = RubyRuntime.getDefault().getSelectedInterpreter()
					.getInstallLocation();
			IPath rdocPath = new Path(RubyPlugin.getDefault()
					.getPreferenceStore().getString(
							PreferenceConstants.RDOC_PATH));

			// check the rdoc path for existence. It might have been
			// unconfigured
			// and set to the default value or the file could have been removed
			File file = new File(rdocPath.toOSString());

			// If we can't find it ourselves then display an error to the user
			if (!file.exists() || !file.isFile()) {
				// TODO Extract the displayed strings to a properties file
				String message = "The input path for RDoc is blank or incorrect.";
				ErrorDialog.openError(RubyPlugin.getActiveWorkbenchShell(),
						"Rdoc location incorrect", message, new StatusInfo(
								StatusInfo.ERROR, message));
				return;
			}

			String riCmd = "\"" + rubyPath + "\" \"" + rdocPath.toString()
					+ "\"";
			String call = riCmd + getArgString();
			try {
				final Process p = Runtime.getRuntime().exec(call);
				RubyPlugin.log(call);
				handleOutput(p);
			} catch (IOException e) {
				RubyPlugin.log(e);
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
		private void handleOutput(Process p) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(p
						.getErrorStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					RubyPlugin.log(line);
				}
			} catch (Exception e) {
				RubyPlugin.log(e);
			}
			try {
				p.waitFor();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			RubyPlugin.log("Done generating RDoc");
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
