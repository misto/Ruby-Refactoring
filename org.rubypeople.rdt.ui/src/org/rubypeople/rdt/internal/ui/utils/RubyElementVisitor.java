package org.rubypeople.rdt.internal.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;

public class RubyElementVisitor implements IResourceVisitor {
	protected List rubyFiles = new ArrayList();

	public RubyElementVisitor() {
		super();
	}

	public boolean visit(IResource resource) throws CoreException {
		switch (resource.getType()) {
			case IResource.PROJECT :
				return true;

			case IResource.FOLDER :
				return true;

			case IResource.FILE :
				IFile fileResource = (IFile) resource;
				if ( "rb".equals(fileResource.getFileExtension()) ) {
					rubyFiles.add(fileResource);
					return true;
				}

			default :
				return false;
		}
	}
	
	public Object[] getCollectedRubyFiles() {
		return rubyFiles.toArray();
	}
}
