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
package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ui.IContributorResourceAdapter;
import org.eclipse.ui.views.tasklist.ITaskListResourceAdapter;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;

/**
 * Implements basic UI support for Ruby elements. Implements handle to
 * persistent support for Ruby elements.
 */
public class RubyElementAdapterFactory implements IAdapterFactory, IContributorResourceAdapter{

	private static Class[] PROPERTIES = new Class[] { IResource.class, IContributorResourceAdapter.class, ITaskListResourceAdapter.class};

	private static ITaskListResourceAdapter fgTaskListAdapter;

	public Class[] getAdapterList() {
		return PROPERTIES;
	}

	public Object getAdapter(Object element, Class key) {
		IRubyElement ruby = getRubyElement(element);

		if (IResource.class.equals(key)) { return getResource(ruby); }
		if (IContributorResourceAdapter.class.equals(key)) { return this; }
		if (ITaskListResourceAdapter.class.equals(key)) { return getTaskListAdapter(); }
		return null;
	}

	private IResource getResource(IRubyElement element) {
		// can't use IRubyElement.getResource directly as we are interrested in
		// the
		// corresponding resource
		switch (element.getElementType()) {
		case IRubyElement.TYPE:
			// top level types behave like the CU
			IRubyElement parent = element.getParent();
			if (parent instanceof IRubyScript) { return RubyModelUtil.toOriginal((IRubyScript) parent).getResource(); }
			return null;
		case IRubyElement.SCRIPT:
			return RubyModelUtil.toOriginal((IRubyScript) element).getResource();
		case IRubyElement.PROJECT:
		case IRubyElement.RUBY_MODEL:
			return element.getResource();
		default:
			return null;
		}
	}

	public IResource getAdaptedResource(IAdaptable adaptable) {
		IRubyElement je = getRubyElement(adaptable);
		if (je != null) return getResource(je);

		return null;
	}

	private IRubyElement getRubyElement(Object element) {
		if (element instanceof IRubyElement) return (IRubyElement) element;
		return null;
	}

	private static ITaskListResourceAdapter getTaskListAdapter() {
		if (fgTaskListAdapter == null) fgTaskListAdapter = new RubyTaskListAdapter();
		return fgTaskListAdapter;
	}
}
