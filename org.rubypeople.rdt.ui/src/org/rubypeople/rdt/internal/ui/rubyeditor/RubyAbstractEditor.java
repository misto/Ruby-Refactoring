package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.DocumentModelChangeEvent;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.IDocumentModelListener;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.RubyContentOutlinePage;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.RubyCore;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.RubyModel;
import org.rubypeople.rdt.internal.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;
import org.rubypeople.rdt.ui.IWorkingCopyManager;

public class RubyAbstractEditor extends TextEditor {

	protected RubyContentOutlinePage outlinePage;
	protected RubyTextTools textTools;
	private IDocumentModelListener fListener;
	private RubyCore fCore;
	private RubyModel model;
	private ISourceReference reference;

	private IPreferenceStore createCombinedPreferenceStore() {
		IPreferenceStore rdtStore = RubyPlugin.getDefault().getPreferenceStore();
		IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
		return new ChainedPreferenceStore(new IPreferenceStore[] { rdtStore, generalTextStore});
	}

	protected void initializeEditor() {
		super.initializeEditor();
		setPreferenceStore(this.createCombinedPreferenceStore());

		textTools = RubyPlugin.getDefault().getTextTools();
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
				if (event.getModel() == getRubyModel()) {
					getSite().getShell().getDisplay().asyncExec(new Runnable() {

						public void run() {
							try {
								createMarkers(event.getModel().getScript());
							} catch (CoreException e) {
								RubyPlugin.log(e);
							}
						}
					});
				}
			}
		};
	}

	public Object getAdapter(Class required) {
		if (IContentOutlinePage.class.equals(required)) return createRubyOutlinePage();

		return super.getAdapter(required);
	}

	protected RubyContentOutlinePage createRubyOutlinePage() {
		outlinePage = new RubyContentOutlinePage(getSourceViewer().getDocument(), this);
		setOutlinePageInput(outlinePage, getEditorInput());
		outlinePage.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				handleOutlinePageSelection(event);
			}
		});
		return outlinePage;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.editors.text.TextEditor#doSetInput(org.eclipse.ui.IEditorInput)
	 */
	protected void doSetInput(IEditorInput input) throws CoreException {
		super.doSetInput(input);
		setOutlinePageInput(outlinePage, input);
	}

	protected void setOutlinePageInput(RubyContentOutlinePage page, IEditorInput input) {
		if (page != null) {
			IWorkingCopyManager manager = RubyPlugin.getDefault().getWorkingCopyManager();
			page.setInput(manager.getWorkingCopy(input));
		}
	}

	protected void handleOutlinePageSelection(SelectionChangedEvent event) {
		StructuredSelection selection = (StructuredSelection) event.getSelection();
		Iterator iter = ((IStructuredSelection) selection).iterator();
		while (iter.hasNext()) {
			Object o = iter.next();
			if (o instanceof ISourceReference) {
				reference = (ISourceReference) o;
				break;
			}
		}
		// FIXME Uncomment so we bring editor to top
		if (!isActivePart() && RubyPlugin.getActivePage() != null)
		  RubyPlugin.getActivePage().bringToTop(this);

		// setSelection(reference, !isActivePart());
		setSelection(reference, true);
	}
	
	protected boolean isActivePart() {
		IWorkbenchPart part= getActivePart();
		return part != null && part.equals(this);
	}
	
	private IWorkbenchPart getActivePart() {
		IWorkbenchWindow window= getSite().getWorkbenchWindow();
		IPartService service= window.getPartService();
		IWorkbenchPart part= service.getActivePart();
		return part;
	}

	protected void setSelection(ISourceReference reference, boolean moveCursor) {
		if (getSelectionProvider() == null) return;

		ISelection selection = getSelectionProvider().getSelection();
		if (selection instanceof TextSelection) {
			TextSelection textSelection = (TextSelection) selection;
			// PR 39995: [navigation] Forward history cleared after going back
			// in navigation history:
			// mark only in navigation history if the cursor is being moved
			// (which it isn't if
			// this is called from a PostSelectionEvent that should only update
			// the magnet)
			if (moveCursor && (textSelection.getOffset() != 0 || textSelection.getLength() != 0)) markInNavigationHistory();
		}

		if (reference != null) {

			StyledText textWidget = null;

			ISourceViewer sourceViewer = getSourceViewer();
			if (sourceViewer != null) textWidget = sourceViewer.getTextWidget();

			if (textWidget == null) return;

			try {
				ISourceRange range = null;
				// if (reference instanceof ILocalVariable) {
				// IJavaElement je= ((ILocalVariable)reference).getParent();
				// if (je instanceof ISourceReference)
				// range= ((ISourceReference)je).getSourceRange();
				// } else
				// range= reference.getSourceRange();
				//				
				// if (range == null)
				// return;
				//				
				// int offset= range.getOffset();
				// int length= range.getLength();
				//				
				// if (offset < 0 || length < 0)
				// return;
				//				
				// setHighlightRange(offset, length, moveCursor);

				if (!moveCursor) return;

				int offset = -1;
				int length = -1;

				if (reference instanceof IMember) {
					range = ((IMember) reference).getNameRange();
					if (range != null) {
						offset = range.getOffset();
						length = range.getLength();
					}
					// } else if (reference instanceof ILocalVariable) {
					// range= ((ILocalVariable)reference).getNameRange();
					// if (range != null) {
					// offset= range.getOffset();
					// length= range.getLength();
					// }
				} else if (reference instanceof IImportDeclaration) {
					String name = ((IImportDeclaration) reference).getElementName();
					if (name != null && name.length() > 0) {
						String content = reference.getSource();
						if (content != null) {
							offset = range.getOffset() + content.indexOf(name);
							length = name.length();
						}
					}
				}

				if (offset > -1 && length > 0) {

					try {
						textWidget.setRedraw(false);
						sourceViewer.revealRange(offset, length);
						sourceViewer.setSelectedRange(offset, length);
					} finally {
						textWidget.setRedraw(true);
					}

					markInNavigationHistory();
				}

			} catch (RubyModelException x) {} catch (IllegalArgumentException x) {}

		} else if (moveCursor) {
			resetHighlightRange();
			markInNavigationHistory();
		}
	}

	protected boolean affectsTextPresentation(PropertyChangeEvent event) {
		return textTools.affectsTextPresentation(event);
	}

	/**
	 * @param script
	 * @throws CoreException
	 */
	private void createMarkers(IRubyScript script) throws CoreException {
	// FIXME Create Markers on the file
	// IEditorInput input = getEditorInput();
	// if (input == null) {
	// // can happen at workbench shutdown
	// return;
	// }
	// IResource resource = (IResource) ((IAdaptable)
	// input).getAdapter(org.eclipse.core.resources.IResource.class);
	// if (resource == null) {
	// // happens if ruby file is external
	// return;
	// }
	// resource.deleteMarkers(IMarker.PROBLEM, true, IResource.DEPTH_ONE);
	// if
	// (!RdtUiPlugin.getDefault().getPreferenceStore().getBoolean(PreferenceConstants.CREATE_PARSER_ANNOTATIONS))
	// { return; }
	// IDocument doc = getDocumentProvider().getDocument(getEditorInput());
	// Set errors = script.getParseErrors();
	// for (Iterator iter = errors.iterator(); iter.hasNext();) {
	// ParseError pe = (ParseError) iter.next();
	// Map attributes = new HashMap();
	// MarkerUtilities.setMessage(attributes, pe.getError());
	// MarkerUtilities.setLineNumber(attributes, pe.getLine());
	// try {
	// int offset = doc.getLineOffset(pe.getLine());
	// MarkerUtilities.setCharStart(attributes, offset + pe.getStart());
	// MarkerUtilities.setCharEnd(attributes, offset + pe.getEnd());
	//
	// attributes.put(IMarker.SEVERITY, pe.getSeverity());
	// try {
	// MarkerUtilities.createMarker(resource, attributes, IMarker.PROBLEM);
	// } catch (CoreException x) {
	// RubyPlugin.log(x);
	// }
	// } catch (BadLocationException e) {
	// RubyPlugin.log(e);
	// }
	//
	// }
	}

	public RubyModel getRubyModel() {
		if (model == null) {
			model = new RubyModel();
		}
		return model;
	}

}