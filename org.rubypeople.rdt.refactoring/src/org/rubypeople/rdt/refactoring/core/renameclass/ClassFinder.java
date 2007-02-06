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
 * Copyright (C) 2006 Mirko Stocker <me@misto.ch>
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

package org.rubypeople.rdt.refactoring.core.renameclass;

import java.util.ArrayList;
import java.util.Collection;

import org.jruby.ast.ClassNode;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;
import org.rubypeople.rdt.refactoring.documentprovider.StringDocumentProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.PartialClassNodeWrapper;

public class ClassFinder implements IClassFinder {
	private final String name;
	private Collection<ClassNodeWrapper> classNodes;
	private final String modulePrefix;
	
	public ClassFinder(IDocumentProvider doc, String name, String modulePrefix) {
		this.name = name;
		this.modulePrefix = modulePrefix;
		classNodes = new ArrayList<ClassNodeWrapper>();
		
		for(String fileName : doc.getFileNames()) {
			if(! fileName.equals(doc.getActiveFileName())) {
				classNodes.addAll(new StringDocumentProvider(fileName, doc.getFileContent(fileName)).getClassNodeProvider().getAllClassNodes());
			}
		}
		
		classNodes.addAll(new StringDocumentProvider(doc.getActiveFileName(), doc.getActiveFileContent()).getClassNodeProvider().getAllClassNodes());
	}
	public Collection<ClassNode> findParts() {
		Collection<ClassNode> found = new ArrayList<ClassNode>();
		
		for (ClassNodeWrapper classNode : classNodes) {
			for(PartialClassNodeWrapper node : classNode.getPartialClassNodes()) {
				if(node.getClassName().equals(modulePrefix + name)) {
					found.add((ClassNode) node.getWrappedNode());
				}
			}
		}
		
		return found;
	}
	public Collection<ClassNode> findChildren() {
		Collection<ClassNode> found = new ArrayList<ClassNode>();
		
		for (ClassNodeWrapper classNode : classNodes) {
			for(PartialClassNodeWrapper node : classNode.getPartialClassNodes()) {
				if((node.getModulePrefix() + node.getSuperClassName()).equals(modulePrefix + name)) {
					found.add((ClassNode) node.getWrappedNode());
				}
			}
		}
		
		return found;
	}
}
