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
 * Copyright (C) 2006 Mirko Stocker <me@misto.ch>
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

package org.rubypeople.rdt.refactoring.core.converttemptofield;

import java.util.Collection;

import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.Node;
import org.rubypeople.rdt.refactoring.JRubyRefactoringUtils;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.exception.NoClassNodeException;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.LocalNodeWrapper;

public class TempToFieldConditionChecker extends RefactoringConditionChecker {

	private TempToFieldConfig config;

	public TempToFieldConditionChecker(TempToFieldConfig config) {
		super(config.getDocProvider(), config);
	}
	
	public void init(Object configObj) {
		config = (TempToFieldConfig) configObj;
		Node selectedNode = findSelectedNode(LocalAsgnNode.class, LocalVarNode.class, DVarNode.class, DAsgnNode.class);
		if (selectedNode != null) {
			config.setSelectedNode(new LocalNodeWrapper(selectedNode));
			config.setEnclosingMethod((MethodDefNode) findSelectedNode(MethodDefNode.class));
			config.setEnclosingClassNode(getClassNode());
		}
	}

	private Node findSelectedNode(Class<?>... filterNodes) {
		return SelectionNodeProvider.getSelectedNodeOfType(config.getDocProvider().getRootNode().getBodyNode(), config.getCaretPosition(), filterNodes);
	}
	
	private ClassNodeWrapper getClassNode() {
		try {
			return SelectionNodeProvider.getSelectedClassNode(config.getDocProvider().getRootNode(), config.getCaretPosition());
		} catch (NoClassNodeException e) {
			return null;
		}
	}

	@Override
	public void checkFinalConditions() {

		Node rootNode = config.getDocProvider().getRootNode();

		if (config.isClassField()) {
			checkUniqueClassFieldName(config.getNewName(), rootNode);
		} else {
			checkUniqueInstVarName(config.getNewName(), rootNode);
		}
	}

	private void checkUniqueInstVarName(String newName, Node rootNode) {
		InstVarNameProvider nameProvider = new InstVarNameProvider();
		checkFieldName(newName, nameProvider.getInstVarNames(rootNode), "@", "Instance");
	}

	private void checkUniqueClassFieldName(String newName, Node rootNode) {
		ClassVarNameProvider nameProvider = new ClassVarNameProvider();
		checkFieldName(newName, nameProvider.getClassVarNames(rootNode), "@@", "Class");
	}

	private void checkFieldName(String newName, Collection<String> classVarNames, String namePrefix, String fieldTypeName) {
		String intendedClassFieldName = namePrefix.concat(newName);
		for (String currentName : classVarNames) {
			if (intendedClassFieldName.equals(currentName)) {
				addError(fieldTypeName + " field with name '" + newName + "' already exists.");
				break;
			}
		}
	}

	@Override
	public void checkInitialConditions() {
		
		if (config.getSelectedNode() == null) {
			addError("There is no local variable at the current caret position.");
		} else if (config.getEnclosingClassNode() == null) {
			addError("There is no enclosing class to insert fields.");
		} else if (config.getEnclosingMethod() == null) {
			addError("Can only convert local variables in methods to fields.");
		} else if (JRubyRefactoringUtils.isParameter(LocalNodeWrapper.getLocalNodeName(config.getSelectedNode()), config.getEnclosingMethod())) {
			addError("Cannot convert method parameters to fields.");
		} else if (isIterParameter()) {
			addError("Cannot convert block parameters to fields.");
		}
	}
	
	private boolean isIterParameter() {
		Collection<Node> allIterNodes = NodeProvider.getSubNodes(config.getEnclosingMethod(), IterNode.class);
		for (Node aktNode : allIterNodes) {
			IterNode aktIterNode = (IterNode) aktNode;
			if (iterNodeContainsAsArg(aktIterNode, config.getSelectedNode())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean iterNodeContainsAsArg(IterNode iterNode, LocalNodeWrapper localNode) {
		if (iterNode.getVarNode() == null) {
			return false;
		}
		String localNodeName = LocalNodeWrapper.getLocalNodeName(localNode);
		for (LocalNodeWrapper aktIterArg : LocalNodeWrapper.gatherLocalNodes(iterNode.getVarNode())) {
			String aktIterArgName = LocalNodeWrapper.getLocalNodeName(aktIterArg);
			if (aktIterArgName.equals(localNodeName)) {
				return true;
			}
		}
		return false;
	}
}
