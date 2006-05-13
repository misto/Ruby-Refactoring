package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.WordRule;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubyWordDetector;
import org.rubypeople.rdt.ui.text.IColorManager;
import org.rubypeople.rdt.ui.text.RubyTextTools;

public class RubyCodeScanner extends AbstractRubyScanner {

    protected String[] keywords;

    private static String[] fgTokenProperties = { IRubyColorConstants.RUBY_KEYWORD,
            IRubyColorConstants.RUBY_DEFAULT, IRubyColorConstants.RUBY_FIXNUM,
            IRubyColorConstants.RUBY_CHARACTER, IRubyColorConstants.RUBY_SYMBOL,
            IRubyColorConstants.RUBY_INSTANCE_VARIABLE, IRubyColorConstants.RUBY_GLOBAL
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

        rules.add(new CharacterRule(getToken(IRubyColorConstants.RUBY_CHARACTER)));

        IToken fixnumToken = getToken(IRubyColorConstants.RUBY_FIXNUM);
        rules.add(new RubyNumberRule(fixnumToken));

        rules.add(new SymbolRule(getToken(IRubyColorConstants.RUBY_SYMBOL)));

        //rules.add(new ClassVariableRule(getToken(IRubyColorConstants.RUBY_CLASS_VARIABLE)));

        IToken defToken = getToken(IRubyColorConstants.RUBY_DEFAULT);
        setDefaultReturnToken(getToken(IRubyColorConstants.RUBY_DEFAULT));

        IWordDetector instanceVarDetector = new InstanceVariableDetector();
        WordRule instanceVarRule = new WordRule(instanceVarDetector, getToken(IRubyColorConstants.RUBY_INSTANCE_VARIABLE));
        rules.add(instanceVarRule);
        
        WordRule gloablRule = new WordRule(new GlobalDetector(), getToken(IRubyColorConstants.RUBY_GLOBAL));
        rules.add(gloablRule);
        
        IWordDetector wordDetector = new RubyWordDetector();
        WordRule wordRule = new WordRule(wordDetector, defToken);
        IToken token = getToken(IRubyColorConstants.RUBY_KEYWORD);
        String[] keywords = RubyTextTools.getKeyWords();
        for (int keyWordIndex = 0; keyWordIndex < keywords.length; keyWordIndex++)
            wordRule.addWord(keywords[keyWordIndex], token);
        rules.add(wordRule);

        return rules;
    }
}