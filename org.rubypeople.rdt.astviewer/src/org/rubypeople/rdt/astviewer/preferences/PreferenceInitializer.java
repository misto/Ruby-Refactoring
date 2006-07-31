package org.rubypeople.rdt.astviewer.preferences;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import org.rubypeople.rdt.astviewer.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public void initializeDefaultPreferences() {
		IPreferenceStore store = Activator.getDefault().getPreferenceStore();
		store.setDefault(PreferenceConstants.P_SHOW_NEWLINE, true);
		store.setDefault(PreferenceConstants.P_SHOW_SCOPE, true);
	}
}
