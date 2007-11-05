package org.rubypeople.rdt.refactoring.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.text.edits.MalformedTreeException;
import org.rubypeople.rdt.core.formatter.CodeFormatter;
import org.rubypeople.rdt.core.formatter.DefaultCodeFormatterConstants;
import org.rubypeople.rdt.internal.formatter.OldCodeFormatter;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.refactoring.RefactoringPlugin;

public class RdtCodeFormatter implements RefactoringCodeFormatter {
	
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

	private CodeFormatter getFormatter()
	{
		CodeFormatter formatter = getRubyPluginFormatter();
		if(formatter != null) {
			return formatter;
		}
	
		Map<String, String> options = new HashMap<String, String>();
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_CHAR, "space"); //$NON-NLS-1$
		options.put(DefaultCodeFormatterConstants.FORMATTER_INDENTATION_SIZE, "2"); //$NON-NLS-1$
		options.put(DefaultCodeFormatterConstants.FORMATTER_TAB_SIZE, "2"); //$NON-NLS-1$
		return new OldCodeFormatter(options);
	}

	protected CodeFormatter getRubyPluginFormatter() {
		if (RubyPlugin.getDefault() != null) {
			return  RubyPlugin.getDefault().getCodeFormatter();
		}
		return null;
	}
}
