package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.text.rules.IWordDetector;

public class RubyWordDetector implements IWordDetector {

	public boolean isWordStart(char character) {
		return Character.isLetter(character);
	}

	public boolean isWordPart(char character) {
		return Character.isLetter(character);
	}

}
