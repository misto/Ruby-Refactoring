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
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public abstract class AbstractRubyScanner extends BufferedRuleBasedScanner {
	protected RubyTextTools textTools;

	public AbstractRubyScanner(RubyTextTools theTextTools) {
		super();
		textTools = theTextTools;
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
		boolean bold = textTools.getPreferenceStore().getBoolean(colorKey + "_bold");
		return new Token(new TextAttribute(textTools.getColorProvider().getColor(colorKey), null, bold ? SWT.BOLD : SWT.NORMAL));
	}

}
