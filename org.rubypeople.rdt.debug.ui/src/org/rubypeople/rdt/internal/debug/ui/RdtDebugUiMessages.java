package org.rubypeople.rdt.internal.debug.ui;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class RdtDebugUiMessages {

	private static final String BUNDLE_NAME =
		"org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages";
	//$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE =
		ResourceBundle.getBundle(BUNDLE_NAME);

	private RdtDebugUiMessages() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}