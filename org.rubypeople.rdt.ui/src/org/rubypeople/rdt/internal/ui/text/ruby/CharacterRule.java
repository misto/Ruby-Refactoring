package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.text.rules.IToken;

public class CharacterRule extends SingleCharacterPrefixRule {

    private static final char PREFIX = '?';
    private static final int WORD_LENGTH = 2;

    /**
     * Creates a rule which will return the specified token when a character
     * sequence is detected.
     * 
     * @param token
     *            the token to be returned
     */
    public CharacterRule(IToken token) {
        super(PREFIX, token, WORD_LENGTH, 2);
    }
}
