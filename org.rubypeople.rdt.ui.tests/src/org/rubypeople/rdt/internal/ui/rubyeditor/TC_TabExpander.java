package org.rubypeople.rdt.internal.ui.rubyeditor;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;

public class TC_TabExpander extends TestCase {

	private static final String TEST_TEXT = "012345678\n123456789\n";

	private static class TestDocumentCommand extends DocumentCommand {

		public TestDocumentCommand(int offset) {
			this.offset = offset;
		}
	}

	public void testSpacesForTab() {
		verifyTabExpansion(4, 0, 4);
		verifyTabExpansion(4, 1, 3);
		verifyTabExpansion(4, 2, 2);
		verifyTabExpansion(4, 3, 1);
		verifyTabExpansion(4, 4, 4);
		verifyTabExpansion(4, 5, 3);
		verifyTabExpansion(5, 4, 1);
	}

	public void testSubsequentLines() {
		verifyTabExpansion(4, 10, 4);
		verifyTabExpansion(4, 12, 2);
		verifyTabExpansion(4, 14, 4);
	}

	public void testSpacesOneSpacePerTab() {
		verifyTabExpansion(1, 0, 1);
		verifyTabExpansion(1, 1, 1);
		verifyTabExpansion(1, 2, 1);
		verifyTabExpansion(1, 3, 1);
		verifyTabExpansion(1, 4, 1);
	}

	public void testSpacesZeroSpacPerTab() {
		verifyTabExpansion(0, 0, 1);
		verifyTabExpansion(0, 1, 1);
		verifyTabExpansion(0, 2, 1);
		verifyTabExpansion(0, 3, 1);
		verifyTabExpansion(0, 4, 1);
	}

	private void verifyTabExpansion(int spacesPerTab, int currentOffset, int expectedCountOfTabs) {
		TabExpander expander = new TabExpander(spacesPerTab);
		TestDocumentCommand command = new TestDocumentCommand(currentOffset);
		command.text = "\t";
		Document document = new Document(TEST_TEXT);
		
		expander.expandTab(command, document);

		String message = "Offset = "+currentOffset + "; tabWidth = "+spacesPerTab;
		assertEquals(message, "     ".substring(0,expectedCountOfTabs), command.text);
		assertEquals("Full indent (" + spacesPerTab + ")", "     ".substring(0,spacesPerTab), expander.getFullIndent());
	}
	
	
}
