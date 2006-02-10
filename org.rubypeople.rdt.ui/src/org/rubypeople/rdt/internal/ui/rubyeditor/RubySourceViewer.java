/*
 * Created on Jan 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;
import org.rubypeople.rdt.core.formatter.Indents;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class RubySourceViewer extends ProjectionViewer implements IPropertyChangeListener {

    private boolean isTabReplacing = false;
    private boolean fIgnoreTextConverters = false;
	private TabExpander tabExpander;
    
    /**
     * This viewer's foreground color.
     * @since 0.8.0
     */
    private Color fForegroundColor;
    /**
     * The viewer's background color.
     * @since 0.8.0
     */
    private Color fBackgroundColor;
    /**
     * This viewer's selection foreground color.
     * @since 0.8.0
     */
    private Color fSelectionForegroundColor;
    /**
     * The viewer's selection background color.
     * @since 0.8.0
     */
    private Color fSelectionBackgroundColor;
    
    /**
     * The preference store.
     *
     * @since 0.8.0
     */
    private IPreferenceStore fPreferenceStore;
    
    /**
     * Is this source viewer configured?
     *
     * @since 0.8.0
     */
    private boolean fIsConfigured;

    public RubySourceViewer(Composite composite, IVerticalRuler verticalRuler,
            IOverviewRuler overviewRuler, boolean overviewRulerVisible, int styles, IPreferenceStore store) {
        super(composite, verticalRuler, overviewRuler, overviewRulerVisible, styles);
        setPreferenceStore(store);
        initializeTabReplace();
    }
    
    /**
     * Sets the preference store on this viewer.
     *
     * @param store the preference store
     *
     * @since 0.8.0
     */
    public void setPreferenceStore(IPreferenceStore store) {
        if (fIsConfigured && fPreferenceStore != null)
            fPreferenceStore.removePropertyChangeListener(this);

        fPreferenceStore= store;

        if (fIsConfigured && fPreferenceStore != null) {
            fPreferenceStore.addPropertyChangeListener(this);
            initializeViewerColors();
        }
    }
    
    protected void initializeViewerColors() {
        if (fPreferenceStore != null) {

            StyledText styledText= getTextWidget();

            // ----------- foreground color --------------------
            Color color= fPreferenceStore.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT)
            ? null
            : createColor(fPreferenceStore, AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, styledText.getDisplay());
            styledText.setForeground(color);

            if (fForegroundColor != null)
                fForegroundColor.dispose();

            fForegroundColor= color;

            // ---------- background color ----------------------
            color= fPreferenceStore.getBoolean(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT)
            ? null
            : createColor(fPreferenceStore, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, styledText.getDisplay());
            styledText.setBackground(color);

            if (fBackgroundColor != null)
                fBackgroundColor.dispose();

            fBackgroundColor= color;

            // ----------- selection foreground color --------------------
            color= fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_DEFAULT_COLOR)
                ? null
                : createColor(fPreferenceStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_COLOR, styledText.getDisplay());
            styledText.setSelectionForeground(color);

            if (fSelectionForegroundColor != null)
                fSelectionForegroundColor.dispose();

            fSelectionForegroundColor= color;

            // ---------- selection background color ----------------------
            color= fPreferenceStore.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_DEFAULT_COLOR)
                ? null
                : createColor(fPreferenceStore, AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_COLOR, styledText.getDisplay());
            styledText.setSelectionBackground(color);

            if (fSelectionBackgroundColor != null)
                fSelectionBackgroundColor.dispose();

            fSelectionBackgroundColor= color;
        }
    }
    
    /*
     * @see ISourceViewer#configure(SourceViewerConfiguration)
     */
    public void configure(SourceViewerConfiguration configuration) {

        /*
         * Prevent access to colors disposed in unconfigure(), see:
         *   https://bugs.eclipse.org/bugs/show_bug.cgi?id=53641
         *   https://bugs.eclipse.org/bugs/show_bug.cgi?id=86177
         */
        StyledText textWidget= getTextWidget();
        if (textWidget != null && !textWidget.isDisposed()) {
            Color foregroundColor= textWidget.getForeground();
            if (foregroundColor != null && foregroundColor.isDisposed())
                textWidget.setForeground(null);
            Color backgroundColor= textWidget.getBackground();
            if (backgroundColor != null && backgroundColor.isDisposed())
                textWidget.setBackground(null);
        }

        super.configure(configuration);

        if (fPreferenceStore != null) {
            fPreferenceStore.addPropertyChangeListener(this);
            initializeViewerColors();
        }

        fIsConfigured= true;
    }
    
    /*
     * @see org.eclipse.jface.text.source.ISourceViewerExtension2#unconfigure()
     * @since 0.8.0
     */
    public void unconfigure() {
        if (fForegroundColor != null) {
            fForegroundColor.dispose();
            fForegroundColor= null;
        }
        if (fBackgroundColor != null) {
            fBackgroundColor.dispose();
            fBackgroundColor= null;
        }

        if (fPreferenceStore != null)
            fPreferenceStore.removePropertyChangeListener(this);

        super.unconfigure();

        fIsConfigured= false;
    }
    
    /*
     * @see org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse.jface.util.PropertyChangeEvent)
     */
    public void propertyChange(PropertyChangeEvent event) {
        String property = event.getProperty();
        if (AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND.equals(property)
                || AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT.equals(property)
                || AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND.equals(property)
                || AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_COLOR.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_FOREGROUND_DEFAULT_COLOR.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_COLOR.equals(property)
                || AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SELECTION_BACKGROUND_DEFAULT_COLOR.equals(property))
        {
            initializeViewerColors();
        }
    }
    
    /**
     * Creates a color from the information stored in the given preference store.
     * Returns <code>null</code> if there is no such information available.
     *
     * @param store the store to read from
     * @param key the key used for the lookup in the preference store
     * @param display the display used create the color
     * @return the created color according to the specification in the preference store
     * @since 3.0
     */
    private Color createColor(IPreferenceStore store, String key, Display display) {

        RGB rgb= null;

        if (store.contains(key)) {

            if (store.isDefault(key))
                rgb= PreferenceConverter.getDefaultColor(store, key);
            else
                rgb= PreferenceConverter.getColor(store, key);

            if (rgb != null)
                return new Color(display, rgb);
        }

        return null;
    }

    public void doOperation(int operation) {
        if (getTextWidget() == null || !redraws()) { return; }

        switch (operation) {
        case UNDO:
            fIgnoreTextConverters = true;
            break;
        case REDO:
            fIgnoreTextConverters = true;
            break;
        }

        super.doOperation(operation);
    }

    protected void customizeDocumentCommand(DocumentCommand command) {
        super.customizeDocumentCommand(command);
        if (!fIgnoreTextConverters) {
            convertTabs(command, getDocument());
        }
        fIgnoreTextConverters = false;
    }

    void initializeTabReplace() {
        this.isTabReplacing = !RubyPlugin.getDefault().getPreferenceStore().getBoolean(
                PreferenceConstants.FORMAT_USE_TAB);
        if (this.isTabReplacing) {
            int length = Indents.getTabWidth(RubyCore.getOptions());
            tabExpander = new TabExpander(length);
        }
    }

    protected void convertTabs(DocumentCommand command, IDocument document) {
    	if (!isTabReplacing)
    		return;
    	
    	if (command.text.equals("\t")) 
    		tabExpander.expandTab(command, document);
    }
    
    public boolean isTabReplacing() {
        return isTabReplacing;
    }

	public String getIndentString() {
		return tabExpander.getFullIndent();
	}

}
