package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.rubypeople.rdt.internal.ui.text.RubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubyColorProvider;
import org.rubypeople.rdt.internal.ui.text.RubyWordDetector;

public class RubyCodeScanner extends BufferedRuleBasedScanner {
	protected RubyColorProvider colorProvider;

	protected final String[] keywords = new String[] {
		"new", "class", "def", "end", "require"
	};

	public RubyCodeScanner(RubyColorProvider aColorProvider) {
		super();
		colorProvider = aColorProvider;
		initialize();
	}

	protected void initialize() {
		initializeRules();
	}

	protected void initializeRules() {
		List rules = createRules();
		if (rules != null) {
			IRule[] result = new IRule[rules.size()];
			rules.toArray(result);
			setRules(result);
		}
	}

	protected List createRules() {
		List rules = new ArrayList();
		
		IToken token = getToken(RubyColorConstants.RUBY_DEFAULT);
		WordRule wordRule = new WordRule(new RubyWordDetector(), token);
		rules.add(wordRule);
		
		token = getToken(RubyColorConstants.RUBY_KEYWORD);
		for (int keyWordIndex = 0; keyWordIndex < keywords.length; keyWordIndex++)
			wordRule.addWord(keywords[keyWordIndex], token);

		return rules;
	}
	
	protected IToken getToken(String colorKey) {
		return new Token(new TextAttribute(colorProvider.getColor(colorKey), null, SWT.BOLD));
	}
}