package org.rubypeople.rdt.internal.ui.text.ruby;

import java.util.Hashtable;
import java.util.List;

import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.BufferedRuleBasedScanner;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.rubypeople.rdt.internal.ui.text.RubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;

public abstract class AbstractRubyScanner extends BufferedRuleBasedScanner implements IPropertyChangeListener {
	protected RubyTextTools textTools;
	protected Hashtable tokens;
	public AbstractRubyScanner(RubyTextTools theTextTools) {
		super();
		textTools = theTextTools;
		tokens = new Hashtable();
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
		textTools.getPreferenceStore().addPropertyChangeListener(this);
	}

	protected IToken getToken(String colorKey) {
		Token newToken = new Token(this.createTextAttribute(colorKey));
		tokens.put(colorKey, newToken);
		return newToken;
	}

	protected TextAttribute createTextAttribute(String colorKey) {
		boolean bold = textTools.getPreferenceStore().getBoolean(colorKey + RubyColorConstants.RUBY_ISBOLD_APPENDIX);
		return new TextAttribute(textTools.getColorProvider().getColor(colorKey), null, bold ? SWT.BOLD : SWT.NORMAL);
	}

	public boolean affectsTextPresentation(PropertyChangeEvent event) {
		return event.getProperty().startsWith(RubyColorConstants.RUBY_COLOR_PREFIX) ;
	}

	public void propertyChange(PropertyChangeEvent event) {

		if (!this.affectsTextPresentation(event)) {
			return;
		}
		String colorKey = event.getProperty();
		int appendixStart = colorKey.indexOf(RubyColorConstants.RUBY_ISBOLD_APPENDIX);
		if (appendixStart != -1) {
			colorKey = colorKey.substring(0, appendixStart);
		}
		Token token = (Token) tokens.get(colorKey);
		if (token == null) {
			return;
		}
		if (appendixStart == -1) {
			textTools.getColorProvider().removeColor(colorKey);
		}

		token.setData(this.createTextAttribute(colorKey));

	}

}
