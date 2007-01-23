package org.rubypeople.rdt.internal.core;

import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ISourceFolderRoot;

public class ExternalSourceFolderRoot extends SourceFolderRoot implements
		ISourceFolderRoot {

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
}
