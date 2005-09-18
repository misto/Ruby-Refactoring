/*
 * Created on Jan 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;

/* Copied from Ant StatusLineSourceViewer */
public class RubySourceViewer extends ProjectionViewer {

    private boolean isTabReplacing = false;
    private boolean fIgnoreTextConverters = false;
	private TabExpander tabExpander;

    public RubySourceViewer(Composite composite, IVerticalRuler verticalRuler,
            IOverviewRuler overviewRuler, boolean overviewRulerVisible, int styles) {
        super(composite, verticalRuler, overviewRuler, overviewRulerVisible, styles);
        initializeTabReplace();
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
            int length = RubyPlugin.getDefault().getPreferenceStore().getInt(
                    PreferenceConstants.FORMAT_INDENTATION);
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
