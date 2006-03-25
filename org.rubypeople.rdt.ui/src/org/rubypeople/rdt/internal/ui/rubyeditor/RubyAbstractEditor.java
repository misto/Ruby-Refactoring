package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.Iterator;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.rubypeople.rdt.core.IImportContainer;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.text.RubySourceViewerConfiguration;
import org.rubypeople.rdt.ui.text.RubyTextTools;

public abstract class RubyAbstractEditor extends TextEditor {

    protected RubyTextTools textTools;
    private ISourceReference reference;

    /** Outliner context menu Id */
    protected String fOutlinerContextMenuId;

    /** The selection changed listener */
    protected AbstractSelectionChangedListener fOutlineSelectionChangedListener = new OutlineSelectionChangedListener();
    private RubyOutlinePage fOutlinePage;

    private IPreferenceStore createCombinedPreferenceStore() {
        IPreferenceStore rdtStore = RubyPlugin.getDefault().getPreferenceStore();
        IPreferenceStore generalTextStore = EditorsUI.getPreferenceStore();
        return new ChainedPreferenceStore(new IPreferenceStore[] { rdtStore, generalTextStore});
    }

    /**
     * Informs the editor that its outliner has been closed.
     */
    public void outlinePageClosed() {
        if (fOutlinePage != null) {
            fOutlineSelectionChangedListener.uninstall(fOutlinePage);
            fOutlinePage = null;
            resetHighlightRange();
        }
    }

    /**
     * Sets the outliner's context menu ID.
     * 
     * @param menuId
     *            the menu ID
     */
    protected void setOutlinerContextMenuId(String menuId) {
        fOutlinerContextMenuId = menuId;
    }

    protected void initializeEditor() {
        super.initializeEditor();
        setPreferenceStore(this.createCombinedPreferenceStore());

        textTools = RubyPlugin.getDefault().getRubyTextTools();
        setSourceViewerConfiguration(new RubySourceViewerConfiguration(textTools, this));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.texteditor.ExtendedTextEditor#dispose()
     */
    public void dispose() {
        super.dispose();

    }

    public Object getAdapter(Class required) {
        if (IContentOutlinePage.class.equals(required)) {
            if (fOutlinePage == null) fOutlinePage = createRubyOutlinePage();
            return fOutlinePage;
        }

        return super.getAdapter(required);
    }

    protected RubyOutlinePage createRubyOutlinePage() {
        RubyOutlinePage outlinePage = new RubyOutlinePage(fOutlinerContextMenuId, this);
        fOutlineSelectionChangedListener.install(outlinePage);
        setOutlinePageInput(outlinePage, getEditorInput());
        return outlinePage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.ui.editors.text.TextEditor#doSetInput(org.eclipse.ui.IEditorInput)
     */
    protected void doSetInput(IEditorInput input) throws CoreException {
        super.doSetInput(input);
        setOutlinePageInput(fOutlinePage, input);
    }

    protected void setOutlinePageInput(RubyOutlinePage page, IEditorInput input) {
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
        if (!isActivePart() && RubyPlugin.getActivePage() != null)
            RubyPlugin.getActivePage().bringToTop(this);

        // setSelection(reference, !isActivePart());
        setSelection(reference, true);
    }

    protected boolean isActivePart() {
        IWorkbenchPart part = getActivePart();
        return part != null && part.equals(this);
    }

    private IWorkbenchPart getActivePart() {
        IWorkbenchWindow window = getSite().getWorkbenchWindow();
        IPartService service = window.getPartService();
        IWorkbenchPart part = service.getActivePart();
        return part;
    }

    public void setSelection(IRubyElement element) {

        if (element == null || element instanceof IRubyScript) {
            /*
             * If the element is an IRubyScript this unit is either the input of
             * this editor or not being displayed. In both cases, nothing should
             * happened. (http://dev.eclipse.org/bugs/show_bug.cgi?id=5128)
             */
            return;
        }

        IRubyElement corresponding = getCorrespondingElement(element);
        if (corresponding instanceof ISourceReference) {
            ISourceReference reference = (ISourceReference) corresponding;
            // set highlight range
            setSelection(reference, true);
            // set outliner selection
            if (fOutlinePage != null) {
                fOutlineSelectionChangedListener.uninstall(fOutlinePage);
                fOutlinePage.select(reference);
                fOutlineSelectionChangedListener.install(fOutlinePage);
            }
        }
    }

    protected IRubyElement getCorrespondingElement(IRubyElement element) {
        // TODO: With new working copy story: original == working copy.
        // Note that the previous code could result in a reconcile as side
        // effect. Should check if that
        // is still required.
        return element;
    }

    /**
     * Synchronizes the outliner selection with the given element position in
     * the editor.
     * 
     * @param element
     *            the java element to select
     * @param checkIfOutlinePageActive
     *            <code>true</code> if check for active outline page needs to
     *            be done
     */
    protected void synchronizeOutlinePage(ISourceReference element, boolean checkIfOutlinePageActive) {
        if (fOutlinePage != null && element != null
                && !(checkIfOutlinePageActive && isRubyOutlinePageActive())) {
            fOutlineSelectionChangedListener.uninstall(fOutlinePage);
            fOutlinePage.select(element);
            fOutlineSelectionChangedListener.install(fOutlinePage);
        }
    }

    private boolean isRubyOutlinePageActive() {
        IWorkbenchPart part = getActivePart();
        return part instanceof ContentOutline
                && ((ContentOutline) part).getCurrentPage() == fOutlinePage;
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
            if (moveCursor && (textSelection.getOffset() != 0 || textSelection.getLength() != 0))
                markInNavigationHistory();
        }

        if (reference != null) {

            StyledText textWidget = null;

            ISourceViewer sourceViewer = getSourceViewer();
            if (sourceViewer != null) textWidget = sourceViewer.getTextWidget();

            if (textWidget == null) return;

            try {
                ISourceRange range = null;
                // if (reference instanceof ILocalVariable) {
                // IRubyElement je = ((ILocalVariable) reference).getParent();
                // if (je instanceof ISourceReference) range =
                // ((ISourceReference) je).getSourceRange();
                // } else
                range = reference.getSourceRange();

                if (range == null) return;

                int offset = range.getOffset();
                int length = range.getLength();

                if (offset < 0 || length < 0) return;

                setHighlightRange(offset, length, moveCursor);

                if (!moveCursor) return;

                offset = -1;
                length = -1;

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

            } catch (RubyModelException x) {
            } catch (IllegalArgumentException x) {
            }

        } else if (moveCursor) {
            resetHighlightRange();
            markInNavigationHistory();
        }
    }

    protected boolean affectsTextPresentation(PropertyChangeEvent event) {
        return textTools.affectsTextPresentation(event);
    }

    protected void doSelectionChanged(SelectionChangedEvent event) {

        ISourceReference reference = null;

        ISelection selection = event.getSelection();
        Iterator iter = ((IStructuredSelection) selection).iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o instanceof ISourceReference) {
                reference = (ISourceReference) o;
                break;
            }
        }
        if (!isActivePart() && RubyPlugin.getActivePage() != null)
            RubyPlugin.getActivePage().bringToTop(this);

