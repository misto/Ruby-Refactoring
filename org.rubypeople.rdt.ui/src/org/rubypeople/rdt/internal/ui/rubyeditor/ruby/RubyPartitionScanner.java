package org.rubypeople.rdt.internal.ui.rubyeditor.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class RubyPartitionScanner extends RuleBasedPartitionScanner {
	public final static String MULTI_LINE_COMMENT = "multiline_comment";

	public RubyPartitionScanner() {
		super();
		initialize();
	}

	protected void initialize() {
		IToken comment = new Token(MULTI_LINE_COMMENT);

		List rules = new ArrayList();

		rules.add(new EndOfLineRule("#", Token.UNDEFINED));

		rules.add(new SingleLineRule("\"", "\"", Token.UNDEFINED, '\\'));
		rules.add(new SingleLineRule("'", "'", Token.UNDEFINED, '\\'));

		rules.add(new MultiLineRule("=begin", "=end", comment));

		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}

	static class EmptyCommentDetector implements IWordDetector {

		public boolean isWordStart(char c) {
			return (c == '=');
		}

		public boolean isWordPart(char c) {
			return (c == 'b' || c == 'e' || c == 'g' || c == 'i' || c == 'n' || c == 'd');
		}
	};
}