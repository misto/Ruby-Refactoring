/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2007 Mirko Stocker <me@misto.ch>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.rubypeople.rdt.refactoring.core.renamefile;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.refactoring.ui.NewNameListener;

public class NewFileNameListener extends NewNameListener {

	private final RenameFileConfig config;

	public NewFileNameListener(RenameFileConfig config) {
		super(config, new FileNameValidator(), new ArrayList<String>());
		this.config = config;
	}

	@Override
	protected boolean isValid(String name) {
		if(name.equals(config.getOriginalName())) {
			getErrorReceiver().setError("You did not change the name of the file.");
		} else if(doesAlreadyExist(name)) {
			getErrorReceiver().setError("A file or folder with this name already exists.");
		} else  if(!constructNewPath(name).isValidPath(constructNewPath(name).toString())) {
			getErrorReceiver().setError("This is an invalid name for a file.");
		} else  if(name.contains("/") || name.contains("\\")) {
			getErrorReceiver().setError("You cannot use \\ or / in a filename.");
		} else {
			getErrorReceiver().setError(null);
			return true;
		}
		return false;
	}
	
	private boolean doesAlreadyExist(String newName) {
		return RubyPlugin.getWorkspace().getRoot().findMember(constructNewPath(newName)) != null;
	}

	private IPath constructNewPath(String newName) {
		IResource resource = RubyPlugin.getWorkspace().getRoot().findMember(config.getOriginalFullName());
		return resource.getFullPath().removeLastSegments(1).append(newName);
	}
}