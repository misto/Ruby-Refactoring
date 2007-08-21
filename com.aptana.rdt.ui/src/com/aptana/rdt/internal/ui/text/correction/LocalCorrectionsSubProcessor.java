package com.aptana.rdt.internal.ui.text.correction;

import java.util.Collection;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.ui.RubyUI;
import org.rubypeople.rdt.ui.text.ruby.IInvocationContext;
import org.rubypeople.rdt.ui.text.ruby.IProblemLocation;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposal;

public class LocalCorrectionsSubProcessor {

	public static void addUnusedMemberProposal(IInvocationContext context, IProblemLocation problem,  Collection<IRubyCompletionProposal> proposals) {
		Image image= RubyPlugin.getDefault().getWorkbench().getSharedImages().getImage(ISharedImages.IMG_TOOL_DELETE);
		CorrectionProposal proposal = new CorrectionProposal("", problem.getOffset(), problem.getLength(), image, "clean up unused code", 100);
		proposals.add(proposal);
	}

	public static void addReplacementProposal(String replacement, String display, IProblemLocation problem, Collection<IRubyCompletionProposal> proposals) {
		addReplacementProposal(problem.getOffset(), problem.getLength(), replacement, display, proposals);
	}
	
	public static void addReplacementProposal(int offset, int length, String replacement, String display, Collection<IRubyCompletionProposal> proposals) {
		Image image= RubyUI.getSharedImages().getImage(org.rubypeople.rdt.ui.ISharedImages.IMG_OBJS_CORRECTION_CHANGE);
		CorrectionProposal proposal = new CorrectionProposal(replacement, offset, length, image, display, 100);
		proposals.add(proposal);		
	}

}
