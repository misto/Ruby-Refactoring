package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.WordRule;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;
import org.rubypeople.rdt.internal.ui.text.RubyWordDetector;
import org.rubypeople.rdt.ui.text.IColorManager;

public class RubyCodeScanner extends AbstractRubyScanner {

	protected String[] keywords;

	private static String[] fgTokenProperties = { IRubyColorConstants.RUBY_KEYWORD, IRubyColorConstants.RUBY_STRING, IRubyColorConstants.RUBY_DEFAULT
	// TODO Add Ability to set colors for return and operators
	// IRubyColorConstants.RUBY_METHOD_NAME,
	// IRubyColorConstants.RUBY_KEYWORD_RETURN,
	// IRubyColorConstants.RUBY_OPERATOR
	};

	/**
	 * Creates a Ruby code scanner
	 * 
	 * @param manager
	 *            the color manager
	 * @param store
	 *            the preference store
	 */
	public RubyCodeScanner(IColorManager manager, IPreferenceStore store) {
		super(manager, store);
		initialize();
	}

	/*
	 * @see AbstractRubyScanner#getTokenProperties()
	 */
	protected String[] getTokenProperties() {
		return fgTokenProperties;
	}

	protected List createRules() {
		List rules = new ArrayList();

		IToken token = getToken(IRubyColorConstants.RUBY_STRING);
		rules.add(new SingleLineRule("'", "'", token, '\\'));
		rules.add(new SingleLineRule("/", "/", token, '\\'));

		IToken defToken = getToken(IRubyColorConstants.RUBY_DEFAULT);
		setDefaultReturnToken(defToken);
		WordRule wordRule = new WordRule(new RubyWordDetector(), defToken);
		rules.add(wordRule);

		token = getToken(IRubyColorConstants.RUBY_KEYWORD);
		String[] keywords = RubyTextTools.getKeyWords();
		for (int keyWordIndex = 0; keyWordIndex < keywords.length; keyWordIndex++)
			wordRule.addWord(keywords[keyWordIndex], token);

		return rules;
	}
}