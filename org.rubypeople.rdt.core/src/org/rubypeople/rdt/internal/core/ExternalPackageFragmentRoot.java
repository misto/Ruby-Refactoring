package org.rubypeople.rdt.internal.core;

import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ISourceFolderRoot;

public class ExternalPackageFragmentRoot extends SourceFolderRoot implements
		ISourceFolderRoot {

	protected ExternalPackageFragmentRoot(IPath resource,
			RubyProject project) {
		super(null, project);
	}

	@Override
	public boolean isExternal() {
		return true;
	}
}
