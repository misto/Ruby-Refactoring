package org.rubypeople.rdt.internal.launching;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class RdtLaunchingMessages {

	private static final String BUNDLE_NAME = "org.rubypeople.rdt.internal.launching.RdtLaunchingMessages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	private RdtLaunchingMessages() {}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
