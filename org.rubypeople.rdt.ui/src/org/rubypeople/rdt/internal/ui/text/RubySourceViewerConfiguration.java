package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.editors.text.TextEditor;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCompletionProcessor;

public class RubySourceViewerConfiguration extends SourceViewerConfiguration {
	protected RubyTextTools textTools;
	protected TextEditor textEditor;

	public RubySourceViewerConfiguration(RubyTextTools theTextTools, TextEditor theTextEditor) {
		super();
		textEditor = theTextEditor;
		textTools = theTextTools;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getMultilineCommentScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, RubyPartitionScanner.MULTI_LINE_COMMENT);

		dr = new DefaultDamagerRepairer(getSinglelineCommentScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, RubyPartitionScanner.SINGLE_LINE_COMMENT);

		dr = new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.STRING);
		reconciler.setRepairer(dr, RubyPartitionScanner.STRING);

		return reconciler;
	}

	protected ITokenScanner getCodeScanner() {
		return textTools.getCodeScanner();
	}

	protected ITokenScanner getMultilineCommentScanner() {
		return textTools.getMultilineCommentScanner();
	}

	protected ITokenScanner getSinglelineCommentScanner() {
		return textTools.getSinglelineCommentScanner();
	}

	protected ITokenScanner getStringScanner() {
		return textTools.getStringScanner();
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, RubyPartitionScanner.MULTI_LINE_COMMENT, RubyPartitionScanner.SINGLE_LINE_COMMENT, RubyPartitionScanner.STRING };
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant contentAssistant = new ContentAssistant();
		contentAssistant.setContentAssistProcessor(new RubyCompletionProcessor(textTools), IDocument.DEFAULT_CONTENT_TYPE);

		contentAssistant.setProposalPopupOrientation(ContentAssistant.PROPOSAL_OVERLAY);
		contentAssistant.setContextInformationPopupOrientation(ContentAssistant.CONTEXT_INFO_ABOVE);

		RubyContentAssistPreference.configure(contentAssistant, getPreferenceStore());
		return contentAssistant;
	}
	
	protected IPreferenceStore getPreferenceStore() {
		return RdtUiPlugin.getDefault().getPreferenceStore();
	}

	public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { "#", "" };
	}

}
