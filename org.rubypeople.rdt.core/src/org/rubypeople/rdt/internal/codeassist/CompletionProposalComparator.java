package org.rubypeople.rdt.internal.codeassist;

import java.util.Comparator;

import org.rubypeople.rdt.core.CompletionProposal;

public class CompletionProposalComparator implements Comparator<CompletionProposal> {

	public int compare(CompletionProposal o1, CompletionProposal o2) {
		if (o1.getRelevance() == o2.getRelevance())
			return o1.getName().compareTo(o2.getName());
		else
			return o2.getRelevance() - o1.getRelevance();
	}

}
