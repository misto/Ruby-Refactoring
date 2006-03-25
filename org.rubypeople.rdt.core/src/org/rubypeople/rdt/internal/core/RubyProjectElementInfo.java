/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ILoadpathEntry;

/**
 * Info for IRubyProject.
 * <p>
 * Note: <code>getChildren()</code> returns all of the
 * <code>IPackageFragmentRoots</code> specified on the classpath for the
 * project. This can include roots external to the project. See
 * <code>RubyProject#getAllPackageFragmentRoots()</code> and
 * <code>RubyProject#getPackageFragmentRoots()</code>. To get only the
 * <code>IPackageFragmentRoots</code> that are internal to the project, use
 * <code>RubyProject#getChildren()</code>.
 */

/* package */
class RubyProjectElementInfo extends OpenableElementInfo {

	/**
	 * A array with all the non-ruby resources contained by this PackageFragment
	 */
	private Object[] nonRubyResources;

	public Map pathToResolvedEntries;

	/**
	 * Create and initialize a new instance of the receiver
	 */
	public RubyProjectElementInfo() {
		this.nonRubyResources = null;
	}

	/**
	 * Compute the non-java resources contained in this java project.
	 */
	private Object[] computeNonRubyResources(RubyProject project) {
		// determine if src == project and/or if bin == project
		IPath projectPath = project.getProject().getFullPath();
		boolean srcIsProject = false;
		char[][] inclusionPatterns = null;
		char[][] exclusionPatterns = null;
		ILoadpathEntry[] classpath = project.getLoadpaths();
		for (int i = 0; i < classpath.length; i++) {
			ILoadpathEntry entry = classpath[i];
			if (projectPath.equals(entry.getPath())) {
				srcIsProject = true;
				inclusionPatterns = ((LoadpathEntry) entry).fullInclusionPatternChars();
				exclusionPatterns = ((LoadpathEntry) entry).fullExclusionPatternChars();
				break;
			}
		}

		Object[] resources = new IResource[5];
		int resourcesCounter = 0;
		try {
			IResource[] members = ((IContainer) project.getResource()).members();
			for (int i = 0, max = members.length; i < max; i++) {
				IResource res = members[i];
				switch (res.getType()) {
				case IResource.FILE:
					IPath resFullPath = res.getFullPath();
					String resName = res.getName();

					// ignore .java file if src == project
					if (srcIsProject && !org.rubypeople.rdt.internal.core.util.Util.isExcluded(res, inclusionPatterns, exclusionPatterns)) {
						break;
					}
					// else add non java resource
					if (resources.length == resourcesCounter) {
						// resize
						System.arraycopy(resources, 0, (resources = new IResource[resourcesCounter * 2]), 0, resourcesCounter);
					}
					resources[resourcesCounter++] = res;
					break;
				case IResource.FOLDER:
					resFullPath = res.getFullPath();

					// ignore non-excluded folders on the classpath or that
					// correspond to an output location
					if ((srcIsProject && !org.rubypeople.rdt.internal.core.util.Util.isExcluded(res, inclusionPatterns, exclusionPatterns)) || this.isLoadpathEntryOrOutputLocation(resFullPath, classpath)) {
						break;
					}
					// else add non java resource
					if (resources.length == resourcesCounter) {
						// resize
						System.arraycopy(resources, 0, (resources = new IResource[resourcesCounter * 2]), 0, resourcesCounter);
					}
					resources[resourcesCounter++] = res;
				}
			}
			if (resources.length != resourcesCounter) {
				System.arraycopy(resources, 0, (resources = new IResource[resourcesCounter]), 0, resourcesCounter);
			}
		} catch (CoreException e) {
			resources = NO_NON_RUBY_RESOURCES;
			resourcesCounter = 0;
		}
		return resources;
	}

	/**
	 * Returns an array of non-ruby resources contained in the receiver.
	 */
	Object[] getNonRubyResources(RubyProject project) {

		if (this.nonRubyResources == null) {
			this.nonRubyResources = computeNonRubyResources(project);
		}
		return this.nonRubyResources;
	}

	/*
	 * Returns whether the given path is a classpath entry
	 */
	private boolean isLoadpathEntryOrOutputLocation(IPath path, ILoadpathEntry[] resolvedLoadpath) {
		for (int i = 0, length = resolvedLoadpath.length; i < length; i++) {
			ILoadpathEntry entry = resolvedLoadpath[i];
			if (entry.getPath().equals(path)) { return true; }
		}
		return false;
	}

	/*
	 * Reset the package fragment roots and package fragment caches
	 */
	void resetCaches() {
		this.pathToResolvedEntries = null;
	}

	/**
	 * Set the fNonRubyResources to res value
	 */
	void setNonRubyResources(Object[] resources) {

		this.nonRubyResources = resources;
	}

}
