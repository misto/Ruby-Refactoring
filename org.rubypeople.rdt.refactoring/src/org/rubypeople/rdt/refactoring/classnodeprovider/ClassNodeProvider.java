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

package org.rubypeople.rdt.refactoring.classnodeprovider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jruby.ast.ClassNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.SClassNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentProvider;
import org.rubypeople.rdt.refactoring.exception.NoClassNodeException;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.MethodNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.PartialClassNodeWrapper;

public class ClassNodeProvider {

	private Map<String, ClassNodeWrapper> classNodeWrappers;

	protected DocumentProvider documentProvider;

	public ClassNodeProvider(DocumentProvider docProvider) {
		this(docProvider, true);
	}

	public ClassNodeProvider(DocumentProvider docProvider, boolean addActiveFile) {
		classNodeWrappers = new LinkedHashMap<String, ClassNodeWrapper>();
		this.documentProvider = docProvider;
		if(addActiveFile) {
			addSource(docProvider.getActiveFileName());
		}
	}

	public void addSource(String sourceName) {
		Node rootNode = NodeProvider.getRootNode(sourceName, documentProvider.getFileContent(sourceName));
		createClassNodes(rootNode);
	}

	private void createClassNodes(Node rootNode) {
		if (rootNode == null) {
			return;
		}
		Collection<Node> classNodes = NodeProvider.getSubNodes(rootNode, ClassNode.class);
		Collection<Node> moduleNodes = NodeProvider.getSubNodes(rootNode, ModuleNode.class);
		classNodes.addAll(NodeProvider.getSubNodes(rootNode, SClassNode.class));
		for (Node node : classNodes) {
			try {
				PartialClassNodeWrapper partialClassNode = PartialClassNodeWrapper.getPartialClassNodeWrapper(node, rootNode);
				addEnclosingModules(partialClassNode, moduleNodes);
				addPartialClassNode(partialClassNode, classNodeWrappers);
			} catch (NoClassNodeException e) {
				e.printStackTrace();
			}
		}
	}

	private void addEnclosingModules(PartialClassNodeWrapper partialClassNode, Collection<Node> moduleNodes) {
		ISourcePosition nodePosition = partialClassNode.getWrappedNode().getPosition();
		ArrayList<ModuleNode> enclosingModules = new ArrayList<ModuleNode>();
		for (Node currentModule : moduleNodes) {
			ISourcePosition modulePosition = currentModule.getPosition();
			if (modulePosition.getStartOffset() < nodePosition.getStartOffset() && modulePosition.getEndOffset() > nodePosition.getEndOffset()) {

				enclosingModules.add((ModuleNode) currentModule);
			}
		}

		partialClassNode.setEnclosingModules(enclosingModules);
	}

	private void addPartialClassNode(PartialClassNodeWrapper partialClassNode, Map<String, ClassNodeWrapper> classes) {
		String className = partialClassNode.getClassName();
		if (classes.containsKey(className)) {
			ClassNodeWrapper classNode = classes.get(className);
			classNode.addPartialClassNode(partialClassNode);
		} else {
			ClassNodeWrapper classNode = new ClassNodeWrapper(partialClassNode);
			classes.put(className, classNode);
		}
	}

	public void addClassNodeProvider(ClassNodeProvider provider) {
		if (provider != null) {
			for (ClassNodeWrapper classNode : provider.getAllClassNodes()) {
				if (!hasClassNode(classNode.getName())) {
					classNodeWrappers.put(classNode.getName(), classNode);
				}
			}
		}
	}

	public Collection<ClassNodeWrapper> getAllClassNodes() {
		return classNodeWrappers.values();
	}

	public ClassNodeWrapper getClassNode(String className) {
		return classNodeWrappers.containsKey(className) ? classNodeWrappers.get(className) : null;
	}

	public boolean hasClassNode(String className) {
		return classNodeWrappers.containsKey(className);
	}

	public Collection<ClassNodeWrapper> getSubClassesOf(String className) {
		Collection<ClassNodeWrapper> childs = new ArrayList<ClassNodeWrapper>();
		for (ClassNodeWrapper classNode : getAllClassNodes()) {
			if (classNode.getSuperClassName() != null && classNode.getSuperClassName().equals(className))
				childs.add(classNode);
		}
		return childs;
	}

	public Collection<MethodNodeWrapper> getAllMethodsFor(String className) {
		Collection<MethodNodeWrapper> methodNodes = new ArrayList<MethodNodeWrapper>();

		for (ClassNodeWrapper classNode : getClassAndAllSuperClassesFor(className)) {
			methodNodes.addAll(classNode.getMethods());
		}
		return methodNodes;
	}

	public Collection<ClassNodeWrapper> getClassAndAllSuperClassesFor(String className) {
		return getClassAndAllSuperClasses(getClassNode(className));
	}

	public Collection<ClassNodeWrapper> getClassAndAllSuperClasses(ClassNodeWrapper classNode) {
		ArrayList<ClassNodeWrapper> classNodes = new ArrayList<ClassNodeWrapper>();

		do {
			if (classNode != null) {
				classNodes.add(classNode);
			}
		} while (classNode != null && (classNode = getClassNode(classNode.getSuperClassName())) != null);
		return classNodes;
	}

	public Collection<ClassNodeWrapper> getClassAndAllSubClasses(ClassNodeWrapper classNode) {
		Collection<ClassNodeWrapper> classes = new ArrayList<ClassNodeWrapper>();
		if (classNode == null) {
			return classes;
		}
		classes.add(classNode);
		for (ClassNodeWrapper aktClassNode : getAllClassNodes()) {
			if (classNode.getName().equals(aktClassNode.getSuperClassName())) {
				classes.addAll(getClassAndAllSubClasses(aktClassNode));
			}
		}
		return classes;
	}
}
