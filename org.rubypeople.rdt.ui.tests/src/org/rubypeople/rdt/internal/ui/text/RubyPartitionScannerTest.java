package org.rubypeople.rdt.internal.ui.text;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
// FIXME Integrate this with already existing PartitionScanner tests
public class RubyPartitionScannerTest extends TestCase {

	private RubyPartitionScanner scanner;

	protected void setUp() throws Exception {
		super.setUp();
		scanner = new RubyPartitionScanner();
	}

	private void setDocument(String text) {
		IDocument document = new Document(text);
		scanner.setRange(document, 0, document.getLength());
	}

	public void testSingleLineComment() {
		setDocument("# comment");
//		assertEquals(IRubyPartitions.RUBY_SINGLE_LINE_COMMENT, scanner
//				.nextToken().getData());
		assertNull(scanner.nextToken().getData()); // comments are default partition now
	}

	public void testMultiLineComment() {
		setDocument("=begin\nSome comment text\n=end\n");
		assertEquals(IRubyPartitions.RUBY_MULTI_LINE_COMMENT, scanner
				.nextToken().getData());
	}

	public void testMultiLineCommentMustStartAtFirstColumn() {
		setDocument(" =begin\nSome comment text\n=end\n");
		assertFalse(IRubyPartitions.RUBY_MULTI_LINE_COMMENT.equals(scanner
				.nextToken().getData()));
		assertNull(scanner.nextToken().getData());
	}

	public void testPoundCharacterIsntAComment() {
		setDocument("?#");
		assertNull(scanner.nextToken().getData());
	}

}
