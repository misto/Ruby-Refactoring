package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

public class RubyTextTools {
	protected RubyColorProvider colorProvider;
	protected RubyPartitionScanner partitionScanner;

	public RubyTextTools() {
		super();
		
		colorProvider = new RubyColorProvider();
		partitionScanner = new RubyPartitionScanner();
	}

	public IDocumentPartitioner createDocumentPartitioner() {
		String[] types= new String[] {
//			JavaPartitionScanner.JAVA_DOC,
//			JavaPartitionScanner.JAVA_MULTI_LINE_COMMENT,
//			JavaPartitionScanner.JAVA_SINGLE_LINE_COMMENT,
//			JavaPartitionScanner.JAVA_STRING
		};

		return new DefaultPartitioner(getPartitionScanner(), types);
	}
	
	protected IPartitionTokenScanner getPartitionScanner() {
		return partitionScanner;
	}
}
