package org.rubypeople.rdt.internal.codeassist;

import java.util.Comparator;

import org.rubypeople.rdt.core.CompletionProposal;

public class CompletionProposalComparator implements Comparator<CompletionProposal> {

	public int compare(CompletionProposal o1, CompletionProposal o2) {
		return o1.getName().compareTo(o2.getName());
	}

}
