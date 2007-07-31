package org.rubypeople.rdt.internal.ui.text.ruby;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IDocument;
import org.rubypeople.rdt.internal.ui.text.TestDocumentCommand;

public class TC_RubyAutoIndentStrategy extends TestCase {
	
	public void testInsertsIndentAndEndAfterClassDefinitionLine() throws Exception {
		DocumentCommand c = addNewline("class Chris");
		assertEquals(15, c.caretOffset);
		assertEquals(false, c.shiftsCaret);
		assertEquals("\r\n  \r\nend", c.text);
	}
	
	public void testInsertsIndentAndEndAfterMethodDefinitionLine() throws Exception {
		DocumentCommand c = addNewline("def bob");
		assertEquals(11, c.caretOffset);
		assertEquals(false, c.shiftsCaret);
		assertEquals("\r\n  \r\nend", c.text);
	}

	private DocumentCommand addNewline(String source) {
		RubyAutoIndentStrategy strategy = new RubyAutoIndentStrategy(null, null);
		DocumentCommand c = createDocumentCommand(source.length());
		IDocument d = new Document(source);
		strategy.customizeDocumentCommand(d, c);
		return c;
	}

	private DocumentCommand createDocumentCommand(int offset) {
		DocumentCommand c = new TestDocumentCommand();	
		c.text = "\r\n";
		c.length = 0;
		c.doit = true;
		c.caretOffset = -1;
		c.offset = offset;
		c.shiftsCaret = true;
		return c;
	}	

}
