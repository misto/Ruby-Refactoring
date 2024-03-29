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

import java.util.Collection;

import org.jruby.ast.ClassNode;
import org.rubypeople.rdt.refactoring.core.renamefile.RenameFileConfig;
import org.rubypeople.rdt.refactoring.core.renamefile.RenameFileEditProvider;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;
import org.rubypeople.rdt.refactoring.editprovider.EditProvider;
import org.rubypeople.rdt.refactoring.editprovider.FileEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.FileMultiEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.IMultiFileEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.MultiFileEditProvider;
import org.rubypeople.rdt.refactoring.editprovider.ScopingNodeRenameEditProvider;
import org.rubypeople.rdt.refactoring.util.NameHelper;

public class RenameClassEditProvider implements IMultiFileEditProvider {

	private final RenameClassConfig config;
	private IDocumentProvider document;
	private String selectedName;

	public RenameClassEditProvider(RenameClassConfig config) {
		this.config = config;
		document = config.getDocumentProvider();
		selectedName = config.getSelectedNode().getCPath().getName();
	}

	private ChildClassesRenameEditProvider createChildrenEditProvider() {
		Collection<ClassNode> childClasses = new ClassFinder(document, selectedName, config.getModulePrefix()).findChildren();
		return new ChildClassesRenameEditProvider(childClasses, config.getNewName());
	}

	private ScopingNodeRenameEditProvider createPartialsEditProvider() {
		Collection<ClassNode> classNodes = new ClassFinder(document, selectedName, config.getModulePrefix()).findParts();
		return new ScopingNodeRenameEditProvider(classNodes, config.getNewName());
	}

	private ConstructorRenameEditProvider createConstructorEditProvider() {
		Collection<ConstructorCall> allCalls = new ClassInstanciationFinder().findAll(document, selectedName, config.getModulePrefix());
		return new ConstructorRenameEditProvider(allCalls, config.getNewName());
	}
	
	public Collection<FileMultiEditProvider> getFileEditProviders() {
		MultiFileEditProvider fileEdits = new MultiFileEditProvider();
		
		fileEdits.addEditProviders(createConstructorEditProvider().getEditProviders());
		fileEdits.addEditProviders(createPartialsEditProvider().getEditProviders());
		fileEdits.addEditProviders(createChildrenEditProvider().getEditProviders());
		
		if(NameHelper.fileNameEqualsClassName(config.getDocumentProvider().getActiveFileName(), selectedName)) {
			addRequireRenames(fileEdits);
		}
		
		return fileEdits.getFileEditProviders();
	}

	private void addRequireRenames(MultiFileEditProvider fileEdits) {
		RenameFileConfig renameFileConfig = new RenameFileConfig(config.getDocumentProvider(), config.getDocumentProvider().getActiveFileName());
		renameFileConfig.setNewName(NameHelper.fileNameFromClassName(config.getNewName()));
		
		for (FileMultiEditProvider fileMultiEditProvider : new RenameFileEditProvider(renameFileConfig).getFileEditProviders()) {
			for (EditProvider editProvider : fileMultiEditProvider.getEditProviders()) {
				fileEdits.addEditProvider(new FileEditProvider(fileMultiEditProvider.getFileName(), editProvider));
			}
		}
	}
}
