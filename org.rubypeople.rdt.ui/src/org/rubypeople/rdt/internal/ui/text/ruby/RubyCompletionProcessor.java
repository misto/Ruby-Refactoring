package org.rubypeople.rdt.internal.ui.text.ruby;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.ParseException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public class RubyCompletionProcessor implements IContentAssistProcessor {

	public static String[] proposals;
	protected RubyTextTools textTools;
	protected IContextInformationValidator contextInformationValidator = new RubyContextInformationValidator();

	private static String[] preDefinedGlobals = { "$!", "$@", "$_", "$.", "$&", "$n", "$~", "$=", "$/", "$\\", "$0", "$*", "$$", "$?", "$:"};
	private static String[] globalContexts = { "error message", "position of an error occurrence", "latest read string by `gets'", "latest read number of line by interpreter", "latest matched string by the regexep.", "latest matched string by nth parentheses of regexp.", "data for latest matche for regexp", "whether or not case-sensitive in string matching", "input record separator", "output record separator", "the name of the ruby scpript file", "command line arguments for the ruby scpript",
			"PID for ruby interpreter", "status of the latest executed child process", "array of paths that ruby interpreter searches for files"};

	public RubyCompletionProcessor(RubyTextTools theTextTools) {
		super();
		textTools = theTextTools;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		ArrayList completionProposals = new ArrayList(getDocumentsRubyElements(viewer));

		String prefix = getCurrentToken(viewer.getDocument().get(), documentOffset);
		// following the JDT convention, if there's no text already entered,
		// then just suggest elements from the current file
		// If there's some text, present them with all options matching
		// that prefix
		if (prefix.length() > 0) {
			completionProposals.addAll(Arrays.asList(getDefaultProposals()));
			// completionProposals.addAll(RubyProjectInformationProvider.instance().getLibraryClassesAndModules());
		}

		ArrayList possibleProposals = new ArrayList();
		for (int i = 0; i < completionProposals.size(); i++) {
			String proposal = (String) completionProposals.get(i);
			if (proposal.startsWith(prefix)) {
				String message;
				if (isPredefinedGlobal(proposal)) {
					message = "{0} " + getContext(proposal);
				} else {
					message = "{0}";
				}
				IContextInformation info = new ContextInformation(proposal, MessageFormat.format(message, new Object[] { proposal}));
				possibleProposals.add(new CompletionProposal(proposal.substring(prefix.length(), proposal.length()), documentOffset, 0, proposal.length() - prefix.length(), null, proposal, info, MessageFormat.format("Ruby keyword: {0}", new Object[] { proposal})));
			}
		}
		ICompletionProposal[] result = new ICompletionProposal[possibleProposals.size()];
		possibleProposals.toArray(result);
		return result;
	}

	/**
	 * @param proposal
	 * @return
	 */
	private String getContext(String proposal) {
		for (int i = 0; i < preDefinedGlobals.length; i++) {
			if (proposal.equals(preDefinedGlobals[i])) return globalContexts[i];
		}
		return "";
	}

	/**
	 * @param proposal
	 * @return
	 */
	private boolean isPredefinedGlobal(String proposal) {
		for (int i = 0; i < preDefinedGlobals.length; i++) {
			if (proposal.equals(preDefinedGlobals[i])) return true;
		}
		return false;
	}

	private List getDocumentsRubyElements(ITextViewer viewer) {
		try {
			RubyProjectInformationProvider projectInfo = RubyProjectInformationProvider.instance();
			return projectInfo.getAllElements(RubyParser.parse(viewer.getDocument().get()));
		} catch (ParseException e) {
			RubyPlugin.log(e);
		}
		return new ArrayList();
	}

	protected String[] getDefaultProposals() {
		if (proposals == null) {
			String[] keywords = textTools.getKeyWords();
			proposals = new String[keywords.length + preDefinedGlobals.length];
			System.arraycopy(keywords, 0, proposals, 0, keywords.length);
			System.arraycopy(preDefinedGlobals, 0, proposals, keywords.length, preDefinedGlobals.length);
		}
		return proposals;
	}

	protected String getCurrentToken(String documentString, int documentOffset) {
		int tokenLength = 0;
		while ((documentOffset - tokenLength > 0) && !Character.isWhitespace(documentString.charAt(documentOffset - tokenLength - 1)))
			tokenLength++;
		return documentString.substring((documentOffset - tokenLength), documentOffset);
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer, int documentOffset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { '#'};
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
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#install(IContextInformation,
		 *      ITextViewer, int)
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
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int,
		 *      TextPresentation)
		 */
		public boolean updatePresentation(int documentPosition, TextPresentation presentation) {
			return false;
		}
	}
}