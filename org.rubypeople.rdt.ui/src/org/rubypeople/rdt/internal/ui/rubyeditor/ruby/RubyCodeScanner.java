package org.rubypeople.rdt.internal.ui.rubyeditor.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.rubypeople.rdt.internal.ui.rubyeditor.util.RubyColorProvider;
import org.rubypeople.rdt.internal.ui.rubyeditor.util.RubyWhitespaceDetector;
import org.rubypeople.rdt.internal.ui.rubyeditor.util.RubyWordDetector;

public class RubyCodeScanner extends RuleBasedScanner {
	protected String[] keywords;
	protected RubyColorProvider colorProvider;

	public RubyCodeScanner(RubyColorProvider aColorProvider) {
		super();
		colorProvider = aColorProvider;
		initialize();
	}

	protected void initialize() {
		List rules = new ArrayList();
		addCommentRules(rules);
		addEndOfProgramRules(rules);
		addKeywordRules(rules);
		addStringRules(rules);
		addWhitespaceRules(rules);

		IRule[] completeRules = new IRule[rules.size()];
		rules.toArray(completeRules);
		setRules(completeRules);
	}
	
	protected void addCommentRules(List rulesList) {
		IToken singleLineCommentToken = new Token(new TextAttribute(colorProvider.getColor(colorProvider.SINGLE_LINE_COMMENT)));
		rulesList.add(new EndOfLineRule("#", singleLineCommentToken, '\\'));

		IToken multiLineCommentToken = new Token(new TextAttribute(colorProvider.getColor(colorProvider.MULTI_LINE_COMMENT)));
		rulesList.add(new MultiLineRule("=begin", "=end", multiLineCommentToken));
	}

	protected void addEndOfProgramRules(List rulesList) {
		IToken endOfProgramToken = new Token(new TextAttribute(colorProvider.getColor(colorProvider.END_OF_PROGRAM)));
		rulesList.add(new MultiLineRule( "__END__", "", endOfProgramToken));
	}

	protected void addKeywordRules(List rulesList) {
		keywords = getDefaultKeywords();
		IToken defaultToken = new Token(new TextAttribute(colorProvider.getColor(colorProvider.DEFAULT)));
		IToken keywordToken = new Token(new TextAttribute(colorProvider.getColor(colorProvider.KEYWORD), null, SWT.BOLD));

		WordRule wordRule = new WordRule(new RubyWordDetector(), defaultToken);
		for (int i = 0; i < keywords.length; i++)
			wordRule.addWord(keywords[i], keywordToken);
		rulesList.add(wordRule);
	}

	protected void addStringRules(List rulesList) {
		IToken stringToken = new Token(new TextAttribute(colorProvider.getColor(colorProvider.STRING)));
		rulesList.add(new SingleLineRule("\"", "\"", stringToken, '\\'));
		rulesList.add(new SingleLineRule("'", "'", stringToken, '\\'));
	}
	
	protected void addWhitespaceRules(List rulesList) {
		rulesList.add(new WhitespaceRule(new RubyWhitespaceDetector()));
	}

	protected String[] getDefaultKeywords() {
		return new String[] { "class", "def", "end", "new", "puts", "gets", "for", "in", "if", "else", "print", "&&", "&", "||", "|" };
	}
}