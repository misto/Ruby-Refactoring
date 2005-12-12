package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
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
import org.rubypeople.rdt.internal.ui.text.ruby.hover.RubyCodeTextHover;

public class RubySourceViewerConfiguration extends SourceViewerConfiguration {

	protected RubyTextTools textTools;
	protected ITextEditor fTextEditor;
	private RubyCodeTextHover fRubyTextHover;

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
		reconciler.setDamager(dr, RubyPartitionScanner.RUBY_SINGLE_LINE_COMMENT);
		reconciler.setRepairer(dr, RubyPartitionScanner.RUBY_SINGLE_LINE_COMMENT);
		
		dr = new DefaultDamagerRepairer(getMultilineCommentScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.RUBY_MULTI_LINE_COMMENT);
		reconciler.setRepairer(dr, RubyPartitionScanner.RUBY_MULTI_LINE_COMMENT);

		dr = new DefaultDamagerRepairer(getStringScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.RUBY_STRING);
		reconciler.setRepairer(dr, RubyPartitionScanner.RUBY_STRING);

		dr = new DefaultDamagerRepairer(getRegexpScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.RUBY_REGULAR_EXPRESSION);
		reconciler.setRepairer(dr, RubyPartitionScanner.RUBY_REGULAR_EXPRESSION);
		
		dr = new DefaultDamagerRepairer(getCommandScanner());
		reconciler.setDamager(dr, RubyPartitionScanner.RUBY_COMMAND);
		reconciler.setRepairer(dr, RubyPartitionScanner.RUBY_COMMAND);
		
		
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
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, RubyPartitionScanner.RUBY_MULTI_LINE_COMMENT, RubyPartitionScanner.RUBY_STRING, RubyPartitionScanner.RUBY_SINGLE_LINE_COMMENT};
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
				return new String[] { viewer.getIndentString(), "\t", " " }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
	    }
	    return super.getIndentPrefixes(sourceViewer, contentType); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}    
    
    public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
        return new RubyDoubleClickSelector();
    }
	
	
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
		if(fRubyTextHover == null){
			fRubyTextHover = new RubyCodeTextHover();
		}
		return fRubyTextHover;
	}
	
}
