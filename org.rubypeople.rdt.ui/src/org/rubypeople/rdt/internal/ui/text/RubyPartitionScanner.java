package org.rubypeople.rdt.internal.ui.text;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class RubyPartitionScanner extends RuleBasedPartitionScanner {

	
	public final static String STRING = "partition_scanner_ruby_string";
	public final static String MULTI_LINE_COMMENT = "partition_scanner_ruby_multiline_comment";
	public static final String SINGLE_LINE_COMMENT = "partition_scanner_ruby_singleline_comment";
	public static final String REGULAR_EXPRESSION = "partition_scanner_ruby_regular_expression";
		
	public static final String[] LEGAL_CONTENT_TYPES = {STRING, MULTI_LINE_COMMENT, SINGLE_LINE_COMMENT, REGULAR_EXPRESSION};
		
	public RubyPartitionScanner() {
		super();
		initialize();
	}

	protected void initialize() {
		IToken string = new Token(STRING);
		IToken multiLineComment = new Token(MULTI_LINE_COMMENT);
		IToken singleLineComment = new Token(SINGLE_LINE_COMMENT);
		IToken regexp = new Token(REGULAR_EXPRESSION);

		List rules = new ArrayList();

		// TODO Fix this to be able to recognize variable substitution inside
		// strings (and therefore have quotes inside them which don't end the
		// partition)
		
		// Strings
		rules.add(new SingleLineRule("\"", "\"", string, '\\'));
		rules.add(new SingleLineRule("'", "'", string, '\\'));
		createGeneralDelimitedRules(rules, "%q", string, '\\');
		createGeneralDelimitedRules(rules, "%Q", string, '\\');
		createGeneralDelimitedRules(rules, "%", string, '\\');
		// Regular expressions
		createRuleWithOptionalEndChars(rules, "/", "/", new char[] {'s', 'u', 'e', 'n', 'x', 'm', 'o', 'i'}, regexp, '\\');
		rules.add(new SingleLineRule("/", "/", regexp, '\\'));		
		createGeneralDelimitedRules(rules, "%r", regexp, '\\');
		
        // Single line comments
		rules.add(new EndOfLineRule("#", singleLineComment));
		// Multiline comments
		rules.add(new MultiLineRule("=begin", "=end", multiLineComment));

		// TODO Handle Heredocs!
		
		IPredicateRule[] result = new IPredicateRule[rules.size()];
		rules.toArray(result);
		setPredicateRules(result);
	}

	private void createRuleWithOptionalEndChars(List rules, String prefix, String endString, char[] options, IToken token, char escapeChar) {
		for (int i = 0; i < options.length; i++) {
			rules.add(new SingleLineRule(prefix, endString + options[i], token, escapeChar));
		}
	}

	private void createGeneralDelimitedRules(List rules, String prefix, IToken token, char escapeChar) {
		rules.add(new MultiLineRule(prefix + "[", "]", token, escapeChar));
		rules.add(new MultiLineRule(prefix + "{", "}", token, escapeChar));
		rules.add(new MultiLineRule(prefix + "(", ")", token, escapeChar));
		rules.add(new MultiLineRule(prefix + "<", ">", token, escapeChar));
	}
}