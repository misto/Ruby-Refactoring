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
	private static boolean isDebug = false ;	

	public static void addRdocListener(RdocListener listener) {
		listeners.add(listener);
	}

	public static void removeRdocListener(RdocListener listener) {
		listeners.remove(listener);
	}
	
	public static void setDebugging(boolean isDebug) {
		RDocUtility.isDebug = isDebug ;
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
		
		private void log(String message) {
			if (RDocUtility.isDebug) {
				System.out.println(message) ;
			}
		}

		public final void invoke() {
			log("Generating RDoc for " + resource.getName());
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

			String riCmd = "\"" + rubyPath + "\" \"" + rdocPath.toString() + "\"";
			String call = riCmd + getArgString();
			try {
				log(call);
				final Process p = Runtime.getRuntime().exec(call);				
				handleOutput(p, call);
			} catch (IOException e) {
				RubyPlugin.log(e);
				log(e.getMessage());
				ErrorDialog.openError(RubyPlugin.getActiveWorkbenchShell(), "Error running Rdoc", e.getMessage(), new StatusInfo(StatusInfo.ERROR, e.getMessage()));
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
		private void handleOutput(Process p, String cmdLine) {
			BufferedReader reader = null;
			String lastLine = null ;
			try {
				reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				String line = null;
				while ((line = reader.readLine()) != null) {
					log(line);
					lastLine = line ;
				}
			} catch (Exception e) {
				log(e.getMessage());
			}
			try {
				p.waitFor();
				if (p.exitValue() != 0) {
					String message = "Ruby running rdoc exited abnormally. A possble reason is a wrong Rdoc path. Please check the path for rdoc in the preference page.";
					String additionalMessage = null ;
					if (lastLine != null) {
						additionalMessage = "The process was started with: "+ cmdLine + ". The last line of stderr of the ruby process: " + lastLine ;
					}
					else {
						additionalMessage = "The process was started with: "+ cmdLine + ". The process did not write any messages to stderr." ;
					}
					ErrorDialog.openError( RubyPlugin.getActiveWorkbenchShell(), "Ruby process exited with value " + p.exitValue(), message, new StatusInfo(StatusInfo.ERROR, additionalMessage));
				}
			} catch (InterruptedException e) {
				log("InterruptedException while waiting for rdoc process to finish." + e.getMessage()) ;
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
