package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.rubypeople.rdt.ui.text.IColorManager;

public class SingleTokenRubyCodeScanner extends AbstractRubyScanner {

	private String[] fProperty;

	public SingleTokenRubyCodeScanner(IColorManager manager, IPreferenceStore store, String property) {
		super(manager, store);
		fProperty = new String[] { property};
		initialize();
	}

	/*
	 * @see AbstractRubyScanner#getTokenProperties()
	 */
	protected String[] getTokenProperties() {
		return fProperty;
	}

	/*
	 * @see AbstractRubyScanner#createRules()
	 */
	protected List createRules() {
		setDefaultReturnToken(getToken(fProperty[0]));
		return null;
	}

}
