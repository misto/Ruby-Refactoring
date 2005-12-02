package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;

public class CharacterRule implements IRule {

	/** The token to be returned when this rule is successful */
	protected IToken fToken;

	/**
	 * Creates a rule which will return the specified token when a numerical
	 * sequence is detected.
	 * 
	 * @param token
	 *            the token to be returned
	 */
	public CharacterRule(IToken token) {
		Assert.isNotNull(token);
		fToken = token;
	}

	
	/*
	 * @see IRule#evaluate(ICharacterScanner)
	 */
	public IToken evaluate(ICharacterScanner scanner) {
		int c = scanner.read();
		if (((char) c) == '?') {
			c = scanner.read();
			if (c == ICharacterScanner.EOF) {
				scanner.unread();
				return Token.UNDEFINED;
			}
			int d = scanner.read();
			if (!Character.isWhitespace((char) c) && Character.isWhitespace((char) d)) {
			  scanner.unread();
			  return fToken;
			}
		}

		scanner.unread();
		return Token.UNDEFINED;
	}
}
