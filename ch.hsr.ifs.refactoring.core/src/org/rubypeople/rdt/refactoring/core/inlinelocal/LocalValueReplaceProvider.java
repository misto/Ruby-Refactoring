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

package org.rubypeople.rdt.refactoring.core.inlinelocal;


import org.jruby.ast.CallNode;
import org.jruby.ast.Node;
import org.jruby.ast.visitor.rewriter.DefaultFormatHelper;
import org.jruby.ast.visitor.rewriter.FormatHelper;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.editprovider.ReplaceEditProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.LocalNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.MethodCallNodeWrapper;
import org.rubypeople.rdt.refactoring.util.JRubyRefactoringUtils;
import org.rubypeople.rdt.refactoring.util.NodeUtil;

public class LocalValueReplaceProvider extends ReplaceEditProvider {

	private LocalNodeWrapper targetNode;

	private LocalNodeWrapper inlinedNode;

	private boolean addBrackets;

	private InlineLocalConfig config;

	public LocalValueReplaceProvider(LocalNodeWrapper targetNode, InlineLocalConfig config, boolean addBrackets) {
		super(false);
		this.config = config;
		this.inlinedNode = config.getDefinitionNode();
		this.targetNode = targetNode;
		this.addBrackets = addBrackets;
	}

	@Override
	protected int getOffsetLength() {
		ISourcePosition replacePos = targetNode.getWrappedNode().getPosition();
		return replacePos.getEndOffset() - replacePos.getStartOffset();
	}

	@Override
	protected Node getEditNode(int offset, String document) {
		return inlinedNode.getValueNode();
	}

	@Override
	protected int getOffset(String document) {
		return targetNode.getWrappedNode().getPosition().getStartOffset();
	}

	protected String getFormatedNode(String document) {
		if(addBrackets) {
			return '(' + super.getFormatedNode(document) + ')';
		}
		return super.getFormatedNode(document);
	}

	@Override
	protected FormatHelper getFormatHelper() {
		if(callNeedsBrackets()) {
			return new DefaultFormatHelper() {

				@Override
				public String afterCallArguments() {
					return ")";
				}

				@Override
				public String beforeCallArguments() {
					return "(";
				}};
		} else {
			return super.getFormatHelper();
		}
	}

	private boolean callNeedsBrackets() {
		Node targetEnclosingNode = NodeProvider.findParentNode(config.getDocumentProvider().getRootNode(),	targetNode.getWrappedNode());
		boolean isTargetEnclosingNodeCallNode = NodeUtil.nodeAssignableFrom(targetEnclosingNode, MethodCallNodeWrapper.METHOD_CALL_NODE_CLASSES());
		if(NodeUtil.nodeAssignableFrom(targetEnclosingNode, CallNode.class)) {
			isTargetEnclosingNodeCallNode &= !JRubyRefactoringUtils.isMathematicalExpression(targetEnclosingNode);
		}
		boolean isInlinedNodeCallNode = NodeUtil.nodeAssignableFrom(inlinedNode.getValueNode(), MethodCallNodeWrapper.METHOD_CALL_NODE_CLASSES());
		if(NodeUtil.nodeAssignableFrom(inlinedNode.getValueNode(), CallNode.class)) {
			isInlinedNodeCallNode &= !JRubyRefactoringUtils.isMathematicalExpression(inlinedNode.getValueNode());
		}
		return isTargetEnclosingNodeCallNode && isInlinedNodeCallNode;
	}
}
