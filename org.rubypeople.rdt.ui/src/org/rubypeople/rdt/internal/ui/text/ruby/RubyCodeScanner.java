package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.NumberRule;
import org.eclipse.jface.text.rules.WordRule;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;
import org.rubypeople.rdt.internal.ui.text.RubyWordDetector;
import org.rubypeople.rdt.ui.text.IColorManager;

public class RubyCodeScanner extends AbstractRubyScanner {

	protected String[] keywords;

	private static String[] fgTokenProperties = {
			IRubyColorConstants.RUBY_KEYWORD, IRubyColorConstants.RUBY_DEFAULT,
			IRubyColorConstants.RUBY_FIXNUM, IRubyColorConstants.RUBY_CHARACTER,
            IRubyColorConstants.RUBY_SYMBOL
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

		rules.add(new CharacterRule(
				getToken(IRubyColorConstants.RUBY_CHARACTER)));

		IToken fixnumToken = getToken(IRubyColorConstants.RUBY_FIXNUM);
		rules.add(new NumberRule(fixnumToken));
        
        rules.add(new SymbolRule(
                getToken(IRubyColorConstants.RUBY_SYMBOL)));        

		IWordDetector wordDetector = new RubyWordDetector();
		IToken defToken = getToken(IRubyColorConstants.RUBY_DEFAULT);
		setDefaultReturnToken(defToken);
		WordRule wordRule = new WordRule(wordDetector, defToken);
		rules.add(wordRule);

		IToken token = getToken(IRubyColorConstants.RUBY_KEYWORD);
		String[] keywords = RubyTextTools.getKeyWords();
		for (int keyWordIndex = 0; keyWordIndex < keywords.length; keyWordIndex++)
			wordRule.addWord(keywords[keyWordIndex], token);

		return rules;
	}
}