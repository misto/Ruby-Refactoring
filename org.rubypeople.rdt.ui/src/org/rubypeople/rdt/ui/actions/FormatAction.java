package org.rubypeople.rdt.ui.actions;

import java.util.ResourceBundle;

import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.texteditor.TextEditorAction;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;

public class FormatAction extends TextEditorAction {

	public FormatAction(ResourceBundle bundle, String prefix, ITextEditor editor) {
		super(bundle, prefix, editor);
	}

	public void run() {
		IDocument doc = this.getTextEditor().getDocumentProvider().getDocument(this.getTextEditor().getEditorInput());
	 	
		           String allFormatted = RdtUiPlugin.getDefault().getCodeFormatter().formatString(doc.get());
		doc.set(allFormatted);
		/*try {
			ISelection selection = this.getTextEditor().getSelectionProvider().getSelection();
			if (selection instanceof TextSelection) {
				TextSelection textSelection = (TextSelection) selection;
				String formatted = RdtUiPlugin.getDefault().getCodeFormatter().formatString(textSelection.getText());
				doc.replace(textSelection.getOffset(), textSelection.getLength(), formatted);
			}
			
		} catch (BadLocationException e) {
			RdtUiPlugin.log(e);
		}*/

		super.run();
	}

}
