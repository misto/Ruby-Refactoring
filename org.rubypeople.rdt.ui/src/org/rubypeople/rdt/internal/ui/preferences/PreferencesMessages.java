/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.preferences;

import org.eclipse.osgi.util.NLS;

public final class PreferencesMessages extends NLS {

    private static final String BUNDLE_NAME = "org.rubypeople.rdt.internal.ui.preferences.PreferencesMessages";//$NON-NLS-1$

    private PreferencesMessages() {
    }

    public static String RiPreferencePage_description_label;
    public static String CodeFormatterPreferencePage_title;
    public static String CodeFormatterPreferencePage_description;
    public static String MembersOrderPreferencePage_category_button_up;
    public static String MembersOrderPreferencePage_category_button_down;
    public static String MembersOrderPreferencePage_visibility_button_up;
    public static String MembersOrderPreferencePage_visibility_button_down;
    public static String MembersOrderPreferencePage_label_description;
    public static String MembersOrderPreferencePage_fields_label;
    public static String MembersOrderPreferencePage_constructors_label;
    public static String MembersOrderPreferencePage_methods_label;
    public static String MembersOrderPreferencePage_staticfields_label;
    public static String MembersOrderPreferencePage_staticmethods_label;
    public static String MembersOrderPreferencePage_types_label;
    public static String MembersOrderPreferencePage_public_label;
    public static String MembersOrderPreferencePage_private_label;
    public static String MembersOrderPreferencePage_protected_label;
    public static String MembersOrderPreferencePage_usevisibilitysort_label;
    public static String TodoTaskPreferencePage_description;
    public static String TodoTaskPreferencePage_title;
    public static String TodoTaskInputDialog_new_title;
    public static String TodoTaskInputDialog_edit_title;
    public static String TodoTaskInputDialog_priority_high;
    public static String TodoTaskInputDialog_name_label;
    public static String TodoTaskInputDialog_priority_normal;
    public static String TodoTaskInputDialog_priority_low;
    public static String TodoTaskInputDialog_priority_label;
    public static String TodoTaskInputDialog_error_noSpace;
    public static String TodoTaskInputDialog_error_entryExists;
    public static String TodoTaskInputDialog_error_comma;
    public static String TodoTaskInputDialog_error_enterName;
    public static String RubyEditorPreferencePage_link_tooltip;
    public static String RubyEditorPreferencePage_link;
    public static String RiPreferencePage_ripath_label;
    public static String RiPreferencePage_rdocpath_label;
    public static String TodoTaskConfigurationBlock_tasks_default;
    public static String TodoTaskConfigurationBlock_markers_tasks_high_priority;
    public static String TodoTaskConfigurationBlock_markers_tasks_normal_priority;
    public static String TodoTaskConfigurationBlock_markers_tasks_low_priority;
    public static String TodoTaskConfigurationBlock_markers_tasks_name_column;
    public static String TodoTaskConfigurationBlock_markers_tasks_priority_column;
    public static String TodoTaskConfigurationBlock_casesensitive_label;
    public static String TodoTaskConfigurationBlock_markers_tasks_add_button;
    public static String TodoTaskConfigurationBlock_markers_tasks_edit_button;
    public static String TodoTaskConfigurationBlock_markers_tasks_remove_button;
    public static String TodoTaskConfigurationBlock_markers_tasks_setdefault_button;
    public static String TodoTaskConfigurationBlock_needsbuild_title;
    public static String TodoTaskConfigurationBlock_needsfullbuild_message;
    public static String TodoTaskConfigurationBlock_needsprojectbuild_message;

    static {
        NLS.initializeMessages(BUNDLE_NAME, PreferencesMessages.class);
    }
}
