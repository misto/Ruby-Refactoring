package org.rubypeople.rdt.internal.ui.text.ruby;

import java.text.MessageFormat;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public class RubyCompletionProcessor implements IContentAssistProcessor {
	public static String[] proposals;
	protected RubyTextTools textTools;
	protected IContextInformationValidator contextInformationValidator = new RubyContextInformationValidator();

	public RubyCompletionProcessor(RubyTextTools theTextTools) {
		super();
		textTools = theTextTools;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		String[] completionProposals = getDefaultProposals();
		ICompletionProposal[] result = new ICompletionProposal[completionProposals.length];
		for (int i = 0; i < completionProposals.length; i++) {
			IContextInformation info = new ContextInformation(completionProposals[i], MessageFormat.format("{0} some context information.", new Object[] { completionProposals[i] }));
			result[i] = new CompletionProposal(completionProposals[i], documentOffset, 0, completionProposals[i].length(), null, completionProposals[i], info, MessageFormat.format("Ruby keyword: {0}", new Object[] { completionProposals[i] }));
		}
		return result;
	}

	protected String[] getDefaultProposals() {
		if (proposals == null) {
			proposals = textTools.getKeyWords();
		}

		return proposals;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { '#' };
	}

	public IContextInformationValidator getContextInformationValidator() {
		return contextInformationValidator;
	}

	public String getErrorMessage() {
		return null;
	}

	protected class RubyContextInformationValidator implements IContextInformationValidator, IContextInformationPresenter {
		protected int installDocumentPosition;

		/**
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#install(IContextInformation, ITextViewer, int)
		 */
		public void install(IContextInformation info, ITextViewer viewer, int documentPosition) {
			installDocumentPosition = documentPosition;
		}

		/**
		 * @see org.eclipse.jface.text.contentassist.IContextInformationValidator#isContextInformationValid(int)
		 */
		public boolean isContextInformationValid(int documentPosition) {
			return Math.abs(installDocumentPosition - documentPosition) < 1;
		}

		/**
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int, TextPresentation)
		 */
		public boolean updatePresentation(int documentPosition, TextPresentation presentation) {
			return false;
		}
	}
}
