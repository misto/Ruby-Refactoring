/*
 * Created on Feb 19, 2005
 *
 */
package org.rubypeople.rdt.internal.ui.text;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.FastPartitioner;

/**
 * @author Chris
 * 
 */
public class TC_RubyPartitionScanner extends TestCase {
	
	private String getContentType(String content, int offset) {
		IDocument doc = new Document(content);
		FastPartitioner partitioner = new FastPartitioner(new RubyPartitionScanner(), RubyPartitionScanner.LEGAL_CONTENT_TYPES);
		partitioner.connect(doc);
		return partitioner.getContentType(offset);
	}
	
	public void testUnclosedInterpolationDoesntInfinitelyLoop() {
		String source = "%[\"#{\"]";
	    this.getContentType(source, 0);
	    assert(true);
	}	

	public void testRecognizeSpecialCase() {
		String source = "a,b=?#,'This is not a comment!'\n";
		
		assertEquals(IDocument.DEFAULT_CONTENT_TYPE, this.getContentType(source, 5));
		assertEquals(IDocument.DEFAULT_CONTENT_TYPE, this.getContentType(source, 6));
	}	
		
	public void testMultilineComment() {
		String source = "=begin\nComment\n=end";

		assertEquals(RubyPartitionScanner.RUBY_MULTI_LINE_COMMENT, this.getContentType(source, 0));
		assertEquals(RubyPartitionScanner.RUBY_MULTI_LINE_COMMENT, this.getContentType(source, 10));
		
		source = "=begin\n"+
				 "  for multiline comments, the =begin and =end must\n" + 
				 "  appear in the first column\n" +
				 "=end";
		assertEquals(RubyPartitionScanner.RUBY_MULTI_LINE_COMMENT, this.getContentType(source, 0));
		assertEquals(RubyPartitionScanner.RUBY_MULTI_LINE_COMMENT, this.getContentType(source, source.length() / 2));
		assertEquals(RubyPartitionScanner.RUBY_MULTI_LINE_COMMENT, this.getContentType(source, source.length() - 1));
	}
	
	public void testMultilineCommentNotOnFirstColumn() {
		String source = " =begin\nComment\n=end";

		assertEquals(IDocument.DEFAULT_CONTENT_TYPE, this.getContentType(source, 0));
		assertEquals(IDocument.DEFAULT_CONTENT_TYPE, this.getContentType(source, 1));
		assertEquals(IDocument.DEFAULT_CONTENT_TYPE, this.getContentType(source, 2));
		assertEquals(IDocument.DEFAULT_CONTENT_TYPE, this.getContentType(source, 10));
	}
	
}
