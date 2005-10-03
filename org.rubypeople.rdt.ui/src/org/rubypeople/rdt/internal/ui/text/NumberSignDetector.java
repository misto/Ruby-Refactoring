package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.text.rules.IWordDetector;

public class NumberSignDetector implements IWordDetector {

	public NumberSignDetector() {
		super();
	}

	public boolean isWordStart(char c) {
		return isWordPart(c) ;
	}

	public boolean isWordPart(char c) {
		return '#' == c;
	}
}
