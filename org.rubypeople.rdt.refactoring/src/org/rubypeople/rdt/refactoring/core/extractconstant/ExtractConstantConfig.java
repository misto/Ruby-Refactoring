package org.rubypeople.rdt.refactoring.core.extractconstant;

import org.jruby.ast.Node;
import org.rubypeople.rdt.refactoring.core.IRefactoringConfig;
import org.rubypeople.rdt.refactoring.core.NodeFactory;
import org.rubypeople.rdt.refactoring.core.SelectionInformation;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.documentprovider.DocumentProvider;
import org.rubypeople.rdt.refactoring.documentprovider.IDocumentProvider;

public class ExtractConstantConfig implements IRefactoringConfig {

	private IDocumentProvider docProvider;
	private SelectionInformation selectionInfo;
	private Node selectedNodes;
	private Node rootNode;
	private String constName = "CONSTANT"; // FIXME Try to guess a good name from the other side of assignment?

	public ExtractConstantConfig(DocumentProvider docProvider, SelectionInformation selectionInfo) {
		this.docProvider = docProvider;
		this.selectionInfo = optimizeSelection(selectionInfo);
	}

	private SelectionInformation optimizeSelection(SelectionInformation selectionInfo) {
		int start = selectionInfo.getStartOfSelection();
		int end = selectionInfo.getEndOfSelection() + 1;
		String selectedText = docProvider.getActiveFileContent().substring(start, end);
		String trimedSelectionInformation = selectedText.trim();
		start += selectedText.indexOf(trimedSelectionInformation);
		end = start + trimedSelectionInformation.length() - 1;
		return new SelectionInformation(start, end, selectionInfo.getSource());
	}

	public IDocumentProvider getDocumentProvider() {
		return docProvider;
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
		rootNode = getDocumentProvider().getActiveFileRootNode();
		selectedNodes = SelectionNodeProvider.getSelectedNodes(rootNode, getSelection());
	}
	
	public void setDocumentProvider(IDocumentProvider doc) {
		this.docProvider = doc;
	}

}
