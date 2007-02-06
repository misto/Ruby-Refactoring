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

package org.rubypeople.rdt.refactoring.core.pushdown;

import java.util.ArrayList;
import java.util.Collection;

import org.jruby.ast.BlockNode;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.SourcePosition;
import org.rubypeople.rdt.refactoring.core.NodeFactory;
import org.rubypeople.rdt.refactoring.editprovider.InsertEditProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.MethodNodeWrapper;
import org.rubypeople.rdt.refactoring.offsetprovider.IOffsetProvider;

public class DownPushedMethodsClass extends InsertEditProvider {
	private Collection<MethodNodeWrapper> methodNodes;

	private Collection<MethodNodeWrapper> constructorNodes;

	private String className;

	public DownPushedMethodsClass(String className, Collection<MethodNodeWrapper> allMethodNodes) {
		super(true);
		this.className = className;
		initConstrucorAndMethodNodes(allMethodNodes);
	}

	private void initConstrucorAndMethodNodes(Collection<MethodNodeWrapper> allMethodNodes) {
		methodNodes = new ArrayList<MethodNodeWrapper>();
		constructorNodes = new ArrayList<MethodNodeWrapper>();
		for (MethodNodeWrapper node : allMethodNodes) {
			if (node.getSignature().isConstructor())
				constructorNodes.add(node);
			else
				methodNodes.add(node);
		}
	}

	@Override
	protected BlockNode getInsertNode(int offset, String document) {
		if (firstEditInGroup) {
			setInsertType(INSERT_AT_BEGIN_OF_LINE);
		}
		boolean needsNewLineAtEndOfBlock = lastEditInGroup && !isNextLineEmpty(offset, document);
		Node classNode = getClassNode();

		BlockNode blockNode = NodeFactory.createBlockNode();
		blockNode.add(classNode);
		if (!firstEditInGroup)
			blockNode.add(NodeFactory.createNewLineNode(null));
		if (needsNewLineAtEndOfBlock)
			blockNode.add(NodeFactory.createNewLineNode(null));
		return blockNode;
	}

	private Node getClassNode() {
		return NodeFactory.createNewLineNode(NodeFactory.createClassNode(className, getBody()));
	}

	private Node getBody() {
		BlockNode body = new BlockNode(new SourcePosition());
		body.add(NodeFactory.createNewLineNode(null));
		for (MethodNodeWrapper constructor : constructorNodes) {
			body.add(NodeFactory.createNewLineNode(constructor.getWrappedNode()));
		}
		for (MethodNodeWrapper method : methodNodes) {
			body.add(NodeFactory.createNewLineNode(method.getWrappedNode()));
		}
		body.add(NodeFactory.createNewLineNode(null));
		return body;
	}

	@Override
	protected int getOffset(String document) {
		IOffsetProvider offsetProvider = new NewClassOffsetProvier();
		return offsetProvider.getOffset();
	}
}