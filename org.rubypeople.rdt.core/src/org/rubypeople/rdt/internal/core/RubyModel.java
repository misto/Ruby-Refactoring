/*
 * Created on Jan 29, 2005
 *
 */
package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.IOpenable;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * @author Chris
 * 
 */
public class RubyModel extends Openable implements IRubyModel {

	protected RubyModel() {
		super(null);
	}

	/**
	 * Returns a new element info for this element.
	 */
	protected Object createElementInfo() {
		return new RubyModelInfo();
	}

	public boolean equals(Object o) {
		if (!(o instanceof RubyModel)) return false;
		return super.equals(o);
	}

	/**
	 * @see IRubyModel
	 */
	public Object[] getNonRubyResources() throws RubyModelException {
		return ((RubyModelInfo) getElementInfo()).getNonRubyResources();
	}

	/**
	 * Finds the given project in the list of the java model's children. Returns
	 * null if not found.
	 */
	public IRubyProject findRubyProject(IProject project) {
		try {
			IRubyProject[] projects = this.getRubyProjects();
			for (int i = 0, length = projects.length; i < length; i++) {
				IRubyProject rubyProject = projects[i];
				if (project.equals(rubyProject.getProject())) { return rubyProject; }
			}
		} catch (RubyModelException e) {
			// ruby model doesn't exist: cannot find any project
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyElement#getElementType()
	 */
	public int getElementType() {
		return IRubyElement.RUBY_MODEL;
	}

	/*
	 * @see IRubyElement
	 */
	public IPath getPath() {
		return Path.ROOT;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rubypeople.rdt.core.IRubyElement#getResource()
	 */
	public IResource getResource() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	/**
	 * @see IOpenable
	 */
	public IResource getUnderlyingResource() {
		return null;
	}

	/**
	 * Returns the workbench associated with this object.
	 */
	public IWorkspace getWorkspace() {
		return ResourcesPlugin.getWorkspace();
	}

	/**
	 * @see IRubyModel
	 */
	public IRubyProject[] getRubyProjects() throws RubyModelException {
		ArrayList list = getChildrenOfType(PROJECT);
		IRubyProject[] array = new IRubyProject[list.size()];
		list.toArray(array);
		return array;

	}

	protected boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) /*
																																	 * throws
																																	 * RubyModelException
																																	 */{

		// determine my children
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0, max = projects.length; i < max; i++) {
			IProject project = projects[i];
			if (RubyProject.hasRubyNature(project)) {
				info.addChild(getRubyProject(project));
			}
		}
		newElements.put(this, info);
		return true;
	}

	/**
	 * Returns the active Ruby project associated with the specified resource,
	 * or <code>null</code> if no Ruby project yet exists for the resource.
	 * 
	 * @exception IllegalArgumentException
	 *                if the given resource is not one of an IProject, IFolder,
	 *                or IFile.
	 */
	public IRubyProject getRubyProject(IResource resource) {
		switch (resource.getType()) {
		case IResource.FOLDER:
			return new RubyProject(((IFolder) resource).getProject(), this);
		case IResource.FILE:
			return new RubyProject(((IFile) resource).getProject(), this);
		case IResource.PROJECT:
			return new RubyProject((IProject) resource, this);
		default:
			throw new IllegalArgumentException(org.rubypeople.rdt.internal.core.util.Util.bind("element.invalidResourceForProject")); //$NON-NLS-1$
		}
	}

	/**
	 * @see IRubyModel
	 */
	public IRubyProject getRubyProject(String projectName) {
		return new RubyProject(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName), this);
	}

}
