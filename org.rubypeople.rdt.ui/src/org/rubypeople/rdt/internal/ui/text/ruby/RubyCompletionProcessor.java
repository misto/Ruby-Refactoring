package org.rubypeople.rdt.internal.ui.text.ruby;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.parser.ParseException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ui.RdtUiImages;
import org.rubypeople.rdt.internal.ui.rubyeditor.templates.RubyFileContextType;
import org.rubypeople.rdt.internal.ui.rubyeditor.templates.RubyTemplateAccess;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public class RubyCompletionProcessor extends TemplateCompletionProcessor implements IContentAssistProcessor {

	private static String[] keywordProposals;
	protected RubyTextTools textTools;
	protected IContextInformationValidator contextInformationValidator = new RubyContextInformationValidator();

	private static String[] preDefinedGlobals = { "$!", "$@", "$_", "$.", "$&", "$n", "$~", "$=", "$/", "$\\", "$0", "$*", "$$", "$?", "$:"};
	private static String[] globalContexts = { "error message", "position of an error occurrence", "latest read string by `gets'", "latest read number of line by interpreter", "latest matched string by the regexep.", "latest matched string by nth parentheses of regexp.", "data for latest matche for regexp", "whether or not case-sensitive in string matching", "input record separator", "output record separator", "the name of the ruby scpript file", "command line arguments for the ruby scpript",
			"PID for ruby interpreter", "status of the latest executed child process", "array of paths that ruby interpreter searches for files"};

	/**
	 * The prefix for the current content assist
	 */
	protected String currentPrefix = null;

	/**
	 * Cursor position, counted from the beginning of the document.
	 * <P>
	 * The first position has index '0'.
	 */
	protected int cursorPosition = -1;

	/**
	 * The text viewer.
	 */
	private ITextViewer viewer;

	public RubyCompletionProcessor(RubyTextTools theTextTools) {
		super();
		textTools = theTextTools;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		this.viewer = viewer;
		ITextSelection selection = (ITextSelection) viewer.getSelectionProvider().getSelection();
		cursorPosition = selection.getOffset() + selection.getLength();


		ICompletionProposal[] normal = determineRubyElementProposals(viewer, documentOffset);
		ICompletionProposal[] templates = determineTemplateProposals(viewer, documentOffset);
		
		ICompletionProposal[] merged = merge(normal, templates);
		
		ICompletionProposal[] keywords = determineKeywordProposals(viewer, documentOffset);
		ICompletionProposal[] mergedTwo = merge(merged, keywords);
		return mergedTwo;
	}

	/**
	 * @param arrayOne
	 * @param arrayTwo
	 * @return
	 */
	private ICompletionProposal[] merge(ICompletionProposal[] arrayOne, ICompletionProposal[] arrayTwo) {
		ICompletionProposal[] merged = new ICompletionProposal[arrayOne.length + arrayTwo.length];
		System.arraycopy(arrayOne, 0, merged, 0, arrayOne.length);
		System.arraycopy(arrayTwo, 0, merged, arrayOne.length, arrayTwo.length);
		return merged;
	}

	/**
	 * @param viewer
	 * @param documentOffset
	 * @return
	 */
	private ICompletionProposal[] determineRubyElementProposals(ITextViewer viewer, int documentOffset) {
		ArrayList completionProposals = new ArrayList(getDocumentsRubyElements(viewer));

		String prefix = getCurrentPrefix(viewer.getDocument().get(), documentOffset);
		// following the JDT convention, if there's no text already entered,
		// then don't suggest imported elements
		if (prefix.length() > 0) {
			try {
				completionProposals.addAll(RubyProjectInformationProvider.instance().getImportedElements(RubyParser.parse(viewer.getDocument().get())));
			} catch (ParseException e) {
				log(e);
			}
		}

		ArrayList possibleProposals = new ArrayList();
		for (int i = 0; i < completionProposals.size(); i++) {
			String proposal = (String) completionProposals.get(i);
			if (proposal.startsWith(prefix)) {
				String message = "{0}";
				IContextInformation info = new ContextInformation(proposal, MessageFormat.format(message, new Object[] { proposal}));
				possibleProposals.add(new CompletionProposal(proposal.substring(prefix.length(), proposal.length()), documentOffset, 0, proposal.length() - prefix.length(), null, proposal, info, MessageFormat.format("Ruby keyword: {0}", new Object[] { proposal})));
			}
		}
		ICompletionProposal[] result = new ICompletionProposal[possibleProposals.size()];
		possibleProposals.toArray(result);
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getImage(org.eclipse.jface.text.templates.Template)
	 */
	protected Image getImage(Template template) {
		return RdtUiImages.get(RdtUiImages.IMG_TEMPLATE_PROPOSAL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getContextType(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 */
	protected TemplateContextType getContextType(ITextViewer textViewer, IRegion region) {
		return RubyTemplateAccess.getDefault().getContextTypeRegistry().getContextType(RubyFileContextType.RUBYFILE_CONTEXT_TYPE);
	}

	/**
	 * @return
	 */
	private ICompletionProposal[] determineTemplateProposals(ITextViewer refViewer, int documentOffset) {
		String prefix = getCurrentPrefix(viewer.getDocument().get(), documentOffset);
		ICompletionProposal[] matchingTemplateProposals;
		if (prefix.length() == 0) {
			matchingTemplateProposals = super.computeCompletionProposals(refViewer, documentOffset);
		} else {
			ICompletionProposal[] templateProposals = super.computeCompletionProposals(refViewer, documentOffset);
			List templateProposalList = new ArrayList(templateProposals.length);
			for (int i = 0; i < templateProposals.length; i++) {
				if (templateProposals[i].getDisplayString().toLowerCase().startsWith(prefix)) {
					templateProposalList.add(templateProposals[i]);
				}
			}
			matchingTemplateProposals = (ICompletionProposal[]) templateProposalList.toArray(new ICompletionProposal[templateProposalList.size()]);
		}
		return matchingTemplateProposals;
	}

	/**
	 * @param e
	 */
	private void log(ParseException e) {
		System.out.println(e.toString());
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

	private ICompletionProposal[] determineKeywordProposals(ITextViewer viewer, int documentOffset) {
		initKeywordProposals();
		
		String prefix = getCurrentPrefix(viewer.getDocument().get(), documentOffset);
		// following the JDT convention, if there's no text already entered,
		// then don't suggest keywords
		if (prefix.length() < 1) {
			return new ICompletionProposal[0];
		}
		List completionProposals = Arrays.asList(keywordProposals);

		// FIXME Refactor to combine the copied code in determineRubyElementProposals
		List possibleProposals = new ArrayList();
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
	 * 
	 */
	private void initKeywordProposals() {
		if (keywordProposals == null) {
			String[] keywords = textTools.getKeyWords();
			keywordProposals = new String[keywords.length + preDefinedGlobals.length];
			System.arraycopy(keywords, 0, keywordProposals, 0, keywords.length);
			System.arraycopy(preDefinedGlobals, 0, keywordProposals, keywords.length, preDefinedGlobals.length);
		}
	}

	protected String getCurrentPrefix(String documentString, int documentOffset) {
		int tokenLength = 0;
		while ((documentOffset - tokenLength > 0) && !Character.isWhitespace(documentString.charAt(documentOffset - tokenLength - 1)))
			tokenLength++;
		return documentString.substring((documentOffset - tokenLength), documentOffset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getTemplates(java.lang.String)
	 */
	protected Template[] getTemplates(String contextTypeId) {
		return RubyTemplateAccess.getDefault().getTemplateStore().getTemplates();
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