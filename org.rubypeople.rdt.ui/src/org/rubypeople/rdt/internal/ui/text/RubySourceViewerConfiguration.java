package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.editors.text.TextEditor;

public class RubySourceViewerConfiguration extends SourceViewerConfiguration {
	protected RubyTextTools textTools;
	protected TextEditor textEditor;

	public RubySourceViewerConfiguration(RubyTextTools theTextTools, TextEditor theTextEditor) {
		super();
		textEditor = theTextEditor;
		textTools = theTextTools;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler= new PresentationReconciler();

		DefaultDamagerRepairer dr= new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}

	protected ITokenScanner getCodeScanner() {
		return textTools.getCodeScanner();
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { 
			IDocument.DEFAULT_CONTENT_TYPE
		};
	}
}
