package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCodeScanner;

public class RubyTextTools {
	protected RubyColorProvider colorProvider;
	protected RubyPartitionScanner partitionScanner;
	protected RubyCodeScanner codeScanner;

	public RubyTextTools() {
		super();
		
		colorProvider = new RubyColorProvider();
		codeScanner = new RubyCodeScanner(colorProvider);
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
	
	protected ITokenScanner getCodeScanner() {
		return codeScanner;
	}
}
