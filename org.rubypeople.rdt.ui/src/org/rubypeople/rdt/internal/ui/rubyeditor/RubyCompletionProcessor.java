
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import sun.security.krb5.internal.i;

public class RubyCompletionProcessor implements IContentAssistProcessor {
	public static String[] proposals;

	public RubyCompletionProcessor() {
		super();
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int documentOffset) {
		String[] completionProposals = getDefaultProposals();
		ICompletionProposal[] result= new ICompletionProposal[completionProposals.length];
		for (int i= 0; i < completionProposals.length; i++) {
			result[i]= new CompletionProposal(completionProposals[i], documentOffset, completionProposals[i].length(), documentOffset);
		}
		return result;
	}

	protected String[] getDefaultProposals() {
		if (proposals == null) {
			proposals = new String[] { "class", "def", "end"};
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
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

}
