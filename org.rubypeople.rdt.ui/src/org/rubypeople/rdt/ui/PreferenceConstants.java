package org.rubypeople.rdt.ui;

import org.eclipse.jface.preference.IPreferenceStore;


public class PreferenceConstants {

	private PreferenceConstants() {
			
	}
	
	public static final String FORMAT_INDENTATION = "formatIndentation"; //$NON-NLS-1$
	
	public static void initializeDefaultValues(IPreferenceStore store) {
		store.setDefault(PreferenceConstants.FORMAT_INDENTATION, 2);
	}	
	
}
