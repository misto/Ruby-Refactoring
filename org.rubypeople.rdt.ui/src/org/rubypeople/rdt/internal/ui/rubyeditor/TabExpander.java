package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;

public class TabExpander {

	private final int spacesPerTab;
	private String maxTabString;

	public TabExpander(int spacesPerTab) {
		this.spacesPerTab = spacesPerTab;
		StringBuffer buffer = new StringBuffer();
		for (int i=0; i<spacesPerTab; i++) 
			buffer.append(' ');
		
		maxTabString = buffer.toString(); 
	}

	public void expandTab(DocumentCommand command, IDocument document) {
		try {
			int offsetFromFileStart = command.offset;
			int lineNumber = document.getLineOfOffset(offsetFromFileStart);
			int offsetFromLineStart = offsetFromFileStart - document.getLineOffset(lineNumber);
			if (spacesPerTab > 0) {
				int count = spacesPerTab - offsetFromLineStart % spacesPerTab;
				command.text = maxTabString.substring(0, count);
			} else {
				command.text = " ";
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	public String getFullIndent() {
		return maxTabString;
	}

}
