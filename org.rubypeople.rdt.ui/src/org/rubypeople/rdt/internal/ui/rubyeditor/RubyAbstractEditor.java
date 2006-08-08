package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.IWidgetTokenKeeper;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.ListenerList;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.SourceViewerDecorationSupport;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.osgi.service.prefs.BackingStoreException;
import org.rubypeople.rdt.core.IImportContainer;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor.ITextConverter;
import org.rubypeople.rdt.internal.ui.text.ContentAssistPreference;
import org.rubypeople.rdt.internal.ui.text.IRubyPartitions;
import org.rubypeople.rdt.internal.ui.text.PreferencesAdapter;
import org.rubypeople.rdt.internal.ui.text.RubyPairMatcher;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.RubyUI;
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
    
	/** Preference key for matching brackets */
	protected final static String MATCHING_BRACKETS=  PreferenceConstants.EDITOR_MATCHING_BRACKETS;
	/** Preference key for matching brackets color */
	protected final static String MATCHING_BRACKETS_COLOR=  PreferenceConstants.EDITOR_MATCHING_BRACKETS_COLOR;


	protected final static char[] BRACKETS= { '{', '}', '(', ')', '[', ']' };

	/** The editor's bracket matcher */
	protected RubyPairMatcher fBracketMatcher= new RubyPairMatcher(BRACKETS);
    
	/**
	 * Creates and returns the preference store for this Ruby editor with the given input.
	 *
	 * @param input The editor input for which to create the preference store
	 * @return the preference store for this editor
	 *
	 * @since 0.9.0
	 */
	private IPreferenceStore createCombinedPreferenceStore(IEditorInput input) {
		List stores= new ArrayList(3);

		IRubyProject project= EditorUtility.getRubyProject(input);
		if (project != null) {
			stores.add(new EclipsePreferencesAdapter(new ProjectScope(project.getProject()), RubyCore.PLUGIN_ID));
		}

		stores.add(RubyPlugin.getDefault().getPreferenceStore());
		stores.add(new PreferencesAdapter(RubyCore.getPlugin().getPluginPreferences()));
		stores.add(EditorsUI.getPreferenceStore());

		return new ChainedPreferenceStore((IPreferenceStore[]) stores.toArray(new IPreferenceStore[stores.size()]));
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
    
	protected void configureSourceViewerDecorationSupport(SourceViewerDecorationSupport support) {
		support.setCharacterPairMatcher(fBracketMatcher);
		support.setMatchingCharacterPainterPreferenceKeys(MATCHING_BRACKETS, MATCHING_BRACKETS_COLOR);

		super.configureSourceViewerDecorationSupport(support);
	}
	
    protected ISourceViewer createSourceViewer(Composite parent, IVerticalRuler ruler, int styles) {
        fAnnotationAccess = createAnnotationAccess();
        fOverviewRuler = createOverviewRuler(getSharedColors());

        IPreferenceStore store= getPreferenceStore();
        ISourceViewer viewer = createRubySourceViewer(parent, ruler, getOverviewRuler(), isOverviewRulerVisible(), styles, store);
        // ensure decoration support has been created and configured.
        getSourceViewerDecorationSupport(viewer);
        return viewer;
    }

	protected ISourceViewer createRubySourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean isOverviewRulerVisible, int styles, IPreferenceStore store) {
		return new RubySourceViewer(parent, verticalRuler, overviewRuler,
				isOverviewRulerVisible, styles, store);
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
        IPreferenceStore store= createCombinedPreferenceStore(null);
		setPreferenceStore(store);
		RubyTextTools textTools= RubyPlugin.getDefault().getRubyTextTools();
        setSourceViewerConfiguration(new RubySourceViewerConfiguration(textTools.getColorManager(), store, this, IRubyPartitions.RUBY_PARTITIONING));
    }
    
	/*
	 * @see org.eclipse.ui.texteditor.AbstractTextEditor#setPreferenceStore(org.eclipse.jface.preference.IPreferenceStore)
	 * @since 0.9.0
	 */
	protected void setPreferenceStore(IPreferenceStore store) {
		super.setPreferenceStore(store);
		if (getSourceViewerConfiguration() instanceof RubySourceViewerConfiguration) {
			RubyTextTools textTools= RubyPlugin.getDefault().getRubyTextTools();
			setSourceViewerConfiguration(new RubySourceViewerConfiguration(textTools.getColorManager(), store, this, IRubyPartitions.RUBY_PARTITIONING));
		}
		if (getSourceViewer() instanceof RubySourceViewer)
			((RubySourceViewer)getSourceViewer()).setPreferenceStore(store);
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
    
    protected void handlePreferenceStoreChanged(PropertyChangeEvent event) {

		String property= event.getProperty();

		if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH.equals(property)) {
			/*
			 * Ignore tab setting since we rely on the formatter preferences.
			 * We do this outside the try-finally block to avoid that EDITOR_TAB_WIDTH
			 * is handled by the sub-class (AbstractDecoratedTextEditor).
			 */
			return;
		}

		try {

			AdaptedSourceViewer sourceViewer= (AdaptedSourceViewer) getSourceViewer();
			if (sourceViewer == null)
				return;

			((RubySourceViewerConfiguration)getSourceViewerConfiguration()).handlePropertyChangeEvent(event);
		
			IContentAssistant c= sourceViewer.getContentAssistant();
			if (c instanceof ContentAssistant)
				ContentAssistPreference.changeConfiguration((ContentAssistant) c, getPreferenceStore(), event);


		} finally {
			super.handlePreferenceStoreChanged(event);
		}
		
		if (AbstractDecoratedTextEditorPreferenceConstants.SHOW_RANGE_INDICATOR.equals(property)) {
			// superclass already installed the range indicator
			Object newValue= event.getNewValue();
			ISourceViewer viewer= getSourceViewer();
			if (newValue != null && viewer != null) {
				if (Boolean.valueOf(newValue.toString()).booleanValue()) {
					// adjust the highlightrange in order to get the magnet right after changing the selection
					Point selection= viewer.getSelectedRange();
					adjustHighlightRange(selection.x, selection.y);
				}
			}

		}
    }
    

	
	/**
	 * Returns the Ruby element wrapped by this editors input.
	 *
	 * @return the Ruby element wrapped by this editors input.
	 * @since 3.0
	 */
	protected IRubyElement getInputRubyElement() {
		IEditorInput editorInput= getEditorInput();
		if (editorInput == null)
			return null;
		return RubyUI.getEditorInputRubyElement(getEditorInput());
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
    	return ((RubySourceViewerConfiguration)getSourceViewerConfiguration()).affectsTextPresentation(event) || super.affectsTextPresentation(event);
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

    /**
	 * Adapts an options {@link IEclipsePreferences} to {@link org.eclipse.jface.preference.IPreferenceStore}.
	 * <p>
	 * This preference store is read-only i.e. write access
	 * throws an {@link java.lang.UnsupportedOperationException}.
	 * </p>
	 *
	 * @since 3.1
	 */
	private static class EclipsePreferencesAdapter implements IPreferenceStore {

		/**
		 * Preference change listener. Listens for events preferences
		 * fires a {@link org.eclipse.jface.util.PropertyChangeEvent}
		 * on this adapter with arguments from the received event.
		 */
		private class PreferenceChangeListener implements IEclipsePreferences.IPreferenceChangeListener {

			/**
			 * {@inheritDoc}
			 */
			public void preferenceChange(final IEclipsePreferences.PreferenceChangeEvent event) {
				if (Display.getCurrent() == null) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							firePropertyChangeEvent(event.getKey(), event.getOldValue(), event.getNewValue());
						}
					});
				} else {
					firePropertyChangeEvent(event.getKey(), event.getOldValue(), event.getNewValue());
				}
			}
		}

		// TODO When we move to Eclipse 3.2 change ListenerList to eclipse.core.runtime.ListenerList
		/** Listeners on on this adapter */
		private ListenerList fListeners= new ListenerList();

		/** Listener on the node */
		private IEclipsePreferences.IPreferenceChangeListener fListener= new PreferenceChangeListener();

		/** wrapped node */
		private final IScopeContext fContext;
		private final String fQualifier;

		/**
		 * Initialize with the node to wrap
		 *
		 * @param context The context to access
		 */
		public EclipsePreferencesAdapter(IScopeContext context, String qualifier) {
			fContext= context;
			fQualifier= qualifier;
		}

		private IEclipsePreferences getNode() {
			return fContext.getNode(fQualifier);
		}

		/**
		 * {@inheritDoc}
		 */
		public void addPropertyChangeListener(IPropertyChangeListener listener) {
			if (fListeners.size() == 0)
				getNode().addPreferenceChangeListener(fListener);
			fListeners.add(listener);
		}

		/**
		 * {@inheritDoc}
		 */
		public void removePropertyChangeListener(IPropertyChangeListener listener) {
			fListeners.remove(listener);
			if (fListeners.size() == 0) {
				getNode().removePreferenceChangeListener(fListener);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean contains(String name) {
			return getNode().get(name, null) != null;
		}

		/**
		 * {@inheritDoc}
		 */
		public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
			PropertyChangeEvent event= new PropertyChangeEvent(this, name, oldValue, newValue);
			Object[] listeners= fListeners.getListeners();
			for (int i= 0; i < listeners.length; i++)
				((IPropertyChangeListener) listeners[i]).propertyChange(event);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean getBoolean(String name) {
			return getNode().getBoolean(name, BOOLEAN_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean getDefaultBoolean(String name) {
			return BOOLEAN_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public double getDefaultDouble(String name) {
			return DOUBLE_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public float getDefaultFloat(String name) {
			return FLOAT_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public int getDefaultInt(String name) {
			return INT_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public long getDefaultLong(String name) {
			return LONG_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public String getDefaultString(String name) {
			return STRING_DEFAULT_DEFAULT;
		}

		/**
		 * {@inheritDoc}
		 */
		public double getDouble(String name) {
			return getNode().getDouble(name, DOUBLE_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public float getFloat(String name) {
			return getNode().getFloat(name, FLOAT_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public int getInt(String name) {
			return getNode().getInt(name, INT_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public long getLong(String name) {
			return getNode().getLong(name, LONG_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public String getString(String name) {
			return getNode().get(name, STRING_DEFAULT_DEFAULT);
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean isDefault(String name) {
			return false;
		}

		/**
		 * {@inheritDoc}
		 */
		public boolean needsSaving() {
			try {
				return getNode().keys().length > 0;
			} catch (BackingStoreException e) {
				// ignore
			}
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		public void putValue(String name, String value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, double value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, float value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, int value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, long value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, String defaultObject) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setDefault(String name, boolean value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setToDefault(String name) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, double value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, float value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, int value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, long value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, String value) {
			throw new UnsupportedOperationException();
		}

		/**
		 * {@inheritDoc}
		 */
		public void setValue(String name, boolean value) {
			throw new UnsupportedOperationException();
		}
	}
	
	class AdaptedSourceViewer extends RubySourceViewer  {

		private List fTextConverters;
		private boolean fIgnoreTextConverters= false;

		public AdaptedSourceViewer(Composite parent, IVerticalRuler verticalRuler, IOverviewRuler overviewRuler, boolean showAnnotationsOverview, int styles, IPreferenceStore store) {
			super(parent, verticalRuler, overviewRuler, showAnnotationsOverview, styles, store);
		}

		public IContentAssistant getContentAssistant() {
			return fContentAssistant;
		}
		
		public void addTextConverter(ITextConverter textConverter) {
			if (fTextConverters == null) {
				fTextConverters= new ArrayList(1);
				fTextConverters.add(textConverter);
			} else if (!fTextConverters.contains(textConverter))
				fTextConverters.add(textConverter);
		}

		public void removeTextConverter(ITextConverter textConverter) {
			if (fTextConverters != null) {
				fTextConverters.remove(textConverter);
				if (fTextConverters.size() == 0)
					fTextConverters= null;
			}
		}

		/*
		 * @see TextViewer#customizeDocumentCommand(DocumentCommand)
		 */
		protected void customizeDocumentCommand(DocumentCommand command) {
			super.customizeDocumentCommand(command);
			if (!fIgnoreTextConverters && fTextConverters != null) {
				for (Iterator e = fTextConverters.iterator(); e.hasNext();)
					((ITextConverter) e.next()).customizeDocumentCommand(getDocument(), command);
			}
		}
		
//		 http://dev.eclipse.org/bugs/show_bug.cgi?id=19270
		public void updateIndentationPrefixes() {
			SourceViewerConfiguration configuration= getSourceViewerConfiguration();
			String[] types= configuration.getConfiguredContentTypes(this);
			for (int i= 0; i < types.length; i++) {
				String[] prefixes= configuration.getIndentPrefixes(this, types[i]);
				if (prefixes != null && prefixes.length > 0)
					setIndentPrefixes(prefixes, types[i]);
			}
		}

		/*
		 * @see IWidgetTokenOwner#requestWidgetToken(IWidgetTokenKeeper)
		 */
		public boolean requestWidgetToken(IWidgetTokenKeeper requester) {
			if (PlatformUI.getWorkbench().getHelpSystem().isContextHelpDisplayed())
				return false;
			return super.requestWidgetToken(requester);
		}

		/*
		 * @see IWidgetTokenOwnerExtension#requestWidgetToken(IWidgetTokenKeeper, int)
		 * @since 3.0
		 */
		public boolean requestWidgetToken(IWidgetTokenKeeper requester, int priority) {
			if (PlatformUI.getWorkbench().getHelpSystem().isContextHelpDisplayed())
				return false;
			return super.requestWidgetToken(requester, priority);
		}

	}
}