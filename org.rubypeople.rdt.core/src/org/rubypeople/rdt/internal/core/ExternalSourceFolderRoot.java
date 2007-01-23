package org.rubypeople.rdt.internal.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.CharOperation;
import org.rubypeople.rdt.internal.core.util.Util;

public class ExternalSourceFolderRoot extends SourceFolderRoot implements ISourceFolderRoot {

	public final static ArrayList EMPTY_LIST = new ArrayList();

	protected final IPath folderPath;

	protected ExternalSourceFolderRoot(IPath resource, RubyProject project) {
		super(null, project);
		this.folderPath = resource;
	}

	@Override
	protected boolean computeChildren(OpenableElementInfo info, Map newElements) throws RubyModelException {
		try {
			// the underlying resource may be a folder or a project (in the case
			// that the project folder
			// is actually the source folder root)
			Object target = RubyModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), this.folderPath, false);
			if (target instanceof File) {
				ArrayList vChildren = new ArrayList(5);
				computeFolderChildren((File) target, CharOperation.NO_STRINGS, vChildren);
				IRubyElement[] children = new IRubyElement[vChildren.size()];
				vChildren.toArray(children);
				info.setChildren(children);
			}
		} catch (RubyModelException e) {
			// problem resolving children; structure remains unknown
			info.setChildren(new IRubyElement[] {});
			throw e;
		}
		return true;
	}

	protected void computeFolderChildren(File folder, String[] pkgName, ArrayList vChildren) throws RubyModelException {
		ISourceFolder pkg = getSourceFolder(pkgName);
		vChildren.add(pkg);

		try {
			RubyProject rubyProject = (RubyProject) getRubyProject();
			RubyModelManager manager = RubyModelManager.getRubyModelManager();
			File[] members = folder.listFiles();

			for (int i = 0, max = members.length; i < max; i++) {
				File member = members[i];
				String memberName = member.getName();
				if (member.isDirectory()) {
					String[] newNames = Util.arrayConcat(pkgName, manager.intern(memberName));
					computeFolderChildren(member, newNames, vChildren);
					ISourceFolder child = getSourceFolder(newNames);
					vChildren.add(child);
				} else if (member.isFile()) {
					// do nothing
				}
			}
		} catch (IllegalArgumentException e) {
			throw new RubyModelException(e, IRubyModelStatusConstants.ELEMENT_DOES_NOT_EXIST); // could
																								// be
																								// thrown
																								// by
																								// ElementTree
																								// when
																								// path
																								// is
																								// not
																								// found
		} catch (CoreException e) {
			throw new RubyModelException(e);
		}
	}
	
	public SourceFolder getSourceFolder(String[] pkgName) {
		return new ExternalSourceFolder(this, pkgName);
	}

	@Override
	public IPath getPath() {
		return folderPath;
	}

	@Override
	public boolean isExternal() {
		return true;
	}

	public int hashCode() {
		return this.folderPath.hashCode();
	}

	@Override
	public boolean isReadOnly() {
		return true;
	}

	/**
	 * Returns true if this handle represents the same folder as the given
	 * handle.
	 * 
	 * @see Object#equals
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof ExternalSourceFolderRoot) {
			ExternalSourceFolderRoot other = (ExternalSourceFolderRoot) o;
			return this.folderPath.equals(other.folderPath);
		}
		return false;
	}

	/**
	 * @see IRubyElement
	 */
	public IResource getUnderlyingResource() throws RubyModelException {
		if (isExternal()) {
			if (!exists())
				throw newNotPresentException();
			return null;
		}
		return super.getUnderlyingResource();
	}

	/**
	 * Returns a new element info for this element.
	 */
	protected Object createElementInfo() {
		return new ExternalSourceFolderRootInfo();
	}

	public IResource getResource() {
		if (this.resource == null) {
			this.resource = RubyModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), this.folderPath, false);
		}
		if (this.resource instanceof IResource) {
			return super.getResource();
		}
		return null;
	}

	protected boolean resourceExists() {
		if (this.isExternal()) {
			return RubyModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), this.getPath(), // don't
																									// make
																									// the
																									// path
																									// relative
																									// as
																									// this
																									// is
																									// an
																									// external
																									// archive
					true) != null;
		}
		return super.resourceExists();
	}
}
