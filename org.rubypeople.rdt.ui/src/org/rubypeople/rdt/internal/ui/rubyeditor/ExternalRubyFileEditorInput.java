package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.editors.text.ILocationProvider;

/**
 * @since 3.0
 */
public class ExternalRubyFileEditorInput implements IFileEditorInput, ILocationProvider {

	private File fFile;

	public ExternalRubyFileEditorInput(File file) {
		super();
		fFile= file;
	}
	/*
	 * @see org.eclipse.ui.IEditorInput#exists()
	 */
	public boolean exists() {
		return fFile.exists();
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getImageDescriptor()
	 */
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getName()
	 */
	public String getName() {
		return fFile.getName();
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getPersistable()
	 */
	public IPersistableElement getPersistable() {
		return null;
	}

	/*
	 * @see org.eclipse.ui.IEditorInput#getToolTipText()
	 */
	public String getToolTipText() {
		return fFile.getAbsolutePath();
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		if (ILocationProvider.class.equals(adapter))
			return this;
		return Platform.getAdapterManager().getAdapter(this, adapter);
	}

	/*
	 * @see org.eclipse.ui.editors.text.ILocationProvider#getPath(java.lang.Object)
	 */
	public IPath getPath(Object element) {
		if (element instanceof ExternalRubyFileEditorInput) {
			ExternalRubyFileEditorInput input= (ExternalRubyFileEditorInput) element;
			return new Path(input.fFile.getAbsolutePath());
		}
		return null;
	}
	
	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object o) {
		if (o == this)
			return true;
		
		if (o instanceof ExternalRubyFileEditorInput) {
			ExternalRubyFileEditorInput input = (ExternalRubyFileEditorInput) o;
			return fFile.equals(input.fFile);		
		}
		
		return false;
	}
	
	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return fFile.hashCode();
	}
	public IFile getFile() {
		// TODO Auto-generated method stub
		return null;
	}

	public IStorage getStorage() throws CoreException {
		// TODO Auto-generated method stub
		return null;
	}
	
	public File getFilesystemFile() {
		return fFile ;
	}

}