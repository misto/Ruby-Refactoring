package org.rubypeople.rdt.internal.ui;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class RubyUIMessages {
	private static final String RESOURCE_BUNDLE= RubyUIMessages.class.getName();
	private static ResourceBundle resourceBundle= ResourceBundle.getBundle(RESOURCE_BUNDLE);

	private RubyUIMessages() {
	}

	public static String getString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
	
	public static String getFormattedString(String key, String arg) {
		return getFormattedString(key, new String[] { arg });
	}
	
	public static String getFormattedString(String key, String[] args) {
		return MessageFormat.format(getString(key), args);	
	}
	
	public static ResourceBundle getResourceBundle() {
		return resourceBundle;
	}
}
