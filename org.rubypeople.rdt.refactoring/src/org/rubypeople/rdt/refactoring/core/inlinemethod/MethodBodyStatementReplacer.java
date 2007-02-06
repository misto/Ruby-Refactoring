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

package org.rubypeople.rdt.refactoring.core.inlinemethod;

import java.util.ArrayList;
import java.util.Collection;

import org.jruby.ast.FCallNode;
import org.jruby.ast.Node;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.VCallNode;
import org.rubypeople.rdt.refactoring.classnodeprovider.IncludedClassesProvider;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.documentprovider.StringDocumentProvider;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentProvider;
import org.rubypeople.rdt.refactoring.nodewrapper.MethodNodeWrapper;

public class MethodBodyStatementReplacer implements IMethodBodyStatementReplacer {

	public DocumentProvider replaceSelfWithObject(final DocumentProvider doc, final String object) {
		Collection<Node> selfNodes = null;
		DocumentProvider result = new StringDocumentProvider(doc);
		do {
			selfNodes = NodeProvider.gatherNodesOfTypeInAktScopeNode(result.getRootNode().getBodyNode(), SelfNode.class);
			if(selfNodes.isEmpty()) {
				continue;
			}
			final SelfNode node = (SelfNode) selfNodes.iterator().next();
			StringBuilder tempResult = new StringBuilder();
			tempResult.append(result.getActiveFileContent().substring(0, node.getPosition().getStartOffset()));
			tempResult.append(object);
			tempResult.append(result.getActiveFileContent().substring(node.getPosition().getEndOffset()));
			result = new StringDocumentProvider(tempResult.toString());
			
		} while(!selfNodes.isEmpty());
		
		return result;
	}

	public DocumentProvider prefixCallsWithObject(DocumentProvider doc, IncludedClassesProvider provider, String className, String object) {
		
		DocumentProvider result = new StringDocumentProvider(doc);
		
		IMethodCallNode call = null;
		while((call = findCallToMethodInClass(result, provider, className)) != null) {
			StringBuilder src = new StringBuilder(result.getActiveFileContent());
			src.insert(call.getNode().getPosition().getStartOffset(), object + '.');
			result = new StringDocumentProvider(src.toString());
		}
		
		return result;
	}
	

	private IMethodCallNode findCallToMethodInClass(DocumentProvider doc, IncludedClassesProvider provider, String className) {
		Collection<MethodNodeWrapper> definedMethods = provider.getAllMethodsFor(className); 

		for (IMethodCallNode node : findFAndVCalls(doc)) {
			for (MethodNodeWrapper methods : definedMethods) {
				if(methods.getName().equals(node.getName())) {
					return node;
				}
			}
		}
		return null;
	}

	private Collection<IMethodCallNode> findFAndVCalls(DocumentProvider doc) {
		Collection<IMethodCallNode> methodCalls = new ArrayList<IMethodCallNode>();
		for (Node node : NodeProvider.gatherNodesOfTypeInAktScopeNode(doc.getRootNode().getBodyNode(), VCallNode.class, FCallNode.class)) {
			methodCalls.add(MethodCallNodeFactory.create(node));
		}
		return methodCalls;
	}

	public DocumentProvider removeReturnStatements(DocumentProvider doc) {
		Collection<Node> nodes = null;
		StringDocumentProvider result = new StringDocumentProvider(doc);
		do {
			nodes = NodeProvider.getSubNodes(NodeProvider.getRootNode("", result.getActiveFileContent()), ReturnNode.class);
			if(nodes.isEmpty()) break;
			StringBuilder newBody = new StringBuilder(result.getActiveFileContent());
			int startOffset = nodes.iterator().next().getPosition().getStartOffset();
			newBody.replace(startOffset, startOffset + "return ".length(), "");
			result = new StringDocumentProvider(newBody.toString());
		} while(!nodes.isEmpty());
		
		return result;
	}
}
