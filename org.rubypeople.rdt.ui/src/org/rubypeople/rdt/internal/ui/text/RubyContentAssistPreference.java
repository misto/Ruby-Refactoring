package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.contentassist.ContentAssistant;

public class RubyContentAssistPreference {
	protected static ContentAssistant contentAssistant;
	protected static IPreferenceStore preferenceStore;

	public static void configure(ContentAssistant aContentAssistant, IPreferenceStore aPreferenceStore) {
		contentAssistant = aContentAssistant;
		preferenceStore = aPreferenceStore;
		
		contentAssistant.enableAutoActivation(true);
		contentAssistant.setAutoActivationDelay(500);
	}
}
