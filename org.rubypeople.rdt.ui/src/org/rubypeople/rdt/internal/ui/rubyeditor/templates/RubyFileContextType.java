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
package org.rubypeople.rdt.internal.ui.rubyeditor.templates;

import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;


/**
 * A very simple context type.
 */
public class RubyFileContextType extends TemplateContextType {

	/** This context's id */
	public static final String RUBYFILE_CONTEXT_TYPE= "org.rubypeople.rdt.ui.templateContextType.rubyFile"; //$NON-NLS-1$

	/**
	 * Creates a new Ruby context type. 
	 */
	public RubyFileContextType() {
		addGlobalResolvers();
	}

	private void addGlobalResolvers() {
		addResolver(new GlobalTemplateVariables.Cursor());
		addResolver(new GlobalTemplateVariables.WordSelection());
		addResolver(new GlobalTemplateVariables.LineSelection());
		addResolver(new GlobalTemplateVariables.Dollar());
		addResolver(new GlobalTemplateVariables.Date());
		addResolver(new GlobalTemplateVariables.Year());
		addResolver(new GlobalTemplateVariables.Time());
		addResolver(new GlobalTemplateVariables.User());
	}
}