        setSelection(reference, !isActivePart());
    }

    class OutlineSelectionChangedListener extends AbstractSelectionChangedListener {

        public void selectionChanged(SelectionChangedEvent event) {
            doSelectionChanged(event);
        }
    }

    /**
     * Computes and returns the source reference that includes the caret and
     * serves as provider for the outline page selection and the editor range
     * indication.
     *
     * @return the computed source reference
     * @since 3.0
     */
    protected ISourceReference computeHighlightRangeSourceReference() {
        ISourceViewer sourceViewer= getSourceViewer();
        if (sourceViewer == null)
            return null;

        StyledText styledText= sourceViewer.getTextWidget();
        if (styledText == null)
            return null;

        int caret= 0;
        if (sourceViewer instanceof ITextViewerExtension5) {
            ITextViewerExtension5 extension= (ITextViewerExtension5)sourceViewer;
            caret= extension.widgetOffset2ModelOffset(styledText.getCaretOffset());
        } else {
            int offset= sourceViewer.getVisibleRegion().getOffset();
            caret= offset + styledText.getCaretOffset();
        }

        IRubyElement element= getElementAt(caret, false);

        if ( !(element instanceof ISourceReference))
            return null;

        if (element.getElementType() == IRubyElement.IMPORT_DECLARATION) {

            IImportDeclaration declaration= (IImportDeclaration) element;
            IImportContainer container= (IImportContainer) declaration.getParent();
            ISourceRange srcRange= null;

            try {
                srcRange= container.getSourceRange();
            } catch (RubyModelException e) {
            }

            if (srcRange != null && srcRange.getOffset() == caret)
                return container;
        }

        return (ISourceReference) element;
    }

    protected abstract IRubyElement getElementAt(int caret, boolean b);

    protected abstract IRubyElement getElementAt(int offset);

}