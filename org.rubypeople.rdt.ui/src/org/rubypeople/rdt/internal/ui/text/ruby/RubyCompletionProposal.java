package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposal;


public class RubyCompletionProposal implements IRubyCompletionProposal {

    private String completion;
    private int start;
    private int length;
    private int relevance;
    private String label;
	private Image image;

    public RubyCompletionProposal(String completion, int start, int length, Image image, String label, int relevance) {
        this.completion = completion;
        this.start = start;
        this.length = length;
        this.label = label;
        this.image = image;
        this.relevance = relevance;
    }

    public int getRelevance() {
        return relevance;
    }

    public void apply(IDocument document) {
        // TODO Auto-generated method stub

    }

    public Point getSelection(IDocument document) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getAdditionalProposalInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getDisplayString() {
        return label;
    }

    public Image getImage() {
        return image;
    }

    public IContextInformation getContextInformation() {
        // TODO Auto-generated method stub
        return null;
    }

}
