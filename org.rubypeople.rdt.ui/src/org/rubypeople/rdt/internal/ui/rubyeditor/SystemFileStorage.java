/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.ui.rubyeditor;

import java.io.*;
import org.eclipse.core.runtime.*;
import org.eclipse.core.resources.*;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;


public class SystemFileStorage extends PlatformObject implements IStorage {
	private File file;
	/**
	 * Constructor for SystemFileStorage.
	 */
	public SystemFileStorage(File file) {
		this.file = file;
	}

	public File getFile() {
		return file;
	}
	public InputStream getContents() throws CoreException {
		try {
			return new FileInputStream(file);
		} catch (FileNotFoundException e) {
			IStatus status =
				new Status(IStatus.ERROR, RdtUiPlugin.PLUGIN_ID, IStatus.OK, null, e);
			throw new CoreException(status);
		}
	}
	public IPath getFullPath() {
		return new Path(file.getAbsolutePath());
	}
	public String getName() {
		return file.getName();
	}
	public boolean isReadOnly() {
		return true;
	}

	public boolean equals(Object object) {
		return object instanceof SystemFileStorage
			&& getFile().equals(((SystemFileStorage) object).getFile());
	}

	public int hashCode() {
		return getFile().hashCode();
	}
}