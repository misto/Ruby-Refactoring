package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.rubypeople.rdt.internal.ui.text.RubyColorProvider;

public class SingleTokenRubyCodeScanner extends AbstractRubyScanner {
	protected String colorKey;

	public SingleTokenRubyCodeScanner(RubyColorProvider aColorProvider, IPreferenceStore prefs, String aColorKey) {
		super(aColorProvider, prefs);
		colorKey = aColorKey;
		initialize();
	}

	protected List createRules() {
		setDefaultReturnToken(getToken(colorKey));
		return null;
	}

}
