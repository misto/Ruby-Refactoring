package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.rules.DefaultPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.rubypeople.rdt.internal.ui.rubyeditor.ruby.*;

public class RubyDocumentProvider extends FileDocumentProvider {
	protected static RubyPartitionScanner partitionScanner;
	protected final static String[] TYPES = new String[] { RubyPartitionScanner.MULTI_LINE_COMMENT };

	public RubyDocumentProvider() {
		super();
	}

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			IDocumentPartitioner partitioner = createRubyPartitioner();
			partitioner.connect(document);
			document.setDocumentPartitioner(partitioner);
		}
		return document;
	}

	 protected DefaultPartitioner createRubyPartitioner() {
		return new DefaultPartitioner(getRubyPartitionScanner(), TYPES);
	}
	
	protected RubyPartitionScanner getRubyPartitionScanner() {
		if (partitionScanner == null)
			partitionScanner = new RubyPartitionScanner();

		return partitionScanner;
	}
}