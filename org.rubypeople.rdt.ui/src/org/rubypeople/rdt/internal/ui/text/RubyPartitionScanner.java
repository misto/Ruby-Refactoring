package org.rubypeople.rdt.internal.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class RubyPartitionScanner extends RuleBasedPartitionScanner {
	public final static String SKIP = "partition_scanner_ruby_skip";
	public final static String STRING = "partition_scanner_ruby_string";
	public final static String SINGLE_LINE_COMMENT = "partition_scanner_ruby_single_line_comment";
	public final static String MULTI_LINE_COMMENT = "partition_scanner_ruby_multiline_comment";

	public RubyPartitionScanner() {
		super();
		initialize();
	}

	protected void initialize() {
		IToken skip= new Token(SKIP);
		IToken string= new Token(STRING);
		IToken multiLineComment= new Token(MULTI_LINE_COMMENT);
		IToken singleLineComment= new Token(SINGLE_LINE_COMMENT);

		List rules = new ArrayList();

		rules.add(new EndOfLineRule("#", singleLineComment));
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("'", "'", skip, '\\'));
		rules.add(new MultiLineRule("=begin", "=end", multiLineComment));

		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}
}