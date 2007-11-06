package ch.hsr.ch.refactoring.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.ruby.internal.ui.formatting.DefaultCodeFormatterConstants;
import org.eclipse.dltk.ruby.internal.ui.formatting.OldCodeFormatter;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.rubypeople.rdt.refactoring.RefactoringPlugin;
import org.rubypeople.rdt.refactoring.util.Constants;
import org.rubypeople.rdt.refactoring.util.RefactoringCodeFormatter;

public class DltkCodeFormatter implements RefactoringCodeFormatter {
	
	public String formatString(String code) {
		Document doc = new Document(code);
		try {
			//the first parameter "kind" seems to be unused
			getFormatter().format(0, code, 0, code.length(), 0, Character.toString(Constants.NL)).apply(doc);
			return doc.get();	
		} catch (MalformedTreeException e) {
			RefactoringPlugin.log(e);
		} catch (BadLocationException e) {
			RefactoringPlugin.log(e);
		}
		return ""; //$NON-NLS-1$
	}

	private OldCodeFormatter getFormatter()	{
		Map<String, String> options = new HashMap<String, String>();
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, "space"); //$NON-NLS-1$
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, "2"); //$NON-NLS-1$
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "2"); //$NON-NLS-1$
		return new OldCodeFormatter(options);
	}
}
