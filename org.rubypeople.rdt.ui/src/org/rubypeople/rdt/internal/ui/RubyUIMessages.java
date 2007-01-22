package org.rubypeople.rdt.internal.ui;

import java.text.MessageFormat;
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
	public static String MultiTypeSelectionDialog_errorTitle;
	public static String MultiTypeSelectionDialog_errorMessage;
	public static String TypeSelectionDialog_errorTitle;
	public static String TypeSelectionDialog_dialogMessage;
	public static String RubyElementLabels_default_package;
	public static String RdtUiPlugin_internalErrorOccurred;
	public static String RubyProjectLibraryPage_project;
	public static String RubyProjectLibraryPage_elementNotIProject;
	public static String RubyProjectPropertyPage_rubyProjectClosed;
	public static String RubyProjectLibraryPage_tabName;
	public static String RubyProjectPropertyPage_performOkException;
	public static String RubyProjectPropertyPage_performOkExceptionDialogMessage;
	public static String OptionalMessageDialog_dontShowAgain;
	public static String FoldingConfigurationBlock_error_not_exist;
	public static String FoldingConfigurationBlock_info_no_preferences;
	public static String RubyBasePreferencePage_label;
	public static String RDocPathErrorTitle;
	public static String RDocPathError;
	public static String ErrorRunningRdocTitle;
	public static String ToggleMenuRubyFilesOnly_Tooltip;
	public static String ToggleMenuRubyFilesOnly;
	public static String RubySearchPage_SearchForGroupLabel;
	public static String RubySearch_SearchForClassSymbol;
	public static String RubySearch_SearchForMethodSymbol;
	public static String RubySearch_ResultLabel;
	public static String HTML2TextReader_listItemPrefix;
	public static String HTMLTextPresenter_ellipsis;
	public static String RubyAnnotationHover_multipleMarkersAtThisLine;
	public static String ExceptionDialog_seeErrorLogMessage;
	public static String NewProjectCreationWizard_windowTitle;
	public static String NewProjectCreationWizard_projectCreationMessage;
	public static String WizardNewProjectCreationPage_pageName;
	public static String WizardNewProjectCreationPage_pageTitle;
	public static String WizardNewProjectCreationPage_pageDescription;

    private RubyUIMessages() {
    }

    public static String getFormattedString(String key, String arg) {
        return getFormattedString(key, new String[] { arg});
    }

    public static String getFormattedString(String key, String[] args) {
        return MessageFormat.format(key, (Object[])args);
    }

    public static ResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    static {
        NLS.initializeMessages(BUNDLE_NAME, RubyUIMessages.class);
    }
}
