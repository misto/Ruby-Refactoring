/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.actions;

import org.eclipse.osgi.util.NLS;

public final class ActionMessages extends NLS {

    private static final String BUNDLE_NAME= ActionMessages.class.getName();//$NON-NLS-1$

    private ActionMessages() {
        // Do not instantiate
    }
   
    public static String ToggleLinkingAction_label;
    public static String ToggleLinkingAction_tooltip;
    public static String ToggleLinkingAction_description;
    public static String MemberFilterActionGroup_hide_fields_label;
    public static String MemberFilterActionGroup_hide_fields_tooltip;
    public static String MemberFilterActionGroup_hide_fields_description;
    public static String MemberFilterActionGroup_hide_static_label;
    public static String MemberFilterActionGroup_hide_static_tooltip;
    public static String MemberFilterActionGroup_hide_static_description;
    public static String MemberFilterActionGroup_hide_nonpublic_label;
    public static String MemberFilterActionGroup_hide_nonpublic_tooltip;
    public static String MemberFilterActionGroup_hide_nonpublic_description;
    public static String MemberFilterActionGroup_hide_localtypes_label;
    public static String MemberFilterActionGroup_hide_localtypes_tooltip;
    public static String MemberFilterActionGroup_hide_localtypes_description;

    static {
        NLS.initializeMessages(BUNDLE_NAME, ActionMessages.class);
    }
}