package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public class RubyDocumentProvider extends FileDocumentProvider {

	public RubyDocumentProvider() {
		super();
	}

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
//		if (document != null) {
//			RubyTextTools tools= RdtUiPlugin.getDefault().getTextTools();
//			IDocumentPartitioner partitioner= tools.createDocumentPartitioner();
//			document.setDocumentPartitioner(partitioner);
//			partitioner.connect(document);
//		}
		return document;
	}
}