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
		assertEquals(IRubyPartitions.RUBY_SINGLE_LINE_COMMENT, scanner
				.nextToken().getData());
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

	public void testDoubleQuotedString() {
		setDocument("\"double quoted string\"");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
	}

	public void testSingleQuotedString() {
		setDocument("'single quoted string'");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertTrue(scanner.nextToken().isEOF());
	}

	public void testCommand() {
		setDocument("`command`");
		assertEquals(IRubyPartitions.RUBY_COMMAND, scanner.nextToken()
				.getData());
		assertTrue(scanner.nextToken().isEOF());
	}

	public void testRegularExpression() {
		setDocument("/regex/");
		assertEquals(IRubyPartitions.RUBY_REGEX, scanner.nextToken().getData());
		assertTrue(scanner.nextToken().isEOF());
	}
	
	public void testRegularExpressionWithPercentSyntax() {
		setDocument("%r(regex)");
		assertEquals(IRubyPartitions.RUBY_REGEX, scanner.nextToken().getData());
		assertTrue(scanner.nextToken().isEOF());
		
		setDocument("%r!regex!");
		assertEquals(IRubyPartitions.RUBY_REGEX, scanner.nextToken().getData());
		assertTrue(scanner.nextToken().isEOF());
		
		setDocument("%r{regex}");
		assertEquals(IRubyPartitions.RUBY_REGEX, scanner.nextToken().getData());
		assertTrue(scanner.nextToken().isEOF());
	}
	
	public void testCommandWithPercentSyntax() {
		setDocument("%x(command)");
		assertEquals(IRubyPartitions.RUBY_COMMAND, scanner.nextToken().getData());
		assertTrue(scanner.nextToken().isEOF());
		
		setDocument("%x!command!");
		assertEquals(IRubyPartitions.RUBY_COMMAND, scanner.nextToken().getData());
		assertTrue(scanner.nextToken().isEOF());
		
		setDocument("%x{command}");
		assertEquals(IRubyPartitions.RUBY_COMMAND, scanner.nextToken().getData());
		assertTrue(scanner.nextToken().isEOF());
	}
	
	public void testStringWithPercentSyntax() {
		setDocument("%q(command) # Comment");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertCommentFollows();
		
		setDocument("%q!command! # Comment");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertCommentFollows();
		
		setDocument("%q{command} # Comment");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertCommentFollows();
		
		setDocument("%Q(command) # Comment");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertCommentFollows();
		
		setDocument("%Q!command! # Comment");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertCommentFollows();
		
		setDocument("%Q[command] # Comment");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertCommentFollows();
		
		setDocument("%/command/ # Comment");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertCommentFollows();
		
		setDocument("%!command! # Comment");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertCommentFollows();
		
		setDocument("%[command] # Comment");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertCommentFollows();
	}

	private void assertCommentFollows() {
		assertNull(scanner.nextToken().getData());
		assertEquals(IRubyPartitions.RUBY_SINGLE_LINE_COMMENT, scanner
				.nextToken().getData());
		assertTrue(scanner.nextToken().isEOF());
	}

	public void testRegularExpressionClosesProperly() {
		setDocument("/regex/ # Comment");
		assertEquals(IRubyPartitions.RUBY_REGEX, scanner.nextToken().getData());
		assertCommentFollows();
	}
	
	public void testRegularExpressionWithOptions() {
		setDocument("/regex/i");
		assertEquals(IRubyPartitions.RUBY_REGEX, scanner.nextToken().getData());
		assertTrue("Failed to gobble up option/flag as part of regular expression",scanner.nextToken().isEOF());
		
		setDocument("/regex/i # Comment");
		assertEquals(IRubyPartitions.RUBY_REGEX, scanner.nextToken().getData());
		assertNull(scanner.nextToken().getData());
		assertEquals(IRubyPartitions.RUBY_SINGLE_LINE_COMMENT, scanner
				.nextToken().getData());
	}
	
	public void testStringWithEscapedEndChar() {
		setDocument("%q(ain\\)# blah)");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertTrue("failed to skip escaped end character", scanner.nextToken().isEOF());
		
		setDocument("%(ain\\)# blah)");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertTrue("failed to skip escaped end character", scanner.nextToken().isEOF());
		
		setDocument("%Q(ain\\)# blah)");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertTrue("failed to skip escaped end character", scanner.nextToken().isEOF());
	}
	
	public void testExpressionSubstitution() {
		setDocument("%{There are #{count} monkeys}");
		assertEquals(IRubyPartitions.RUBY_STRING, scanner.nextToken().getData());
		assertTrue("failed to gobble up expression substiution", scanner.nextToken().isEOF());
	}

}
