package org.rubypeople.rdt.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;

/**
 * Represents an entire Ruby (<code>.rb</code> source file).
 */
public class RubyFile implements RubyElement {
	public static final String EXTENSION = "rb";

	protected IFile underlyingFile;

	public RubyFile(IFile theUnderlyingFile) {
		super();
		underlyingFile = theUnderlyingFile;
	}

	public IResource getUnderlyingResource() {
		return underlyingFile;
	}

}
