package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextFileDocumentProvider;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

public class RubyDocumentProvider extends TextFileDocumentProvider {

	/**
	 * Bundle of all required informations to allow working copy management.
	 */
	static protected class RubyScriptInfo extends FileInfo {

		public IRubyScript fCopy;
	}

	public RubyDocumentProvider() {
		IDocumentProvider provider = new TextFileDocumentProvider();
		setParentDocumentProvider(provider);
	}

	/*
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#createAnnotationModel(org.eclipse.core.resources.IFile)
	 */
	protected IAnnotationModel createAnnotationModel(IFile file) {
		return new RubyAnnotationModel(file);
	}

	/**
	 * Creates a compilation unit from the given file.
	 * 
	 * @param file
	 *            the file from which to create the compilation unit
	 */
	protected IRubyScript createRubyScript(IFile file) {
		Object element = RubyCore.create(file);
		if (element instanceof IRubyScript) return (IRubyScript) element;
		return null;
	}

	/*
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#createEmptyFileInfo()
	 */
	protected FileInfo createEmptyFileInfo() {
		return new RubyScriptInfo();
	}

	/*
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#createFileInfo(java.lang.Object)
	 */
	protected FileInfo createFileInfo(Object element) throws CoreException {
		if (!(element instanceof IFileEditorInput)) return null;

		IFileEditorInput input = (IFileEditorInput) element;
		IRubyScript original = createRubyScript(input.getFile());
		if (original == null) return null;

		FileInfo info = super.createFileInfo(element);
		if (!(info instanceof RubyScriptInfo)) return null;

		RubyScriptInfo cuInfo = (RubyScriptInfo) info;

		// FIXME Pass in a RubyScriptAnnotationModel as an IProblemRequestor
		// This is how the UI gets updated with the problems generated during parsing on a working copy
		original.becomeWorkingCopy(getProgressMonitor());
		cuInfo.fCopy = original;
		return cuInfo;
	}

	/*
	 * @see org.eclipse.ui.editors.text.TextFileDocumentProvider#disposeFileInfo(java.lang.Object,
	 *      org.eclipse.ui.editors.text.TextFileDocumentProvider.FileInfo)
	 */
	protected void disposeFileInfo(Object element, FileInfo info) {
		if (info instanceof RubyScriptInfo) {
			RubyScriptInfo cuInfo = (RubyScriptInfo) info;
			try {
				cuInfo.fCopy.discardWorkingCopy();
			} catch (RubyModelException x) {
				handleCoreException(x, x.getMessage());
			}
		}
		super.disposeFileInfo(element, info);
	}

	/**
	 * @param element
	 * @return
	 */
	public IRubyScript getWorkingCopy(Object element) {
		FileInfo fileInfo = getFileInfo(element);
		if (fileInfo instanceof RubyScriptInfo) {
			RubyScriptInfo info = (RubyScriptInfo) fileInfo;
			return info.fCopy;
		}
		return null;
	}

	/*
	 * @see org.eclipse.jdt.internal.ui.javaeditor.ICompilationUnitDocumentProvider#shutdown()
	 */
	public void shutdown() {
		Iterator e = getConnectedElementsIterator();
		while (e.hasNext())
			disconnect(e.next());
	}

}