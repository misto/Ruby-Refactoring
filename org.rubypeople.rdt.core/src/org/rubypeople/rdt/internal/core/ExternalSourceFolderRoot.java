package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.Map;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.util.CharOperation;

public class ExternalSourceFolderRoot extends SourceFolderRoot implements
		ISourceFolderRoot {
	
	public final static ArrayList EMPTY_LIST = new ArrayList();

	protected final IPath folderPath;
	
	protected ExternalSourceFolderRoot(IPath resource,
			RubyProject project) {
		super(null, project);
		this.folderPath = resource;
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
	 * Returns true if this handle represents the same folder
	 * as the given handle.
	 *
	 * @see Object#equals
	 */
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o instanceof ExternalSourceFolderRoot) {
			ExternalSourceFolderRoot other= (ExternalSourceFolderRoot) o;
			return this.folderPath.equals(other.folderPath);
		}
		return false;
	}
	
	@Override
	protected boolean computeChildren(OpenableElementInfo info, Map newElements) throws RubyModelException {
		try {
			// the underlying resource may be a folder or a project (in the case that the project folder
			// is actually the source folder root)
			IWorkspaceRoot workspaceRoot = RubyCore.getWorkspace().getRoot();
			IContainer rootFolder = workspaceRoot.getContainerForLocation(folderPath.makeAbsolute());			
			if (rootFolder.getType() == IResource.FOLDER || rootFolder.getType() == IResource.PROJECT) {
				ArrayList vChildren = new ArrayList(5);
				computeFolderChildren(rootFolder, CharOperation.NO_STRINGS, vChildren);
				IRubyElement[] children = new IRubyElement[vChildren.size()];
				vChildren.toArray(children);
				info.setChildren(children);
			}
		} catch (RubyModelException e) {
			//problem resolving children; structure remains unknown
			info.setChildren(new IRubyElement[]{});
			throw e;
		}
		return true;
	}
}
