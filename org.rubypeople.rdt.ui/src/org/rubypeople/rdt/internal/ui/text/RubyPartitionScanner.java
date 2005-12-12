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
import org.eclipse.jface.text.rules.WordPatternRule;

public class RubyPartitionScanner extends RuleBasedPartitionScanner {

	
	public final static String RUBY_STRING = "partition_scanner_ruby_string";
	public final static String RUBY_MULTI_LINE_COMMENT = "partition_scanner_ruby_multiline_comment";
	public static final String RUBY_SINGLE_LINE_COMMENT = "partition_scanner_ruby_singleline_comment";
	public static final String RUBY_REGULAR_EXPRESSION = "partition_scanner_ruby_regular_expression";
	public static final String RUBY_COMMAND = "partition_scanner_ruby_command";
	public static final String HERE_DOC = "partition_scanner_here_doc";
		
	public static final String[] LEGAL_CONTENT_TYPES = {RUBY_STRING, RUBY_MULTI_LINE_COMMENT, RUBY_SINGLE_LINE_COMMENT, RUBY_REGULAR_EXPRESSION, RUBY_COMMAND};
		
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
		IToken hereDoc = new Token(RUBY_STRING) ;

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
		
		// Commands
		rules.add(new SingleLineRule("`", "`", command, '\\'));
		createGeneralDelimitedRules(rules, "%x", command, '\\');
		
        // Single line comments
		// ?# evaluates to the asiic value of #
		rules.add(new WordPatternRule(new NumberSignDetector(),"?", "#", Token.UNDEFINED)) ;
		rules.add(new EndOfLineRule("#", singleLineComment));
		// Multiline comments
		MultiLineRule multiLineCommentRule = new MultiLineRule("=begin", "=end", multiLineComment) ;
		multiLineCommentRule.setColumnConstraint(0) ;
		rules.add(multiLineCommentRule);
		
		rules.add(new HereDocPatternRule(hereDoc)) ;
		
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