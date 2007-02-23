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

package org.rubypeople.rdt.refactoring.core.renamelocalvariable;

import java.util.Collection;

import org.jruby.ast.ArgumentNode;
import org.jruby.ast.AssignableNode;
import org.jruby.ast.BlockArgNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.ast.types.INameNode;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.LocalNodeWrapper;
import org.rubypeople.rdt.refactoring.util.NameValidator;
import org.rubypeople.rdt.refactoring.util.NodeUtil;

public class RenameLocalConditionChecker extends RefactoringConditionChecker {

	private static final String ALREADY_EXISTS = "The chosen variable name already exists! Please go back and change it.";

	private static final String INVALID_NAME = "Please enter a valid name for the variable.";

	private static final String NO_VARIABLE_SELECTED = "No variable selected. Please select the variable you want to rename.";

	private static final String NO_LOCAL_VARIABLES = "There are no local variables at the current carret position.";

	private static final Class[] SELECTED_NODE_TYPES = {LocalVarNode.class, LocalAsgnNode.class, ArgumentNode.class,
		BlockArgNode.class, DVarNode.class, DAsgnNode.class};

	public static final String DEFAULT_ERROR = NO_LOCAL_VARIABLES;
	
	private RenameLocalConfig config;

	public RenameLocalConditionChecker(RenameLocalConfig config) {
		super(config.getDocumentProvider(), config);
	}
	
	public void init(Object configObj) {
		config = (RenameLocalConfig) configObj;
		RootNode rootNode = config.getDocumentProvider().getRootNode();
		Node selectedNode = SelectionNodeProvider.getSelectedNodeOfType(rootNode, config.getCaretPosition(), SELECTED_NODE_TYPES);
		if(selectedNode instanceof AssignableNode) {
			int start = selectedNode.getPosition().getStartOffset();
			int end = start + ((INameNode) selectedNode).getName().length();
			if(config.getCaretPosition() < start || config.getCaretPosition() > end) {
				return;
			}
		}
		
		config.setSelectedNode(selectedNode);
		if(selectedNode == null) {
			config.setLocalNames(NodeUtil.getScope(rootNode).getVariables());
			Collection<MethodDefNode> methodNodes = NodeProvider.getMethodNodes(rootNode);
			config.setSelectedMethod(SelectionNodeProvider.getSelectedNodeOfType(methodNodes, config.getCaretPosition(), MethodDefNode.class));
			return;
		}
		config.setSelectedMethod(SelectionNodeProvider.getEnclosingScope(rootNode, selectedNode));
		config.setLocalNames(NodeUtil.getScope(config.getSelectedMethod()).getVariables());
	}
	
	@Override
	protected void checkInitialConditions() {
		if (!config.hasSelectedNode() || !isSelectedNodeLocalVar()) {
			addError(NO_LOCAL_VARIABLES);
		}
	}

	private boolean isSelectedNodeLocalVar() {
		Node selected = config.getSelectedNode();
		if(NodeUtil.nodeAssignableFrom(selected, LocalNodeWrapper.LOCAL_NODES_CLASSES)){
			return true;
		}
		if(NodeUtil.nodeAssignableFrom(selected, ArgumentNode.class, BlockArgNode.class) && NodeUtil.nodeAssignableFrom(config.getSelectedMethod(), MethodDefNode.class)) {
			MethodDefNode methodNode = (MethodDefNode) config.getSelectedMethod();
			return methodNode.getNameNode() != selected;
		}
		return false;
	}

	@Override
	protected void checkFinalConditions() {
		LocalVariablesEditProvider editProvider = config.getRenameEditProvider();
		if (editProvider.getSelectedVariableName().equals("") && editProvider.getNewVariableName().equals("")) {
			addError(NO_VARIABLE_SELECTED);
		}

		if (!NameValidator.isValidLocalVariableName(editProvider.getNewVariableName())) {
			addError(INVALID_NAME);
		}

		if (editProvider.getSelectedVariableName().equals(editProvider.getNewVariableName())) {
			addError("You didn't choose a different name.");
		}

		for (String s : config.getLocalNames()) {
			if (editProvider.getNewVariableName().equals(s)) {
				addError(ALREADY_EXISTS);
			}
		}
	}

}
