package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.List;

import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public class SingleTokenRubyCodeScanner extends AbstractRubyScanner {
	protected String colorKey;

	public SingleTokenRubyCodeScanner(RubyTextTools theTextTools, String aColorKey) {
		super(theTextTools);
		colorKey = aColorKey;
		initialize();
	}

	protected List createRules() {
		setDefaultReturnToken(getToken(colorKey));
		return null;
	}

}
