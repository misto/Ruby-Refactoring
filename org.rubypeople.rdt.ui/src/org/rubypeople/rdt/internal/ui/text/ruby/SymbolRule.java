package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.text.rules.IToken;

/**
 * A rule which will return the given Token for Ruby symbols.
 * 
 * @author cawilliams
 * 
 */
public class SymbolRule extends SingleCharacterPrefixRule {

	private static final char PREFIX = ':';

	public SymbolRule(IToken token) {
		super(PREFIX, token, 2, Integer.MAX_VALUE);
	}

	protected boolean isValidCharacter(int c, int index) {
		if (!super.isValidCharacter(c, index))
			return false;
		
		// Symbols can only contain word characters (a-zA-Z0-9_)
		// TODO which special method names contain brackets?
		String theChar = String.valueOf((char) c);
		if(theChar.matches("\\w")) {
			return true;
		}
		else {
			return false;
		}
	}

}
