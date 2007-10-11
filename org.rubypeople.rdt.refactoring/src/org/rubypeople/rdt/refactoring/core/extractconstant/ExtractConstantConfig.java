/**
 * Copyright (c) 2007 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 */
package org.rubypeople.rdt.refactoring.core.extractconstant;

import org.jruby.ast.Node;
import org.jruby.ast.visitor.rewriter.ReWriteVisitor;
import org.rubypeople.rdt.core.RubyConventions;
import org.rubypeople.rdt.refactoring.core.NodeFactory;
import org.rubypeople.rdt.refactoring.core.RefactoringConfig;
import org.rubypeople.rdt.refactoring.core.SelectionInformation;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;

public class ExtractConstantConfig extends RefactoringConfig {

	private static final String DEFAULT_CONSTANT_NAME = "CONSTANT";

	private SelectionInformation selectionInfo;
	private Node selectedNodes;
	private Node rootNode;
	private String constName = DEFAULT_CONSTANT_NAME;

	public ExtractConstantConfig(IDocumentProvider docProvider, SelectionInformation selectionInfo) {
		super(docProvider);
		this.selectionInfo = optimizeSelection(selectionInfo);
	}

	private SelectionInformation optimizeSelection(SelectionInformation selectionInfo) {
		int start = selectionInfo.getStartOfSelection();
		int end = selectionInfo.getEndOfSelection() + 1;
		String selectedText = getDocumentProvider().getActiveFileContent().substring(start, end);
		String trimedSelectionInformation = selectedText.trim();
		start += selectedText.indexOf(trimedSelectionInformation);
		end = start + trimedSelectionInformation.length() - 1;
		return new SelectionInformation(start, end, selectionInfo.getSource());
	}


	public SelectionInformation getSelection() {
		return selectionInfo;
	}

	public Node getSelectedNodes() {
		return selectedNodes;
	}

	public Node getRootNode() {
		return rootNode;
	}

	public Node getConstantCallNode() {
		return NodeFactory.createConstNode(constName);
	}

	public void setConstantName(String name) {
		constName = name;
	}

	public Node getConstantDeclNode() {
		return NodeFactory.createConstDeclNode(constName, selectedNodes);
	}

	public String getConstantName() {
		return constName;
	}

	public void init() {
		rootNode = getDocumentProvider().getRootNode();
		selectedNodes = SelectionNodeProvider.getSelectedNodes(rootNode, getSelection());
		constName = extractConstantName(selectedNodes);
	}
	
	private String extractConstantName(Node node) {
		String name = ReWriteVisitor.createCodeFromNode(node, "");
		name = trim(name);				
		if (RubyConventions.validateConstant(name).isOK())
			return name;
		return DEFAULT_CONSTANT_NAME;
	}

	private String trim(String name) {
		name = name.trim();
		name = name.toUpperCase();
		name = name.replace(' ', '_');
		while(true) { // trim until first uppercase letter
			if (name.length() == 0) break;
			char c = name.charAt(0);
			if (!(Character.isUpperCase(c) && Character.isLetter(c))) {
				name = name.substring(1);
			} else {
				break;
			}
		}
		while(true) { // trim end until letter or underscore
			if (name.length() == 0) break;
			char c = name.charAt(name.length() - 1);
			if (!Character.isLetter(c) && c != '_') {
				name = name.substring(0, name.length() - 1);
			} else {
				break;
			}
		}
		return name;
	}
}
