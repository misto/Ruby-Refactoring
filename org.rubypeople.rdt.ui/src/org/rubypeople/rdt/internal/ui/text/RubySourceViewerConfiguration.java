package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.ITokenScanner;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyAbstractEditor;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubySourceViewer;
import org.rubypeople.rdt.internal.ui.text.ruby.RubyCompletionProcessor;

public class RubySourceViewerConfiguration extends SourceViewerConfiguration {

	protected RubyTextTools textTools;
	protected ITextEditor fTextEditor;

	public RubySourceViewerConfiguration(RubyTextTools theTextTools, RubyAbstractEditor theTextEditor) {
		super();
		fTextEditor = theTextEditor;
		textTools = theTextTools;
	}

	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();
		reconciler.setDocumentPartitioning(IRubyPartitions.RUBY_PARTITIONING);
		
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getCodeScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		dr = new DefaultDamagerRepairer(getSinglelineCommentScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, RubyPartitionScanner.SINGLE_LINE_COMMENT);
		
		dr = new DefaultDamagerRepairer(getMultilineCommentScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, RubyPartitionScanner.MULTI_LINE_COMMENT);

		dr = new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.STRING);
		reconciler.setRepairer(dr, RubyPartitionScanner.STRING);

		dr = new DefaultDamagerRepairer(getRegexpScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.REGULAR_EXPRESSION);
		reconciler.setRepairer(dr, RubyPartitionScanner.REGULAR_EXPRESSION);
		
		dr = new DefaultDamagerRepairer(getCommandScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.COMMAND);
		reconciler.setRepairer(dr, RubyPartitionScanner.COMMAND);
		
		
		return reconciler;
	}

	private ITokenScanner getCommandScanner() {
		return textTools.getCommandScanner();
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
	
	protected ITokenScanner getRegexpScanner() {
		return textTools.getRegexpScanner();
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, RubyPartitionScanner.MULTI_LINE_COMMENT, RubyPartitionScanner.STRING, RubyPartitionScanner.SINGLE_LINE_COMMENT};
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant contentAssistant = new ContentAssistant();
		contentAssistant.setContentAssistProcessor(new RubyCompletionProcessor(getEditor()), IDocument.DEFAULT_CONTENT_TYPE);

		contentAssistant.setProposalPopupOrientation(ContentAssistant.PROPOSAL_OVERLAY);
		contentAssistant.setContextInformationPopupOrientation(ContentAssistant.CONTEXT_INFO_ABOVE);

		RubyContentAssistPreference.configure(contentAssistant, getPreferenceStore());
		return contentAssistant;
	}
	
	/*
	 * @see SourceViewerConfiguration#getAnnotationHover(ISourceViewer)
	 */
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		return new RubyAnnotationHover(RubyAnnotationHover.VERTICAL_RULER_HOVER);
	}
	
	/*
	 * @see SourceViewerConfiguration#getInformationControlCreator(ISourceViewer)
	 * @since 2.0
	 */
	public IInformationControlCreator getInformationControlCreator(ISourceViewer sourceViewer) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				return new DefaultInformationControl(parent, SWT.NONE, new HTMLTextPresenter(true));
			}
		};
	}
	
	/**
	 * Returns the editor in which the configured viewer(s) will reside.
	 *
	 * @return the enclosing editor
	 */
	protected ITextEditor getEditor() {
		return fTextEditor;
	}

	protected IPreferenceStore getPreferenceStore() {
		return RubyPlugin.getDefault().getPreferenceStore();
	}

	public String[] getDefaultPrefixes(ISourceViewer sourceViewer, String contentType) {
		return new String[] { "#", ""};
	}

	 /* (non-Javadoc)
     * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getReconciler(org.eclipse.jface.text.source.ISourceViewer)
     */
    public IReconciler getReconciler(ISourceViewer sourceViewer) {
        RubyReconciler reconciler = new RubyReconciler(fTextEditor, new RubyReconcilingStrategy((RubyAbstractEditor)fTextEditor), true);
	    reconciler.setDelay(RubyReconcilingStrategy.DELAY);
	    return reconciler;
    }
    
	public String[] getIndentPrefixes(ISourceViewer sourceViewer, String contentType) {
	    if (!(fTextEditor instanceof RubyEditor)) {
			return super.getIndentPrefixes(sourceViewer, contentType) ;
		}
	    if (sourceViewer instanceof RubySourceViewer) {
	        RubySourceViewer viewer = (RubySourceViewer) sourceViewer;
	        if (viewer.isTabReplacing()) {
				return new String[] { viewer.getTabReplaceString(), "\t", "" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
	    }
	    return super.getIndentPrefixes(sourceViewer, contentType); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}    
	
}
