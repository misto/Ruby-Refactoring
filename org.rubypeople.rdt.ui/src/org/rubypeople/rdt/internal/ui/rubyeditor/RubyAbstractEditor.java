package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.ExtendedTextEditor;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.eclipse.ui.texteditor.WorkbenchChainedTextFontFieldEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.ParseError;
import org.rubypeople.rdt.internal.core.parser.ast.RubyElement;
import org.rubypeople.rdt.internal.core.parser.ast.RubyScript;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.DocumentModelChangeEvent;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.IDocumentModelListener;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.RubyContentOutlinePage;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.RubyCore;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.RubyModel;
import org.rubypeople.rdt.internal.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class RubyAbstractEditor extends ExtendedTextEditor {

	protected RubyContentOutlinePage outlinePage;
	protected RubyTextTools textTools;
	private IDocumentModelListener fListener;
	private RubyCore fCore;
	private RubyModel model;

	protected void configurePreferenceStore() {
		IPreferenceStore prefs = RdtUiPlugin.getDefault().getPreferenceStore();
		setPreferenceStore(prefs);

		WorkbenchChainedTextFontFieldEditor.startPropagate(prefs, JFaceResources.TEXT_FONT);
	}

	protected void initializeEditor() {
		configurePreferenceStore();

		textTools = RdtUiPlugin.getDefault().getTextTools();
		setSourceViewerConfiguration(new RubySourceViewerConfiguration(textTools, this));
		
		if (fListener == null) {
			fListener = createRubyModelChangeListener();
		}
		fCore = RubyCore.getDefault();
		fCore.addDocumentModelListener(fListener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.ExtendedTextEditor#dispose()
	 */
	public void dispose() {
		super.dispose();
		// Remove the listener so we don't try and create markers on a closed
		// document
		fCore.removeDocumentModelListener(fListener);
	}

	/**
	 * @return
	 */
	private IDocumentModelListener createRubyModelChangeListener() {
		return new IDocumentModelListener() {

			public void documentModelChanged(final DocumentModelChangeEvent event) {
				/*if (model == null) {
					try {
						RubyDocumentProvider provider = (RubyDocumentProvider) getDocumentProvider();
						model = RubyCore.getDefault().getRubyModel(getEditorInput()) ;
						IDocument document = getDocumentProvider().getDocument(getEditorInput());
						model.setScript(RubyParser.parse(document.get()));
					} catch (ParseException e) {
						RubyPlugin.log(e);
					}
				}
				*/
				if (event.getModel() == getRubyModel()) {
					getSite().getShell().getDisplay().asyncExec(new Runnable() {

						public void run() {
							try {
								createMarkers(event.getModel().getScript());
							} catch (CoreException e) {
								RdtUiPlugin.log(e);
							}
						}
					});
				}
			}
		};
	}

	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) return createRubyOutlinePage();

		return super.getAdapter(adapter);
	}

	protected Object createRubyOutlinePage() {
		outlinePage = new RubyContentOutlinePage(getSourceViewer().getDocument(), this);
		outlinePage.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				handleOutlinePageSelection(event);
			}
		});
		return outlinePage;
	}

	protected void handleOutlinePageSelection(SelectionChangedEvent event) {
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		RubyElement element = (RubyElement) selection.getFirstElement();
		if (element == null) return;
		try {
			int offset = getSourceViewer().getDocument().getLineOffset(element.getStart().getLineNumber());
			selectAndReveal(offset + element.getStart().getOffset(), element.getName().length());
		} catch (BadLocationException e) {
			RubyPlugin.log(e);
		}
	}

	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return textTools.affectsTextPresentation(event);
	}

	/**
	 * @param script
	 * @throws CoreException
	 */
	private void createMarkers(RubyScript script) throws CoreException {
		IEditorInput input = getEditorInput();
		IResource resource = (IResource) ((IAdaptable) input).getAdapter(org.eclipse.core.resources.IResource.class);
		if (resource == null) {
		// happens if ruby file is external
		return; }
		resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ONE);
		if (!RdtUiPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CREATE_PARSER_ANNOTATIONS)) {
			return ;
		}
		IDocument doc = getDocumentProvider().getDocument(getEditorInput());
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

				attributes.put(IMarker.SEVERITY, pe.getSeverity());
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
	
	public RubyModel getRubyModel() {
		if (model == null) {
			model = new RubyModel() ;
		}
		return model ;
	}

}