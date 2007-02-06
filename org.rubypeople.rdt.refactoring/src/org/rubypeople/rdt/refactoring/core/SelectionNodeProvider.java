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

package org.rubypeople.rdt.refactoring.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import org.jruby.ast.ArrayNode;
import org.jruby.ast.AttrAssignNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.CaseNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.NewlineNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.WhenNode;
import org.jruby.ast.types.INameNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.refactoring.exception.NoClassNodeException;
import org.rubypeople.rdt.refactoring.nodewrapper.AttrAccessorNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.PartialClassNodeWrapper;

public class SelectionNodeProvider {

	public static Node getEnclosingScope(Node rootNode, Node from) {
		return getEnclosingScope(rootNode, from.getPosition().getStartOffset());
	}

	public static Node getEnclosingScope(Node rootNode, int where) {
		return SelectionNodeProvider.getSelectedNodeOfType(rootNode, where, MethodDefNode.class, ClassNode.class, RootNode.class, IterNode.class);
	}	
	
	public static Node getSelectedNodes(Node rootNode, SelectionInformation selection) {
		Node enclosingNode = getEnclosingNode(rootNode, selection, Node.class);

		if (enclosingNode == null)
			return null;
		if (NodeProvider.nodeAssignableFrom(enclosingNode, WhenNode.class)) {
			enclosingNode = getEnclosingNode(rootNode, selection, CaseNode.class);
		}
		if(NodeProvider.nodeAssignableFrom(enclosingNode, ArrayNode.class)) {
			Node node = getEnclosingNode(rootNode, selection, WhenNode.class);
			WhenNode enclosingWhen = (WhenNode) node;
			if(enclosingWhen != null && nodeEnclosesNode(enclosingWhen.getExpressionNodes(), enclosingNode)) {
				enclosingNode = getEnclosingNode(rootNode, selection, CaseNode.class);
			}
		}

		BlockNode enclosingBlockNode = (BlockNode) getEnclosingNode(rootNode, selection, BlockNode.class);
		if (enclosingBlockNode == null) {
			return enclosingNode;
		}

		Collection<Node> blockChildren = NodeProvider.getChildren(enclosingBlockNode);

		Node beginNode = getSelectedNodeOfType(enclosingBlockNode, selection.getStartOfSelection(), Node.class);
		Node beginBlockChildNode = getEnclosingNode(beginNode, blockChildren);
		Node endNode = getSelectedNodeOfType(enclosingBlockNode, selection.getEndOfSelection(), Node.class);
		Node endBlockChildNode = getEnclosingNode(endNode, blockChildren);
		
		Collection<Node> selectedNodes = getNodesFromTo(beginBlockChildNode, endBlockChildNode, blockChildren);

		if(isNodeContainedInNode(selectedNodes.toArray(new Node[selectedNodes.size()])[0], enclosingNode)) { 
			BlockNode blockAroundSelected = new BlockNode(NodeFactory.unionPositions(beginNode.getPosition(), endNode.getPosition()));
			for(Node node : selectedNodes) {
				blockAroundSelected.add(node);
			}
			blockAroundSelected.setPosition(NodeFactory.unionPositions(NodeProvider.unwrap(beginNode).getPosition(), NodeProvider.unwrap(endNode).getPosition()));
			return blockAroundSelected;
		} else if (beginNode.equals(endNode)) {
			return beginNode;
		}
		return enclosingNode;
	}


	public static boolean isNodeContainedInNode(Node containedNode, Node containingNode) {
		return (nodeContainsPosition(containingNode, containedNode.getPosition().getStartOffset()) 
				&& nodeContainsPosition(containingNode, containedNode.getPosition().getEndOffset()));
	}

	private static Collection<Node> getNodesFromTo(Node beginNode, Node endNode, Collection<Node> allNodes) {
		Collection<Node> affectedNodes = new ArrayList<Node>();
		boolean between = false;
		for (Node aktNode : allNodes) {
			if (aktNode.equals(beginNode))
				between = true;
			if (between) {
				affectedNodes.add(aktNode);
			}
			if (aktNode.equals(endNode))
				between = false;
		}
		return affectedNodes;
	}

