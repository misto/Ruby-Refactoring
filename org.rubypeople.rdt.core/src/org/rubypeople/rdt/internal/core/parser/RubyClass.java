package org.rubypeople.rdt.internal.core.parser;

import org.apache.regexp.RE;
import org.apache.regexp.RESyntaxException;

public class RubyClass extends RubyElement {
	public RubyClass(String name) {
		super(name);
	}

	public RubyClass(String name, String sourceLine) {
		super(name);
		parse(sourceLine);
	}

	protected boolean parse(String sourceLine) {
		RE matcher = createElementMatcher();

		if (matcher.match(sourceLine)) {
			String elementName = getElementName(sourceLine);
			addElement(new RubyMethod(elementName));
			return true;
		}
		
		return false;
	}

	protected RE createElementMatcher() {
		try {
			return new RE("[a-zA-Z ]*def [a-zA-Z]+ *");
		} catch (RESyntaxException e) {
			throw new RuntimeException(e.toString());
		}
	}

	protected String getElementName(String line) {
		String word = "def ";
		int endOfWord = line.indexOf(word) + word.length();
		int spaceAfterWord = line.indexOf(" ", endOfWord);
		if (spaceAfterWord < endOfWord)
			spaceAfterWord = line.length();

		return line.substring(endOfWord, spaceAfterWord);
	}
}
