package org.rubypeople.rdt.internal.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.PatternRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordPatternRule;

public class RubyPartitionScanner extends BufferedRuleBasedScanner implements
		IPartitionTokenScanner {

	/** The content type of the partition in which to resume scanning. */
	protected String fContentType;

	/** The offset of the partition inside which to resume. */
	protected int fPartitionOffset;

	public final static String RUBY_STRING = IRubyPartitions.RUBY_STRING;

	public final static String RUBY_MULTI_LINE_COMMENT = IRubyPartitions.RUBY_MULTI_LINE_COMMENT;

	public static final String RUBY_SINGLE_LINE_COMMENT = IRubyPartitions.RUBY_SINGLE_LINE_COMMENT;

	public static final String RUBY_REGULAR_EXPRESSION = IRubyPartitions.RUBY_REGEX;

	public static final String RUBY_COMMAND = IRubyPartitions.RUBY_COMMAND;

	public static final String HERE_DOC = "partition_scanner_here_doc";

	public static final String[] LEGAL_CONTENT_TYPES = { RUBY_STRING,
			RUBY_MULTI_LINE_COMMENT, RUBY_SINGLE_LINE_COMMENT,
			RUBY_REGULAR_EXPRESSION, RUBY_COMMAND };

	public RubyPartitionScanner() {
		super();
		initialize();
	}

	protected void initialize() {
		IToken string = new Token(RUBY_STRING);
		IToken multiLineComment = new Token(RUBY_MULTI_LINE_COMMENT);
		IToken singleLineComment = new Token(RUBY_SINGLE_LINE_COMMENT);
		IToken regexp = new Token(RUBY_REGULAR_EXPRESSION);
		IToken command = new Token(RUBY_COMMAND);
		IToken hereDoc = new Token(RUBY_STRING);

		List rules = new ArrayList();

		// TODO Fix this to be able to recognize variable substitution inside
		// strings (and therefore have quotes inside them which don't end the
		// partition)

		// Strings
		rules.add(new PatternRule("\"", "\"", string, '\\', false, false));
		rules.add(new PatternRule("'", "'", string, '\\', false, false));

		// Regular expressions
		// TODO Work with options: 's', 'u', 'e', 'n', 'x', 'm', 'o', 'i'
		rules.add(new SingleLineRule("/", "/", regexp, '\\'));

		// Commands
		rules.add(new SingleLineRule("`", "`", command, '\\'));

		// Catch all the wacky Percent Syntax (Strings/commands/regexps)
		rules.add(new PercentSyntaxRule());

		// Single line comments
		// ?# evaluates to the ascii value of #
		rules.add(new WordPatternRule(new NumberSignDetector(), "?", "#",
				Token.UNDEFINED));
		rules.add(new EndOfLineRule("#", singleLineComment));
		// Multiline comments
		MultiLineRule multiLineCommentRule = new MultiLineRule("=begin",
				"=end", multiLineComment);
		multiLineCommentRule.setColumnConstraint(0);
		rules.add(multiLineCommentRule);

		rules.add(new HereDocPatternRule(hereDoc));

		// FIXME Create Hyrbid of RuleBasedPartitionScanner which allows IRule
		// or IPredicateRule and will only evaluate IPredicateRules if success
		// token matches content type
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