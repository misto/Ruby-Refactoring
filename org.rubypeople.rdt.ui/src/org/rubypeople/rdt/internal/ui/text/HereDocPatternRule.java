package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.rules.ICharacterScanner;
import org.eclipse.jface.text.rules.IToken;



public class HereDocPatternRule extends org.eclipse.jface.text.rules.PatternRule {

	public HereDocPatternRule(IToken token) {
		// TODO: escape char OK?
		super("<<", "", token, (char)-1, false, false);
	}

	
	protected boolean endSequenceDetected(ICharacterScanner scanner) {
		
		char firstChar = (char) scanner.read() ;
		
		if (Character.isWhitespace(firstChar)) {
			return false ;
		}
		
		if (firstChar == '-') {
			firstChar = (char) scanner.read() ;
		}
		boolean isQuoted = false ;
		char quote = ' ' ;
		if (firstChar == '\'' || firstChar == '"') {
			isQuoted = true ;
			quote = firstChar ;
			firstChar = (char) scanner.read() ;
		}
		String keyword = "" ; 
		char c = firstChar ;
		do {
			if ((byte) c == ICharacterScanner.EOF || Character.isWhitespace(c)) {
				break ;
			}
			keyword += c ;
			c = (char) scanner.read() ;
		} while ( true ) ;
		if (keyword.length() == 0) {
			return false ;
		}
		if (isQuoted && !(keyword.charAt(keyword.length() -1) == quote) ) {
			return false ;
		}
		if (isQuoted) {
			fEndSequence = keyword.substring(0, keyword.length() - 1).toCharArray() ;
		}
		else {
			fEndSequence = keyword.toCharArray() ;
		}
		boolean result = super.endSequenceDetected(scanner) ;
		if (!result) {
			return false ;
		}
		// the keyword must be at the beginning of the line
		return scanner.getColumn() == fEndSequence.length ;
	}

}
