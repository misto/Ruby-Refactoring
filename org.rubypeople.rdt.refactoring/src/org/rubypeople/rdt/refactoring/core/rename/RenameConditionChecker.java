package org.rubypeople.rdt.refactoring.core.rename;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.Node;
import org.jruby.ast.SymbolNode;
import org.rubypeople.rdt.refactoring.core.RefactoringConditionChecker;
import org.rubypeople.rdt.refactoring.core.SelectionNodeProvider;
import org.rubypeople.rdt.refactoring.exception.NoClassNodeException;
import org.rubypeople.rdt.refactoring.nodewrapper.ClassNodeWrapper;
import org.rubypeople.rdt.refactoring.nodewrapper.LocalNodeWrapper;

public class RenameConditionChecker extends RefactoringConditionChecker {

	private ClassNode selectedClassNode;
	private Node selectedMethodNode;
	private Node selectedFieldNode;
	private Node selectedLocalNode;
	private Node preferedNode;
	private Node rootNode;
	private int offset;

	public RenameConditionChecker(RenameConfig config) {
		super(config.getDocumentProvider(), config);
	}

	@Override
	protected void checkInitialConditions() {
		if(preferedNode == null) {
			addError("Nothing selected to rename.");
		}
	}

	@Override
	protected void init(Object configObj) {
		RenameConfig config = (RenameConfig) configObj;
		rootNode = config.getDocumentProvider().getRootNode();
		offset = config.getOffset();
		selectedClassNode = (ClassNode) SelectionNodeProvider.getSelectedNodeOfType(rootNode, offset, ClassNode.class);
		selectedMethodNode = SelectionNodeProvider.getSelectedNodeOfType(rootNode, offset, MethodDefNode.class);
		selectedFieldNode = SelectionNodeProvider.getSelectedNodeOfType(rootNode, offset, InstAsgnNode.class, InstVarNode.class);
		selectedLocalNode = SelectionNodeProvider.getSelectedNodeOfType(rootNode, offset, LocalNodeWrapper.getLocalNodeClasses());
		ConstNode selectedConstNode = (ConstNode) SelectionNodeProvider.getSelectedNodeOfType(rootNode, offset, ConstNode.class);
		SymbolNode selectedSymbolNode = (SymbolNode) SelectionNodeProvider.getSelectedNodeOfType(rootNode, offset, SymbolNode.class);
		if(selectedLocalNode == null && SelectionNodeProvider.getSelectedNodeOfType(rootNode, offset, ArgsNode.class) != null) {
			selectedLocalNode = SelectionNodeProvider.getSelectedNodeOfType(rootNode, offset, ArgumentNode.class);
		}
		initPreferedNode(selectedConstNode, selectedSymbolNode);
	}

	private void initPreferedNode(ConstNode selectedConstNode, SymbolNode selectedSymbolNode) {
		if(selectedLocalNode != null) {
			if(selectedFieldNode != null) {
				preferedNode = (SelectionNodeProvider.isNodeContainedInNode(selectedFieldNode, selectedLocalNode)) ? selectedFieldNode : selectedLocalNode;
			} else {
				preferedNode = selectedLocalNode;
			}
		} else if(selectedFieldNode != null) {
			preferedNode = selectedFieldNode;
		} else if(selectedMethodNode != null) {
			preferedNode = selectedMethodNode;
		} else if(selectedClassNode != null) {
			preferedNode = selectedClassNode;
			if(selectedConstNode != null) {
				considerConstNode(selectedConstNode.getName());
			}
			if(selectedSymbolNode != null) {
				considerSymbolNode(selectedSymbolNode);
			}
		}
	}

	private void considerSymbolNode(SymbolNode selectedSymbolNode) {
		try {
			ClassNodeWrapper classNode = SelectionNodeProvider.getSelectedClassNode(rootNode, offset);
		String symbolName = selectedSymbolNode.getName();
		if(classNode.containsField(symbolName)) {
			selectedFieldNode = selectedSymbolNode;
			preferedNode = selectedFieldNode;
		} else if(classNode.containsMethod(symbolName)) {
			selectedMethodNode = selectedSymbolNode;
			preferedNode = selectedSymbolNode;
		}
		} catch (NoClassNodeException e) {/*do nothing*/}
	}

	private void considerConstNode(String constName) {
		if(selectedClassNode != null && constName.equals(selectedClassNode.getCPath().getName())) {
			preferedNode = selectedClassNode;
		}
	}

	public boolean shouldRenameLocal() {
		return testShould(selectedLocalNode);
	}
	
	private boolean testShould(Node nodeToTest) {
		if(preferedNode == null) {
			return false;
		}
		return preferedNode.equals(nodeToTest);
	}

	public boolean shouldRenameField() {
		return testShould(selectedFieldNode);
	}
	
	public boolean shouldRenameMethod() {
		return testShould(selectedMethodNode);
	}
	
	public boolean shouldRenameClass() {
		return testShould(selectedClassNode);
	}
}
