/*
 * Created on Feb 19, 2005
 *
 */
package org.rubypeople.rdt.internal.ui.text;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.DefaultPartitioner;

/**
 * @author Chris
 * 
 */
public class TC_RubyPartitionScanner extends TestCase {

	public void testPartitioningOfSingleLineComment() {
		String source = "# This is a comment\n";
		IDocument doc = new Document(source);
		
		DefaultPartitioner partitioner = new DefaultPartitioner(new RubyPartitionScanner(), RubyPartitionScanner.LEGAL_CONTENT_TYPES);
		partitioner.connect(doc);

		assertEquals(RubyPartitionScanner.SINGLE_LINE_COMMENT, partitioner.getContentType(3));
	}

}
