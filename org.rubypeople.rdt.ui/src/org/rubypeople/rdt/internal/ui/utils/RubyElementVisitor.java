package org.rubypeople.rdt.internal.ui.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
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
		case IResource.PROJECT:
			return true;

		case IResource.FOLDER:
			return true;

		case IResource.FILE:
			IFile fileResource = (IFile) resource;
			String extension = fileResource.getFileExtension();
			String name = fileResource.getName();
			if (name != null && name.equalsIgnoreCase("rakefile")) {
				rubyFiles.add(fileResource);
				return true;
			}
			if ("rb".equals(extension) || "rbw".equals(extension) || "cgi".equals(extension) || "gem".equals(extension) || "gemspec".equals(extension) || "rhtml".equals(extension)) {
				rubyFiles.add(fileResource);
				return true;
			}

		default:
			return false;
		}
	}

	public Object[] getCollectedRubyFiles() {
		return rubyFiles.toArray();
	}
}