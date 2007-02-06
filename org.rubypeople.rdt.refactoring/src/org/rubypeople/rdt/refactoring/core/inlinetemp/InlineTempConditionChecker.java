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

package org.rubypeople.rdt.refactoring.core.inlinetemp;

import java.util.ArrayList;
import java.util.Collection;

import org.jruby.ast.ClassNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.MultipleAsgnNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.refactoring.JRubyRefactoringUtils;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.LocalNodeWrapper;
import org.rubypeople.rdt.refactoring.util.NodeUtil;

public class InlineTempConditionChecker extends RefactoringConditionChecker {

	private InlineTempConfig config;

	public InlineTempConditionChecker(InlineTempConfig config) {
		super(config.getDocProvider(), config);
	}
	
	public void init(Object configObj) {
		this.config = (InlineTempConfig) configObj;
		Node rootNode = config.getDocProvider().getRootNode();
		int caretPosition = config.getCaretPosition();
		config.setEnclosingMethod(SelectionNodeProvider.getSelectedNodeOfType(rootNode, caretPosition, MethodDefNode.class));
		
		config.setEnclosingNode(SelectionNodeProvider.getEnclosingScope(rootNode, caretPosition));
		
		Node locVarNode = SelectionNodeProvider.getSelectedNodeOfType(rootNode, caretPosition, InlineTempConfig.LOCAL_VAR_NODE_CLASSES);
		if (locVarNode == null) {
			return;
		}

		config.setSelectedItem(new LocalNodeWrapper(locVarNode));
		config.setSelectedItemName(LocalNodeWrapper.getLocalNodeName(config.getSelectedItem()));

		initDefinitionNode();
		initLocalOccurrences();
	}
	private void initDefinitionNode() {
		Collection<LocalNodeWrapper> asgnNodes = LocalNodeWrapper.gatherLocalAsgnNodes(config.getEnclosingScopeNode());
		for (LocalNodeWrapper currentAsgnNode : asgnNodes) {
			if (currentAsgnNode.getName().equals(config.getSelectedItemName())) {
				config.setDefinitionNode(currentAsgnNode);
			}
		}
	}

	private void initLocalOccurrences() {
		Collection<LocalNodeWrapper> localOccurrences = new ArrayList<LocalNodeWrapper>();
		Collection<LocalNodeWrapper> nodesInMethod = LocalNodeWrapper.gatherLocalVarNodes(config.getEnclosingScopeNode());
		for (LocalNodeWrapper currentLocalNode : nodesInMethod) {
			String currentNodeName = LocalNodeWrapper.getLocalNodeName(currentLocalNode);
			if (currentNodeName.equals(config.getSelectedItemName())) {
				localOccurrences.add(currentLocalNode);
			}
		}
		config.setLocalOccurences(localOccurrences);
	}
	
	@Override
	protected void checkFinalConditions() {
		if (!isNewMethodNameUnique()) {
			addError("New method name is not unique.");
		}
	}

	@Override
	protected void checkInitialConditions() {
		if (config.getSelectedItem() == null) {
			addError("There is no local variable at the current carret position.");
		} else if (isTempParameter()) {
			addError("Cannot inline method parameters.");
		} else if (isTempMultiassigned()) {
			addError("Cannot inline a multi assigned local variable.");
		} else if (defintiontionContainsItself()) {
			addError("Cannot inline a local variable that uses its own value in the assignment.");
		} else if (isMultipleAsgnNode()) {
			addError("Inline in multiple assignments not jet supported.");
		}
	}
	
	private boolean isMultipleAsgnNode() {
		Node enclosingMultipleAssignmentNode = SelectionNodeProvider.getSelectedNodeOfType(config.getEnclosingScopeNode(), config.getCaretPosition(), MultipleAsgnNode.class);
		return enclosingMultipleAssignmentNode != null;
	}

	private boolean isTempParameter() {
		if (config.getEnclosingMethod() == null) {
			return false;
		}
		return JRubyRefactoringUtils.isParameter(config.getSelectedItem(), (MethodDefNode) config.getEnclosingMethod());
	}

	private boolean isNewMethodNameUnique() {

		Node environment = SelectionNodeProvider.getSelectedNodeOfType(config.getDocProvider().getRootNode(), config.getCaretPosition(), ClassNode.class, RootNode.class);
		Collection<MethodDefNode> methodNodes = NodeProvider.gatherMethodDefinitionNodes(NodeUtil.getBody(environment));

		for (MethodDefNode currentDefnNode : methodNodes) {
			if (currentDefnNode.getName().equals(config.getNewMethodName())) {
				return false;
			}
		}
		return true;
	}

	private boolean isTempMultiassigned() {
		Collection<LocalNodeWrapper> nodesInMethod = LocalNodeWrapper.gatherLocalAsgnNodes(config.getEnclosingScopeNode());
		int countOccurrence = 0;
		for (LocalNodeWrapper currentLocalNode : nodesInMethod) {
			String currentNodeName = LocalNodeWrapper.getLocalNodeName(currentLocalNode);
			if (currentNodeName.equals(config.getSelectedItemName())) {
				countOccurrence++;
			}
		}
		return countOccurrence > 1;
	}
	
	private boolean defintiontionContainsItself() {
		if(config.getDefinitionNode() == null) {
			return false;
		}
		ISourcePosition defPosition = config.getDefinitionNode().getWrappedNode().getPosition();

		for (LocalNodeWrapper currentOccurrence : config.getLocalOccurrences()) {
			ISourcePosition occurrencePosition = currentOccurrence.getWrappedNode().getPosition();
			if (defPosition.getStartOffset() <= occurrencePosition.getStartOffset() && defPosition.getEndOffset() >= occurrencePosition.getEndOffset()) {
				return true;
			}
		}
		return false;
	}

}
