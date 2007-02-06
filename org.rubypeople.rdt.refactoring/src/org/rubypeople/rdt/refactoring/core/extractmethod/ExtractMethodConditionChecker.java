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

package org.rubypeople.rdt.refactoring.core.extractmethod;

import java.util.Collection;

import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.SuperNode;
import org.jruby.ast.YieldNode;
import org.jruby.ast.ZSuperNode;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.exception.NoClassNodeException;
import org.rubypeople.rdt.refactoring.nodewrapper.PartialClassNodeWrapper;
import org.rubypeople.rdt.refactoring.util.NodeUtil;

public class ExtractMethodConditionChecker extends RefactoringConditionChecker {

	private ExtractMethodConfig config;

	public ExtractMethodConditionChecker(ExtractMethodConfig config) {
		super(config.getDocumentProvider(), config);
	}
	
	public void init(Object configObj) {
		this.config = (ExtractMethodConfig) configObj;
		initEnclosingNodes();
		if (!NodeProvider.isEmptyNode(config.getSelectedNodes())) {
			config.setExtractedMethodHelper(new ExtractedMethodHelper(config));
		}
	}
	
	private void initEnclosingNodes() {
		RootNode rootNode = config.getDocumentProvider().getRootNode();
		config.setRootNode(rootNode);
		config.setEnclosingScopeNode(SelectionNodeProvider.getEnclosingScope(rootNode, config.getSelection().getStartOfSelection()));
		config.setEnclosingMethodNode((MethodDefNode) SelectionNodeProvider.getEnclosingNode(rootNode, config.getSelection(), MethodDefNode.class));
		config.setSelectedNodes(SelectionNodeProvider.getSelectedNodes(rootNode, config.getSelection()));
		
		Node classNode = SelectionNodeProvider.getEnclosingNode(rootNode, config.getSelection(), ClassNode.class, SClassNode.class);
		try {
			config.setEnclosingClassNode(PartialClassNodeWrapper.getPartialClassNodeWrapper(classNode, rootNode));
		} catch (NoClassNodeException e) {
			/*don't care*/
		}
	}

	@Override
	protected void checkFinalConditions() {
		checkNewMethodName();
	}

	private void checkNewMethodName() {
		String newMethodName = config.getHelper().getMethodName();
		PartialClassNodeWrapper enclosingClassNode = config.getEnclosingClassNode();
		if (enclosingClassNode != null) {
			Collection<Node> methodNodes = NodeProvider.getSubNodes(enclosingClassNode.getWrappedNode(), DefnNode.class);
			for (Node aktNode : methodNodes) {
				if (((DefnNode)aktNode).getName().equals(newMethodName)) {
					addError("A method with the chose name already exists.");
				}
			}
		}
	}

	@Override
	protected void checkInitialConditions() {
		if(!existSelectedNodes()) {
			addError("There is nothing selected to extract.");
		} else if (containsYieldStatements()) {
			addError("Extracting methods not possible when the selected code contains a yield statement");
		} else if (containsSuperStatement()) {
			addError("Extracting methods not possible when the selected code contains super calls");
		} else if (isModuleInSelection()) {
			addError("Cannot extract a module.");
		} else if (isClassInSelection()) {
			addError("Selection must not contain a class.");
		} else if (isMethodInSelction()) {
			addError("Selection must not contain a method.");
		} else {
			checkInternalMethods();
		}
	}

	private boolean existSelectedNodes() {
		return !NodeProvider.isEmptyNode(config.getSelectedNodes());
	}
	
	private boolean containsYieldStatements() {
		return NodeProvider.hasSubNodes(config.getSelectedNodes(), YieldNode.class);
	}

	private boolean containsSuperStatement() {
		return NodeProvider.hasSubNodes(config.getSelectedNodes(), SuperNode.class, ZSuperNode.class);
	}	
	
	private boolean isModuleInSelection() {
		Node selctedNodes = config.getSelectedNodes();
		Collection<Node> moduleNodes = NodeProvider.getSubNodes(selctedNodes, ModuleNode.class);
		return !moduleNodes.isEmpty();
	}

	private boolean isClassInSelection() {
		Node selctedNodes = config.getSelectedNodes();
		Collection<Node> classNodes = NodeProvider.getSubNodes(selctedNodes, ClassNode.class);
		return !classNodes.isEmpty();
	}

	private boolean isMethodInSelction() {
		Node selctedNodes = config.getSelectedNodes();
		Collection<Node> methodNodes = NodeProvider.getSubNodes(selctedNodes, MethodDefNode.class);
		return !methodNodes.isEmpty();
	}

	private void checkInternalMethods() {
		if (config.hasEnclosingClassNode() && !config.hasEnclosingMethodNode()) {
			addError("To extract code out of class definitions, the selected code needs to be inside a method definition.");
		}
		if (config.hasEnclosingClassNode() && NodeProvider.hasSubNodes(NodeUtil.getBody(config.getEnclosingScopeNode()), DefnNode.class)) {
			addError("Extracting methods is not possible when the selected Method contains internal methods.");
		}

	}

}