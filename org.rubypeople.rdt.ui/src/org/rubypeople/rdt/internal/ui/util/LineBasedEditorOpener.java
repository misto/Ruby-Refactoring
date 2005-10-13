package org.rubypeople.rdt.internal.ui.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ITextEditor;

public class LineBasedEditorOpener extends EditorOpener {

    private final int lineNumber;

    public LineBasedEditorOpener(String filename, int lineNumber) {
        super(filename);
        this.lineNumber = lineNumber;
    }

    protected void setEditorPosition(ITextEditor editor) {
        try {
            if (lineNumber > 0) {
                IDocument document= editor.getDocumentProvider().getDocument(editor.getEditorInput());
                int offset = document.getLineOffset(lineNumber-1);
                int length = document.getLineLength(lineNumber-1);
                editor.selectAndReveal(offset, length);
            }
        } catch (BadLocationException doNothing) {
        }
    }

}
