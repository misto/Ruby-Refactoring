package org.rubypeople.rdt.ui.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;

public class FormatAction extends TextEditorAction {

	public FormatAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	public void run() {
		IDocument doc = this.getTextEditor().getDocumentProvider().getDocument(this.getTextEditor().getEditorInput());

		try {
			ISelection selection = this.getTextEditor().getSelectionProvider().getSelection();
			if (selection instanceof TextSelection) {
				TextSelection textSelection = (TextSelection) selection;
				String text = textSelection.getText();
				if (text == null || text.length() == 0) {
					String allFormatted = RdtUiPlugin.getDefault().getCodeFormatter().formatString(doc.get());
					doc.set(allFormatted);
				} else {
					// format always complete lines, otherwise the indentation of the first line is lost
					int startPos = doc.getLineOffset(textSelection.getStartLine()) ;
					int endLine = textSelection.getEndLine() ;
					int endPos = doc.getLineOffset(endLine) + doc.getLineLength(endLine) ;

					String formatted = RdtUiPlugin.getDefault().getCodeFormatter().formatString(doc.get(startPos, endPos - startPos));
					doc.replace(startPos, endPos-startPos, formatted);
				}
			}

		} catch (BadLocationException e) {
			RdtUiPlugin.log(e);
		}

		super.run();
	}

}