	private static Node getEnclosingNode(Node enclosedNode, Collection<Node> possibleEnclosingNodes) {
		for (Node aktNode : possibleEnclosingNodes) {
			if (nodeEnclosesNode(aktNode, enclosedNode))
				return aktNode;
		}
		return null;
	}

	private static boolean nodeEnclosesNode(Node enclosingNode, Node enclosedNode) {
		ISourcePosition enclosingPos = enclosingNode.getPosition();
		ISourcePosition enclosedPos = enclosedNode.getPosition();
		return (enclosingPos.getStartOffset() <= enclosedPos.getStartOffset() && enclosingPos.getEndOffset() >= enclosedPos.getEndOffset());
	}

	public static Node getEnclosingNode(Node rootNode, SelectionInformation selection, Class<?>... classes) {
		Collection<Node> enclosingNodes = getEnclosingNodes(rootNode, selection, classes);
		Node lastNode = null;
		Node secondLastNode = null;
		Node thirdLastNode = null;
		for (Node aktNode : enclosingNodes) {
			thirdLastNode = secondLastNode;
			secondLastNode = lastNode;
			lastNode = aktNode;
		}
		if (thirdLastNode != null && sameStartPosAndIsVariableNode(thirdLastNode, lastNode) && hasSamePosAndIsSelfAsignment(secondLastNode, thirdLastNode)) {
			return thirdLastNode;
		}

		if (secondLastNode != null && hasSamePosAndIsSelfAsignment(lastNode, secondLastNode)) {
			return secondLastNode;
		}

		return lastNode;
	}

	private static boolean sameStartPosAndIsVariableNode(Node firstNode, Node secondNode) {
		Class[] classes = { LocalAsgnNode.class, LocalVarNode.class, DAsgnNode.class, DVarNode.class, InstAsgnNode.class, InstVarNode.class,
				ClassVarAsgnNode.class, ClassVarNode.class, GlobalAsgnNode.class, GlobalVarNode.class};
		boolean sameStart = firstNode.getPosition().getStartOffset() == secondNode.getPosition().getStartOffset();
		boolean isFirstNodeVarNode = NodeProvider.nodeAssignableFrom(firstNode, classes);
		boolean isSecondNodeVarNode = NodeProvider.nodeAssignableFrom(secondNode, classes);
		return sameStart && isFirstNodeVarNode && isSecondNodeVarNode;
	}

	private static boolean hasSamePosAndIsSelfAsignment(Node probablyCallNode, Node probablyAsgnNode) {
		boolean sameStart = probablyCallNode.getPosition().getStartOffset() == probablyAsgnNode.getPosition().getStartOffset();
		boolean sameEnd = probablyCallNode.getPosition().getEndOffset() == probablyAsgnNode.getPosition().getEndOffset();
		boolean isAsgnNode = NodeProvider.nodeAssignableFrom(probablyAsgnNode, LocalAsgnNode.class, DAsgnNode.class, InstAsgnNode.class, ClassVarAsgnNode.class);
		boolean isCallNode = NodeProvider.nodeAssignableFrom(probablyCallNode, CallNode.class, AttrAssignNode.class);
		return sameStart && sameEnd && isCallNode && isAsgnNode;
	}

	public static Collection<Node> getEnclosingNodes(Node rootNode, SelectionInformation selection, Class<?>... classes) {
		Collection<Node> allNodes = NodeProvider.getAllNodes(rootNode);
		Collection<Node> enclosingStartNodes = getSelectedNodesOfType(allNodes, selection.getStartOfSelection(), classes);
		Collection<Node> enclosingNodes = new ArrayList<Node>();
		for (Node aktNode : enclosingStartNodes) {
			if (nodeContainsPosition(aktNode, selection.getEndOfSelection())) {
				enclosingNodes.add(aktNode);
			} else {
				break;
			}
		}
		return enclosingNodes;
	}

	public static Node getSelectedNodeOfType(Node baseNode, int position, Class<?>... klasses) {
		return getSelectedNodeOfType(NodeProvider.getAllNodes(baseNode), position, klasses);
	}

