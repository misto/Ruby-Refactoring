package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.FileDocumentProvider;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.outline.RubyModel;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public class RubyDocumentProvider extends FileDocumentProvider {

	private HashMap modelToDocument = new HashMap() ;
	
	public RubyDocumentProvider() {
		super();
	}

	protected IDocument createDocument(Object element) throws CoreException {
		IDocument document = super.createDocument(element);
		if (document != null) {
			RubyTextTools tools = RdtUiPlugin.getDefault().getTextTools();
			IDocumentPartitioner partitioner = tools.createDocumentPartitioner();
			document.setDocumentPartitioner(partitioner);
			partitioner.connect(document);
		}
		return document;
	}
	
	public RubyModel getRubyModel(IEditorInput pEditorInput) {
		RubyModel rubyModel = (RubyModel) modelToDocument.get(pEditorInput) ;
		if (rubyModel == null) {
			rubyModel = new RubyModel() ;
			modelToDocument.put(pEditorInput, rubyModel) ;
		}
		return rubyModel ;
	}

	protected IAnnotationModel createAnnotationModel(Object element) throws CoreException {
		return new RubyAnnotationModel((IFileEditorInput) element);
	}
}