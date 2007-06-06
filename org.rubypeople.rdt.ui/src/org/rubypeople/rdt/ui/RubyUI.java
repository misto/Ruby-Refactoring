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

import org.eclipse.jface.util.Assert;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.SharedImages;

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
	
	private static ISharedImages fgSharedImages= null;
	
	/**
	 * The id of the Ruby plugin (value <code>"org.rubypeople.rdt.ui"</code>).
	 */
	public static final String ID_PLUGIN = "org.rubypeople.rdt.ui"; //$NON-NLS-1$

	/**
	 * The id of the Ruby action set
	 * (value <code>"org.rubypeople.rdt.ui.RubyActionSet"</code>).
	 */
	public static final String ID_ACTION_SET = "org.rubypeople.rdt.ui.RubyActionSet"; //$NON-NLS-1$

	/**
	 * The editor part id of the editor that presents Ruby compilation units
	 * (value <code>"org.rubypeople.rdt.ui.EditorRubyFile"</code>).
	 */	
	public static final String ID_RUBY_EDITOR=			"org.rubypeople.rdt.ui.EditorRubyFile"; //$NON-NLS-1$
	
	/**
	 * The editor part id of the editor that presents Ruby binary class files
	 * (value <code>"org.rubypeople.rdt.ui.ExternalRubyEditor"</code>).
	 */
	public static final String ID_EXTERNAL_EDITOR=			"org.rubypeople.rdt.ui.ExternalRubyEditor"; //$NON-NLS-1$
	
	/**
	 * The view part id of the Ruby Browsing Projects view (value
	 * <code>"org.rubypeople.rdt.ui.ProjectsView"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static String ID_PROJECTS_VIEW = "org.rubypeople.rdt.ui.ProjectsView"; //$NON-NLS-1$

	/**
	 * The view part id of the Ruby Browsing Types view (value
	 * <code>"org.rubypeople.rdt.ui.TypesView"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static String ID_TYPES_VIEW = "org.rubypeople.rdt.ui.TypesView"; //$NON-NLS-1$

	/**
	 * The view part id of the Ruby Browsing Memberss view (value
	 * <code>"org.rubypeople.rdt.ui.MembersView"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static String ID_MEMBERS_VIEW = "org.rubypeople.rdt.ui.MembersView"; //$NON-NLS-1$

	/**
	 * The id of the Ruby Element Creation action set (value
	 * <code>"org.rubypeople.rdt.ui.RubyElementCreationActionSet"</code>).
	 * 
	 * @since 0.8.0
	 */
	public static final String ID_ELEMENT_CREATION_ACTION_SET = "org.rubypeople.rdt.ui.RubyElementCreationActionSet"; //$NON-NLS-1$

	/**
	 * The id of the Ruby perspective
	 * (value <code>"org.rubypeople.rdt.ui.PerspectiveRuby"</code>).
	 */	
	public static final String ID_PERSPECTIVE= 		"org.rubypeople.rdt.ui.PerspectiveRuby"; //$NON-NLS-1$

	public static final String ID_RUBY_RESOURCE_VIEW = "org.rubypeople.rdt.ui.ViewRubyResources"; //$NON-NLS-1$
		
	/**
	 * Returns the Ruby element wrapped by the given editor input.
	 * 
	 * @param editorInput
	 *            the editor input
	 * @return the Ruby element wrapped by <code>editorInput</code> or
	 *         <code>null</code> if none
	 * @since 0.9.0
	 */
	public static IRubyElement getEditorInputRubyElement(
			IEditorInput editorInput) {
		Assert.isNotNull(editorInput);
		IRubyElement je = getWorkingCopyManager().getWorkingCopy(editorInput);
		if (je != null)
			return je;

		/*
		 * This needs works, see
		 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=120340
		 */
		return (IRubyElement) editorInput.getAdapter(IRubyElement.class);
	}

	/**
	 * Returns the shared images for the Ruby UI.
	 *
	 * @return the shared images manager
	 */
	public static ISharedImages getSharedImages() {
		if (fgSharedImages == null)
			fgSharedImages= new SharedImages();
			
		return fgSharedImages;
	}
	
	/**
	 * Returns the working copy manager for the Ruby UI plug-in.
	 * 
	 * @return the working copy manager for the Ruby UI plug-in
	 */
	public static IWorkingCopyManager getWorkingCopyManager() {
		return RubyPlugin.getDefault().getWorkingCopyManager();
	}
	/**
	 * Returns the DocumentProvider used for Ruby compilation units.
	 *
	 * @return the DocumentProvider for Ruby compilation units.
	 * 
	 * @see IDocumentProvider
	 * @since 2.0
	 */
	public static IDocumentProvider getDocumentProvider() {
		return RubyPlugin.getDefault().getRubyDocumentProvider();
	}
}