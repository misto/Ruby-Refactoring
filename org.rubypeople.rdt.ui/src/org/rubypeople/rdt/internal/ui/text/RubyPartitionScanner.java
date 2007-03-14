package org.rubypeople.rdt.internal.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class RubyPartitionScanner extends BufferedRuleBasedScanner implements
		IPartitionTokenScanner {

	/** The content type of the partition in which to resume scanning. */
	protected String fContentType;

	/** The offset of the partition inside which to resume. */
	protected int fPartitionOffset;

	public final static String RUBY_MULTI_LINE_COMMENT = IRubyPartitions.RUBY_MULTI_LINE_COMMENT;

	public static final String[] LEGAL_CONTENT_TYPES = {
			RUBY_MULTI_LINE_COMMENT
			};

	public RubyPartitionScanner() {
		super();
		initialize();
	}

	protected void initialize() {
		IToken multiLineComment = new Token(RUBY_MULTI_LINE_COMMENT);

		List rules = new ArrayList();
		rules.add(new DocumentationCommentRule(multiLineComment));
		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}

	/*
	 * @see ITokenScanner#setRange(IDocument, int, int)
	 */
	public void setRange(IDocument document, int offset, int length) {
		setPartialRange(document, offset, length, null, -1);
	}

	/*
	 * @see IPartitionTokenScanner#setPartialRange(IDocument, int, int, String,
	 *      int)
	 */
	public void setPartialRange(IDocument document, int offset, int length,
			String contentType, int partitionOffset) {
		fContentType = contentType;
		fPartitionOffset = partitionOffset;
		if (partitionOffset > -1) {
			int delta = offset - partitionOffset;
			if (delta > 0) {
				super.setRange(document, partitionOffset, length + delta);
				fOffset = offset;
				return;
			}
		}
		super.setRange(document, offset, length);
	}

	/*
	 * @see ITokenScanner#nextToken()
	 */
	public IToken nextToken() {

		if (fContentType == null || fRules == null) {
			// don't try to resume
			return super.nextToken();
		}

		// inside a partition

		fColumn = UNDEFINED;
		boolean resume = (fPartitionOffset > -1 && fPartitionOffset < fOffset);
		fTokenOffset = resume ? fPartitionOffset : fOffset;

		IRule rule;
		IToken token;

		for (int i = 0; i < fRules.length; i++) {
			rule = (IRule) fRules[i];
			if (rule instanceof IPredicateRule) {
				IPredicateRule predRule = (IPredicateRule) rule;
				token = predRule.getSuccessToken();
				if (fContentType.equals(token.getData())) {
					token = predRule.evaluate(this, resume);
					if (!token.isUndefined()) {
						fContentType = null;
						return token;
					}
				}
			} else {
				token= rule.evaluate(this);
				if (!token.isUndefined())
					return token;				
			}
		}

		// haven't found any rule for this type of partition
		fContentType = null;
		if (resume)
			fOffset = fPartitionOffset;
		return super.nextToken();
	}
}