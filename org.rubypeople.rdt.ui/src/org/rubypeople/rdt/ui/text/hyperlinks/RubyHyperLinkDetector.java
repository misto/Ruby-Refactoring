package org.rubypeople.rdt.ui.text.hyperlinks;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.codeassist.SelectionEngine;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyWordFinder;
import org.rubypeople.rdt.ui.IWorkingCopyManager;

public class RubyHyperLinkDetector implements IHyperlinkDetector {

	private final IEditorInput fEditorInput;

	public class RubyHyperlink implements IHyperlink {

		private IRegion fRegion;
		private final IEditorInput fEditorInput;

		public RubyHyperlink(IEditorInput editorInput, IRegion region) {
			this.fEditorInput = editorInput;
			fRegion = region;
		}

		public IRegion getHyperlinkRegion() {
			return fRegion;
		}

		public String getHyperlinkText() {
			// TODO Auto-generated method stub
			return null;
		}

		public String getTypeLabel() {
			// TODO Auto-generated method stub
			return null;
		}

		public void open() {			
			try {
				SelectionEngine engine = new SelectionEngine();
				IWorkingCopyManager manager = RubyPlugin.getDefault().getWorkingCopyManager();
				IRubyScript script = manager.getWorkingCopy(fEditorInput);
				IRubyElement[] elements = engine.select(script, fRegion.getOffset(), fRegion.getOffset() + fRegion.getLength());
				// FIXME Check for first element which is an instanceof of IMember, don't just try to access the first element!
				ISourceRange sourceRange = ((IMember) elements[0]).getSourceRange();
				IFile file = null;
				if (fEditorInput instanceof IFileEditorInput) {
					IFileEditorInput fileInput = (IFileEditorInput) fEditorInput;
					file = fileInput.getFile();
				}
				openFileAndLocation(sourceRange, file);
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RubyModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
		private void openFileAndLocation(ISourceRange sourceRange, IFile file)
				throws PartInitException, CoreException {
			if (file == null) return;
			if (sourceRange == null) return;
			IEditorPart editorPart = IDE.openEditor(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getActivePage(), file, true);

			IMarker mark = file.createMarker(IMarker.TEXT);
			mark.setAttribute(IMarker.CHAR_START, sourceRange.getOffset());
			mark.setAttribute(IMarker.CHAR_END, sourceRange.getOffset()
					+ sourceRange.getLength());
			IDE.gotoMarker(editorPart, mark);
			mark.delete();
			IDE.gotoMarker(editorPart, mark);
		}

	}

	public RubyHyperLinkDetector(IEditorInput editorInput) {
		this.fEditorInput = editorInput;

	}

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		IRegion newRegion = region;
		newRegion = RubyWordFinder.findWord(textViewer.getDocument(), region
				.getOffset());
		return new IHyperlink[] { new RubyHyperlink(fEditorInput, newRegion) };
	}

}
