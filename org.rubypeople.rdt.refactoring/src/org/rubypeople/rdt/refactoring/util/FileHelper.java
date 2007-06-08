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
 * Copyright (C) 2006 Lukas Felber <lfelber@hsr.ch>
 * Copyright (C) 2007 Mirko Stocker <me@misto.ch>
 * Copyright (C) 2006 Thomas Corbat <tcorbat@hsr.ch>
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

package org.rubypeople.rdt.refactoring.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.internal.core.util.Util;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.refactoring.documentprovider.StringDocumentProvider;


public class FileHelper {
	public static final String DEFAULT_LINE_DELIMITER = System.getProperty("line.separator"); //$NON-NLS-1$
	
	public static String getFileContent(String fileName) {
		try {
			return getReaderContent(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			RubyPlugin.log(e);
		}
		return null;
	}
	
	private static String getReaderContent(Reader reader) {
		try {
			StringBuilder contentBuilder = new StringBuilder();
			
			int character;
			while ((character = reader.read()) != -1) {
				contentBuilder.append((char)character);
			}
			return contentBuilder.toString();
		} catch (IOException e) {
			RubyPlugin.log(e);
		}
		return null;
	}
	
	public static String getLineDelimiter(String document){
		String findLineSeparator = Util.findLineSeparator(document.toCharArray());
		return findLineSeparator == null ? DEFAULT_LINE_DELIMITER : findLineSeparator;
	}
	
	public static String getFileNameFromPath(String path) {
		String[] strings = path.split("/");
		return strings[strings.length - 1];
	}
	
	public static Collection<StringDocumentProvider> getAllDocuments() {
		final Collection<StringDocumentProvider> docs = new ArrayList<StringDocumentProvider>();
		
		try {
			RubyPlugin.getWorkspace().getRoot().accept(new IResourceVisitor(){
				public boolean visit(IResource resource) throws CoreException {

					if(resource instanceof IFile && Util.isValidRubyScriptName(((IFile) resource).getName())) {
						IFile file = (IFile) resource;
						docs.add(new StringDocumentProvider(file.getFullPath().toString(), new String(Util.getResourceContentsAsCharArray(file))));
					}
					return true;
				}});
			
		} catch (CoreException e) {
			RubyPlugin.log(e);
		}
		
		return docs;
	}
}
