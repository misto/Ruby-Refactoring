package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.RuleBasedDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.graphics.RGB;

public class RubySourceViewerConfiguration extends SourceViewerConfiguration {

	public RubySourceViewerConfiguration() {
		super();
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant assistant = new ContentAssistant();
		assistant.setContentAssistProcessor(new RubyCompletionProcessor(), IDocument.DEFAULT_CONTENT_TYPE);

		assistant.enableAutoActivation(true);
		assistant.setAutoActivationDelay(500);
		assistant.setProposalPopupOrientation(assistant.PROPOSAL_OVERLAY);
		assistant.setContextInformationPopupOrientation(assistant.CONTEXT_INFO_ABOVE);
		assistant.setContextInformationPopupBackground(RubyEditorEnvironment.getRubyColorProvider().getColor(new RGB(150, 150, 0)));
		return assistant;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		RuleBasedDamagerRepairer dr = new RuleBasedDamagerRepairer(RubyEditorEnvironment.getRubyCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new RuleBasedDamagerRepairer(RubyEditorEnvironment.getRubyPartitionScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, RubyPartitionScanner.MULTI_LINE_COMMENT);

		return reconciler;
	}
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, RubyPartitionScanner.MULTI_LINE_COMMENT };
	}

}