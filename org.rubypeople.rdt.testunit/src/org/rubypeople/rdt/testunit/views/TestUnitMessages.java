package org.rubypeople.rdt.testunit.views;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class TestUnitMessages {

	private static final String BUNDLE_NAME= "org.rubypeople.rdt.testunit.views.TestUnitMessages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE= ResourceBundle.getBundle(BUNDLE_NAME);

	private TestUnitMessages() {
	}

	/**
	 * Gets a string from the resource bundle and formats it with the argument
	 * 
	 * @param key	the string used to get the bundle value, must not be null
	 */
	public static String getFormattedString(String key, Object arg) {
		return MessageFormat.format(getString(key), new Object[] { arg });
	}

	/**
	 * Gets a string from the resource bundle and formats it with arguments
	 */	
	public static String getFormattedString(String key, Object[] args) {
		return MessageFormat.format(getString(key), args);
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
