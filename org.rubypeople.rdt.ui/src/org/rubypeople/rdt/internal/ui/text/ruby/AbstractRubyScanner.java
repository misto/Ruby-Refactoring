package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.swt.SWT;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyColorProvider;

public abstract class AbstractRubyScanner extends BufferedRuleBasedScanner {
	protected RubyColorProvider colorProvider;
	protected IPreferenceStore prefs;

	public AbstractRubyScanner(RubyColorProvider aColorProvider, IPreferenceStore thePrefs) {
		super();
		colorProvider = aColorProvider;
		prefs = thePrefs;
	}

	protected void initializeRules() {
		List rules = createRules();
		if (rules != null) {
			IRule[] result = new IRule[rules.size()];
			rules.toArray(result);
			setRules(result);
		}
	}

	protected abstract List createRules();

	protected void initialize() {
		initializeRules();
	}

	protected IToken getToken(String colorKey) {
		boolean bold = prefs.getBoolean(colorKey + "_bold");
		return new Token(new TextAttribute(colorProvider.getColor(colorKey), null, bold ? SWT.BOLD : SWT.NORMAL));
	}

}
