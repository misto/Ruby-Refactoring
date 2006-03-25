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
package org.rubypeople.rdt.ui;

/**
 * Central access point for the Ruby UI plug-in (id
 * <code>"org.rubypeople.rdt.ui"</code>). This class provides static methods
 * for:
 * <ul>
 * <li> creating various kinds of selection dialogs to present a collection of
 * Ruby elements to the user and let them make a selection.</li>
 * <li> opening a Ruby editor on a compilation unit.</li>
 * </ul>
 * <p>
 * This class provides static methods and fields only; it is not intended to be
 * instantiated or subclassed by clients.
 * </p>
 */
public final class RubyUI {

	private RubyUI() {
	// prevent instantiation of RubyUI.
	}

	/**
	 * The id of the Ruby plugin (value <code>"org.rubypeople.rdt.ui"</code>).
	 */
	public static final String ID_PLUGIN = "org.rubypeople.rdt.ui"; //$NON-NLS-1$
	public static final String ID_ACTION_SET = null;
	
	/**
	 * The view part id of the Ruby Browsing Projects view
	 * (value <code>"org.rubypeople.rdt.ui.ProjectsView"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static String ID_PROJECTS_VIEW= "org.rubypeople.rdt.ui.ProjectsView"; //$NON-NLS-1$
	
	/**
	 * The view part id of the Ruby Browsing Types view
	 * (value <code>"org.rubypeople.rdt.ui.TypesView"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static String ID_TYPES_VIEW= "org.rubypeople.rdt.ui.TypesView"; //$NON-NLS-1$
	
	/**
	 * The view part id of the Ruby Browsing Memberss view
	 * (value <code>"org.rubypeople.rdt.ui.MembersView"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static String ID_MEMBERS_VIEW= "org.rubypeople.rdt.ui.MembersView"; //$NON-NLS-1$
	
	/**
	 * The id of the Ruby Element Creation action set
	 * (value <code>"org.rubypeople.rdt.ui.RubyElementCreationActionSet"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static final String ID_ELEMENT_CREATION_ACTION_SET= "org.rubypeople.rdt.ui.RubyElementCreationActionSet"; //$NON-NLS-1$
}