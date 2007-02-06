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

import org.jruby.ast.AssignableNode;
import org.jruby.ast.Node;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.visitor.rewriter.ReWriteVisitor;
import org.rubypeople.rdt.refactoring.core.NodeProvider;
import org.rubypeople.rdt.refactoring.documentprovider.StringDocumentProvider;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentProvider;

public class ReturnStatementReplacer implements IReturnStatementReplacer {

	public boolean singleReturnOnLastLine(DocumentProvider doc) {

		if(countReturnNodes(doc) > 1) {
			return false;
		} else if (countReturnNodes(doc) == 1) {
			return returnIsOnLastLine(getReturnNode(doc), doc);
		} else {
			return true;
		}
	}
	
	private boolean returnIsOnLastLine(ReturnNode node, DocumentProvider doc) {
		return node.getPosition().getStartLine() == doc.getActiveFileContent().split("\\n").length - 1;
	}

	private int countReturnNodes(DocumentProvider doc) {
		int returnNodes = 0;
		for (Node node : NodeProvider.getAllNodesFromFile(doc.getActiveFileName(), doc.getActiveFileContent())) {
			if(node instanceof ReturnNode) {
				returnNodes++;
			}
		}
		return returnNodes;
	}

	private ReturnNode getReturnNode(DocumentProvider doc) {
		for (Node node : NodeProvider.getAllNodesFromFile(doc.getActiveFileName(), doc.getActiveFileContent())) {
			if(node instanceof ReturnNode) {
				return (ReturnNode) node;
			}
		}
		return null;
	}
	

	public DocumentProvider replaceReturn(DocumentProvider doc, AssignableNode target) {
		
 		if(!singleReturnOnLastLine(doc) || target == null) {
			return null;
		}
		
		StringBuilder result = new StringBuilder();
		ReturnNode returnNode = getReturnNode(doc);
		
		if(returnNode == null) {
			insertLastLineToAssignment(doc, target, result);
		} else {
			replaceReturnStatementWithAssignment(doc, target, result, returnNode);
		}

		return new StringDocumentProvider(result.append(ReWriteVisitor.createCodeFromNode(target, doc.getActiveFileContent())).toString());
		
	}

	private void insertLastLineToAssignment(DocumentProvider doc, AssignableNode target, StringBuilder result) {
		String[] lines = doc.getActiveFileContent().split("\\n");
		target.setValueNode(NodeProvider.getRootNode("", lines[lines.length - 1]).getBodyNode());
		for(int i = 0; i < lines.length - 1; i++) {
			result.append(lines[i]);
			result.append('\n');
		}
	}

	private void replaceReturnStatementWithAssignment(DocumentProvider doc, AssignableNode target, StringBuilder result, ReturnNode returnNode) {
		target.setValueNode(returnNode.getValueNode());
		result.append(doc.getActiveFileContent().substring(0, returnNode.getPosition().getStartOffset()));
	}

}