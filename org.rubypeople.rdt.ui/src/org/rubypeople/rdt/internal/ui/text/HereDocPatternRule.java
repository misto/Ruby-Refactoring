package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.PatternRule;

public class HereDocPatternRule extends PatternRule {

    public HereDocPatternRule(IToken token) {
        // TODO: escape char OK?
        // FIXME We need to only handle exact cases, not <<=
        
        // Correct cases:
        // (http://www.zenspider.com/Languages/Ruby/QuickRef.html#8)
        // <<identifier - interpolated, goes until identifier
        // <<"identifier" - same thing
        // <<'identifier' - no interpolation
        // <<-identifier - you can indent the identifier by using "-" in front
        super("<<", "", token, (char) -1, false, false);
    }

    protected boolean endSequenceDetected(ICharacterScanner scanner) {
        char firstChar = (char) scanner.read();

        if (Character.isWhitespace(firstChar)) { return false; }
        
    	boolean indentedKeyword = false;
        if (firstChar == '-') {
        	indentedKeyword = true;
            firstChar = (char) scanner.read();
        }
        boolean isQuoted = false;
        char quote = ' ';
        if (firstChar == '\'' || firstChar == '"') {
            isQuoted = true;
            quote = firstChar;
            firstChar = (char) scanner.read();
        }
        String keyword = readKeyword(scanner, firstChar);
        if (keyword.length() == 0) { return false; }
        if (isQuoted && !(keyword.charAt(keyword.length() - 1) == quote)) { return false; }
        if (isQuoted) {
            fEndSequence = removeEndQuote(keyword).toCharArray();
        } else {
            fEndSequence = keyword.toCharArray();
        }
        boolean result = super.endSequenceDetected(scanner);
        if (!result) { return false; }
        
        if(indentedKeyword) {
        	rewindToBeginOfLine(scanner);
        	char lastChar;
        	do {
        		lastChar = (char) scanner.read();
        	}
        	while(Character.isWhitespace(lastChar));
        	
        	String newKeyword = readKeyword(scanner, lastChar);
        	
        	if(isQuoted)
        		return removeEndQuote(keyword).equals(newKeyword);
        	else
        		return keyword.equals(newKeyword);
        	
        } else {
        	return scanner.getColumn() == fEndSequence.length;
        }
    }

	private String removeEndQuote(String keyword) {
		return keyword.substring(0, keyword.length() - 1);
	}

	private void rewindToBeginOfLine(ICharacterScanner scanner) {
		char readChar = 0;
		while (readChar != '\n') {
			scanner.unread();
			scanner.unread();
			readChar = (char) scanner.read();
		}
	}

	private String readKeyword(ICharacterScanner scanner, char firstChar) {
		String keyword = "";
        char c = firstChar;
        do {
            if ((byte) c == ICharacterScanner.EOF || Character.isWhitespace(c)) {
                break;
            }
            keyword += c;
            c = (char) scanner.read();
        } while (true);
		return keyword;
	}

}
