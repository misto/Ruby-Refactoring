package org.rubypeople.rdt.internal.ui;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.osgi.util.NLS;

public class RubyUIMessages extends NLS {

    private static final String BUNDLE_NAME = RubyUIMessages.class.getName();
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

    public static String StatusBarUpdater_num_elements_selected;
    
    public static String RubyElementLabels_anonym_type;
    public static String RubyElementLabels_anonym;
    public static String RubyElementLabels_import_container;
    public static String RubyElementLabels_initializer;
    public static String RubyElementLabels_concat_string;
    public static String RubyElementLabels_comma_string;
    public static String RubyElementLabels_declseparator_string;
    
    public static String RubyImageLabelprovider_assert_wrongImage;
	public static String CoreUtility_buildproject_taskname;
	public static String CoreUtility_buildall_taskname;
	public static String CoreUtility_job_title;

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
        return getFormattedString(key, new String[] { arg});
    }

    public static String getFormattedString(String key, String[] args) {
        return MessageFormat.format(getString(key), args);
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, RubyUIMessages.class);
    }
}
