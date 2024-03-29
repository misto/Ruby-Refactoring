/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.rubypeople.rdt.refactoring;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The main plugin class to be used in the desktop.
 */
public class RefactoringPlugin extends AbstractUIPlugin {

	private static RefactoringPlugin plugin;

	private static RefactoringObjectFactory refactoringObjectFactory;
	
	public static final String PLUGIN_ID = "org.rubypeople.rdt.ui"; //$NON-NLS-1$

	/**
	 * The constructor.
	 */
	public RefactoringPlugin() {
		plugin = this;
	}

	/**
	 * This method is called upon plug-in activation
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/**
	 * This method is called when the plug-in is stopped
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance.
	 */
	public static RefactoringPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path.
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return AbstractUIPlugin.imageDescriptorFromPlugin("org.rubypeople.rdt.refactoring", path); //$NON-NLS-1$
	}
	
    public static void log(IStatus status) {
		getDefault().getLog().log(status);
		System.out.println(status.getMessage());
		if (status.getException() != null)
			status.getException().printStackTrace();
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR,	"internal Error Occurred", e));
	}

	public static void log(String string) {
		log(IStatus.OK, string);
	}

	public static void log(int severity, String message, Throwable e) {
		Status status = new Status(severity, PLUGIN_ID, IStatus.OK, message, e);
		log(status);
	}

	public static void log(int severity, String string) {
		log(new Status(severity, PLUGIN_ID, IStatus.OK, string, null));
	}

	public static RefactoringObjectFactory getRefactoringObjectFactory() {
		return refactoringObjectFactory;
	}

	public static void setRefactoringObjectFactory(RefactoringObjectFactory refactoringObjectFactory) {
		RefactoringPlugin.refactoringObjectFactory = refactoringObjectFactory;
	}
	
}
