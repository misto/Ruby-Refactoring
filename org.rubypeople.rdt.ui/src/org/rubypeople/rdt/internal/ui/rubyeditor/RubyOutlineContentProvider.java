package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ExtendedTextEditor;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.ParseError;
import org.rubypeople.rdt.internal.core.parser.ParseException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.parser.ast.IRubyElement;
import org.rubypeople.rdt.internal.core.parser.ast.RubyScript;

public class RubyOutlineContentProvider implements ITreeContentProvider, IDocumentListener {

	protected Viewer viewer;
	private ExtendedTextEditor fTextEditor;

	/**
	 * @param textEditor
	 */
	public RubyOutlineContentProvider(ExtendedTextEditor textEditor) {
		this.fTextEditor = textEditor;
	}

	public Object[] getChildren(Object parentElement) {
		IRubyElement rubyElement = (IRubyElement) parentElement;
		return rubyElement.getElements();
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof IRubyElement) { return ((IRubyElement) element).hasElements(); }
		RubyPlugin.log("A non IRubyElement is being asked for children: " + element);
		return false;
	}

	public Object[] getElements(Object inputElement) {
		IRubyElement element = (IRubyElement) inputElement;
		return element.getElements();
	}

	/**
	 * @param script
	 * @throws CoreException
	 */
	private void createMarkers(RubyScript script, IDocument doc) throws CoreException {
		IEditorInput input = fTextEditor.getEditorInput();
		IResource resource = (IResource) ((IAdaptable) input).getAdapter(org.eclipse.core.resources.IResource.class);
		if (resource == null) {
			// happens if ruby file is external
			return ;
		}
		resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ONE);

		Set errors = script.getParseErrors();
		for (Iterator iter = errors.iterator(); iter.hasNext();) {
			ParseError pe = (ParseError) iter.next();
			Map attributes = new HashMap();
			MarkerUtilities.setMessage(attributes, pe.getError());
			MarkerUtilities.setLineNumber(attributes, pe.getLine());
			try {
				int offset = doc.getLineOffset(pe.getLine());
				MarkerUtilities.setCharStart(attributes, offset + pe.getStart());
				MarkerUtilities.setCharEnd(attributes, offset + pe.getEnd());
				attributes.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_WARNING));
				try {
					MarkerUtilities.createMarker(resource, attributes, IMarker.PROBLEM);
				} catch (CoreException x) {
					RubyPlugin.log(x);
				}
			} catch (BadLocationException e) {
				RubyPlugin.log(e);
			}

		}
	}

	public void dispose() {}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = viewer;
		RubyPlugin.log(	"Input changed, " + newInput.getClass().getName() );
		//if (oldInput != null) ((IDocument) oldInput).removeDocumentListener(this);
		//if (newInput != null) ((IDocument) newInput).addDocumentListener(this);
	}

	public void documentAboutToBeChanged(DocumentEvent event) {}

	public void documentChanged(DocumentEvent event) {
		viewer.refresh();
	}
}
