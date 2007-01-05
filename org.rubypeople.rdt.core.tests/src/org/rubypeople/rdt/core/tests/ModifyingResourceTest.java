package org.rubypeople.rdt.core.tests;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

public class ModifyingResourceTest extends AbstractRubyModelTest {
	protected IFile editFile(String path, String content) throws CoreException {
		IFile file = this.getFile(path);
		InputStream input = new ByteArrayInputStream(content.getBytes());
		file.setContents(input, IResource.FORCE, null);
		return file;
	}
}
