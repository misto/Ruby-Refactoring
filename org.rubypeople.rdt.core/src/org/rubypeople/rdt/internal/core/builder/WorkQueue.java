/**
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;

public class WorkQueue {

	ArrayList needsCompileList;
	ArrayList compiledList;

	public WorkQueue() {
		this.needsCompileList = new ArrayList(11);
		this.compiledList = new ArrayList(11);
	}

	public void add(IFile element) {
		needsCompileList.add(element);
	}

	public void addAll(IFile[] elements) {
		for (int i = 0, l = elements.length; i < l; i++)
			add(elements[i]);
	}

	public void clear() {
		this.needsCompileList.clear();
		this.compiledList.clear();
	}

	public void finished(IFile element) {
		needsCompileList.remove(element);
		compiledList.add(element);
	}

	public boolean isCompiled(IFile element) {
		return compiledList.contains(element);
	}

	public boolean isWaiting(IFile element) {
		return needsCompileList.contains(element);
	}

	public String toString() {
		return "WorkQueue: " + needsCompileList; //$NON-NLS-1$
	}
}
