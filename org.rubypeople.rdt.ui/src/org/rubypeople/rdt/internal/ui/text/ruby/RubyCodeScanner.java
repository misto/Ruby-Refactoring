package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WordRule;
import org.rubypeople.rdt.internal.ui.text.RubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;
import org.rubypeople.rdt.internal.ui.text.RubyWordDetector;

public class RubyCodeScanner extends AbstractRubyScanner {
	protected String[] keywords;

	public RubyCodeScanner(RubyTextTools theTextTools) {
		super(theTextTools);
		initialize();
	}

	protected List createRules() {
		List rules = new ArrayList();

		IToken token = getToken(RubyColorConstants.RUBY_STRING);
		rules.add(new SingleLineRule("'", "'", token, '\\'));

		token = getToken(RubyColorConstants.RUBY_DEFAULT);
		setDefaultReturnToken(token);
		WordRule wordRule = new WordRule(new RubyWordDetector(), token);
		rules.add(wordRule);

		token = getToken(RubyColorConstants.RUBY_KEYWORD);
		String[] keywords = textTools.getKeyWords();
		for (int keyWordIndex = 0; keyWordIndex < keywords.length; keyWordIndex++)
			wordRule.addWord(keywords[keyWordIndex], token);

		return rules;
	}
}