	public static Node getSelectedNodeOfType(Collection<? extends Node> nodes, int position, Class<?>... klasses) {
		return returnLast(getSelectedNodesOfType(nodes, position, klasses));
	}


	private static Node returnLast(Collection<Node> candidates) {
		if (candidates.size() <= 0)
			return null;

		Node candidate = candidates.toArray(new Node[candidates.size()])[0];
		for (Node node : candidates) {
			if(node.getPosition().getEndOffset() <= candidate.getPosition().getEndOffset()) {
				candidate = node;
			}
		}
		
		// we assume that the last element in the list is the most accurate
		return candidate;
	}

	public static Collection<Node> getSelectedNodesOfType(Collection<? extends Node> nodes, int position, Class<?>... klasses) {
		ArrayList<Node> candidates = new ArrayList<Node>();
		for (Node n : nodes) {
			if (nodeContainsPosition(n, position) && !(n instanceof NewlineNode) && NodeProvider.nodeAssignableFrom(n, klasses)) {
				candidates.add(n);
			}
		}
		return candidates;
	}

	public static Collection<Node> getSelectedNodesOfType(Node baseNode, int position, Class<?>... klasses) {
		return getSelectedNodesOfType(NodeProvider.getAllNodes(baseNode), position, klasses);
	}

	public static boolean nodeContainsPosition(Node n, int position) {
		return (position + CURSOR_TOLERANCE >= n.getPosition().getStartOffset() && position - CURSOR_TOLERANCE < n.getPosition().getEndOffset());
	}

	public static String[] localNamesFromLocalAsgnNodes(Collection<LocalAsgnNode> nodes) {
		Vector<String> names = new Vector<String>();
		names.setSize(nodes.size() + 2);

		names.set(0, "_");
		names.set(1, "~");
		for (LocalAsgnNode n : nodes) {
			if(n.getIndex() >= names.size()) {
				names.setSize(n.getIndex() + 1);
			}
			names.set(n.getIndex(), n.getName());
		}
		return names.toArray(new String[names.size()]);
	}

	public static final int CURSOR_TOLERANCE = 1;

	public static ClassNodeWrapper getSelectedClassNode(Node rootNode, int position) throws NoClassNodeException {
		return getSelectedClassNode(rootNode, position, SClassNode.class, ClassNode.class);
		
	}

	private static ClassNodeWrapper getSelectedClassNode(Node rootNode, int position, Class<?>... classes) throws NoClassNodeException {
		
		Node enclosingClassNode = getSelectedNodeOfType(rootNode, position, classes);
		PartialClassNodeWrapper partialClassNode = PartialClassNodeWrapper.getPartialClassNodeWrapper(enclosingClassNode, rootNode);

		ArrayList<ModuleNode> moduleNodes = new ArrayList<ModuleNode>();
		ModuleNode moduleNode = (ModuleNode) SelectionNodeProvider.getSelectedNodeOfType(rootNode, position, ModuleNode.class);
		if (moduleNode != null) {
			moduleNodes.add(moduleNode);
			partialClassNode.setEnclosingModules(moduleNodes);
		}
		return new ClassNodeWrapper(partialClassNode);
	}

	public static AttrAccessorNodeWrapper getSelectedAccessorNode(Node baseNode, INameNode selectedAccessorNameNode) {

		Collection<AttrAccessorNodeWrapper> accessorNodes = NodeProvider.getAccessorNodes(baseNode);
		String selectedName = selectedAccessorNameNode.getName();
		if (selectedName.charAt(0) == '@') {
			selectedName = selectedName.substring(1);
		}

		AttrAccessorNodeWrapper selectedAccessor = null;
		for (AttrAccessorNodeWrapper aktAccessorNode : accessorNodes) {
			if (aktAccessorNode.getAttrName().equals(selectedName)) {
				if (selectedAccessor == null) {
					selectedAccessor = aktAccessorNode;
				} else {
					selectedAccessor.addAccessorType(aktAccessorNode);
				}
			}
		}
		return selectedAccessor;
	}
}
