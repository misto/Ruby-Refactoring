/*
 * Created on Jan 13, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.Arrays;

import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.widgets.Composite;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.PreferenceConstants;

/* Copied from Ant StatusLineSourceViewer */
public class RubySourceViewer extends ProjectionViewer {

    private String tabReplaceString;
    private boolean isTabReplacing = false;
    private boolean fIgnoreTextConverters = false;

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
            convertTabs(command);
        }
        fIgnoreTextConverters = false;
    }

    void initializeTabReplace() {
        this.isTabReplacing = !RubyPlugin.getDefault().getPreferenceStore().getBoolean(
                PreferenceConstants.FORMAT_USE_TAB);
        if (this.isTabReplacing) {
            int length = RubyPlugin.getDefault().getPreferenceStore().getInt(
                    PreferenceConstants.FORMAT_INDENTATION);
            char[] spaces = new char[length];
            Arrays.fill(spaces, ' ');
            tabReplaceString = new String(spaces);
        }
    }

    protected void convertTabs(DocumentCommand command) {
        if (!isTabReplacing) { return; }
        if (command.text.equals("\t")) {
            command.text = this.tabReplaceString;
        }
    }
    
    public boolean isTabReplacing() {
        return isTabReplacing;
    }
    
    /**
     * 
     * @return Returns the replacement string for tab if isTabReplacing()
     */
    public String getTabReplaceString() {
        return tabReplaceString;
    }
    
}
