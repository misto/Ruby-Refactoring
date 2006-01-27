package org.rubypeople.rdt.internal.ui.text.ruby;

import org.eclipse.jface.text.rules.IWordDetector;


public class IdentifierDetector implements IWordDetector {

    public boolean isWordStart(char c) {
        if (c == '_') return true;
        return Character.isLetter(c);
    }

    public boolean isWordPart(char c) {
        if (c == '_') return true;
        if (Character.isWhitespace(c)) return false;
        if (Character.isDigit(c)) return true;
        return Character.isLetter(c);
    }

}
