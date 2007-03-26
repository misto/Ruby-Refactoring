package org.rubypeople.rdt.internal.ti;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.text.Position;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.BlockNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DVarNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.ReturnNode;
import org.jruby.ast.SymbolNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.jruby.lexer.yacc.SourcePosition;
import org.jruby.lexer.yacc.SyntaxException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.util.ASTUtil;
import org.rubypeople.rdt.internal.ti.util.FirstPrecursorNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ti.util.OffsetNodeLocator;
import org.rubypeople.rdt.internal.ti.util.ScopedNodeLocator;

/**
 * Implements "Mark Occurences" feature
 * 
 * @author Jason Morrison
 * 
 */
public class DefaultOccurrencesFinder extends AbstractOccurencesFinder {

	// Root of the document to search
	private Node root;

	// Originating node; corresponds to cursor selection
	private Node orig;

	// Original source
	private String source;

	public String initialize(String source, int offset, int length) {
		if (source == null) {
			return null;
		}

		this.source = source;
		try {
			RubyParser rubyParser = new RubyParser();
			this.root = rubyParser.parse(source);
			if (this.root == null) {
				return null;
			}
		}
		// TODO: Is there anything else the parsing could choke on that should
		// be silently ignored with no markings?
		catch (SyntaxException se) {
			this.root = null;
			return null;
		}
		this.orig = OffsetNodeLocator.Instance().getNodeAtOffset(root, offset);
		if (orig == null) {
			return null;
		}
		if (orig.getPosition().getEndOffset() > offset + length) {
			// Selection spans nodes; not handling that for now.
			return "Selection spans nodes; can only search for a single node.";
		}

		return null;
	}

	/**
	 * Determines the kind of originating node, and collects occurrences
	 * accordingly
	 */
	public List<Position> perform() {
		// Mark no occurrences if root is null (AST couldn't be parsed
		// correctly.)
		if (root == null)
			return new LinkedList<Position>();
		if (orig == null)
			return new LinkedList<Position>();

		// occurrences to return
		List<ISourcePosition> occurrences = new LinkedList<ISourcePosition>();

		if (fMarkLocalVariableOccurrences && isLocalVarRef(orig)) {
			pushLocalVarRefs(root, orig, occurrences);
		}

		if (fMarkLocalVariableOccurrences && isDVarRef(orig)) {
			pushDVarRefs(root, orig, occurrences);
		}

		// XXX: Add pref for instvars
		if (fMarkLocalVariableOccurrences && isInstanceVarRef(orig)) {
			pushInstVarRefs(root, orig, occurrences);
		}

		// XXX: Add pref for classvars
		if (fMarkLocalVariableOccurrences && isClassVarRef(orig)) {
			pushClassVarRefs(root, orig, occurrences);
		}

		// XXX: Add pref for global vars
		if (fMarkLocalVariableOccurrences && isGlobalVarRef(orig)) {
			pushGlobalVarRefs(root, orig, occurrences);
		}

		// XXX: Add pref for symbols
		if (fMarkConstantOccurrences && orig instanceof SymbolNode) {
			pushSymbolRefs(root, orig, occurrences);
		}

		// if ( isMethodRefNode(orig)) {
		// pushMethodRefs( root, orig, occurrences );
		// }

		if (fMarkConstantOccurrences && isConstRef(orig)) {
			pushConstRefs(root, orig, occurrences);
		}

		if (fMarkTypeOccurrences && isTypeRef(orig)) {
			pushTypeRefs(root, orig, occurrences);
		}
		
		if (fMarkMethodExitPoints) {
			pushReturns(root, orig, occurrences);
		}

		// Convert ISourcePosition to IPosition
		List<Position> positions = new LinkedList<Position>();
		for (ISourcePosition occurrence : occurrences) {
			Position position = new Position(occurrence.getStartOffset(), occurrence.getEndOffset() - occurrence.getStartOffset());
			positions.add(position);
		}

		// Uniqueify positions
		positions = new LinkedList<Position>(new HashSet<Position>(positions));

		return positions;
	}

	// ****************************************************************************
	// *
	// * Reference kind definitions
	// *
	// ****************************************************************************

	/**
	 * Determines whether a given node is a local variable reference
	 * 
	 * @param node
	 * @return
	 */
	private boolean isLocalVarRef(Node node) {
		return ((node instanceof LocalAsgnNode) || (node instanceof ArgumentNode) || (node instanceof LocalVarNode));
	}

	/**
	 * Determines whether a given node is a dynamic variable reference
	 * 
	 * @param node
	 * @return
	 */
	private boolean isDVarRef(Node node) {
		return ((node instanceof DVarNode) || (node instanceof DAsgnNode));
	}

	/**
	 * Determines whether a given node is an instance variable reference
	 * 
	 * @param node
	 * @return
	 */
	private boolean isInstanceVarRef(Node node) {
		return ((node instanceof InstAsgnNode) || (node instanceof InstVarNode));
	}

	/**
	 * Determines whether a given node is a class variable reference
	 * 
	 * @param node
	 * @return
	 */
	private boolean isClassVarRef(Node node) {
		return ((node instanceof ClassVarNode) || (node instanceof ClassVarAsgnNode) || (node instanceof ClassVarDeclNode));
	}

	/**
	 * Determines whether a given node is a global variable reference
	 * 
	 * @param node
	 * @return
	 */
	private boolean isGlobalVarRef(Node node) {
		return ((node instanceof GlobalAsgnNode) || (node instanceof GlobalVarNode));
	}

	/**
	 * Determines whether a given node is a constant reference (constant)
	 * 
	 * @param node
	 * @return
	 */
	private boolean isConstRef(Node node) {
		return (node instanceof ConstNode);
	}

	/**
	 * Determines whether a given node is a type reference (class, module)
	 * 
	 * @param node
	 * @return
	 */
	private boolean isTypeRef(Node node) {
		// TODO: Classes can be referred to as a ConstNode; i.e. "class
		// Klass;end; k = Klass.new" the last reference is a ConstNode, not a
		// ClassNode. Special way to handle this?
		return ((node instanceof ClassNode) || (node instanceof ModuleNode) || (node instanceof ConstNode));
	}

	// ****************************************************************************
	// *
	// * Worker methods - handles delegation of occurrence searches
	// *
	// ****************************************************************************

	/**
	 * Collects all corresponding local variable occurrences
	 * 
	 * @param root
	 *            Root node to search
	 * @param orig
	 *            Originating node
	 * @param occurrences
	 */
	private void pushLocalVarRefs(Node root, Node orig, List<ISourcePosition> occurrences) {
		// System.out.println("Finding occurrences for a local variable " +
		// orig.toString());

		// Find the search space
		Node searchSpace = FirstPrecursorNodeLocator.Instance().findFirstPrecursor(root, orig.getPosition().getStartOffset(), new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ((node instanceof DefnNode) || (node instanceof DefsNode) ); // TODO: Block Body?
			}
		});

		// If no enclosing node found, search the entire space
		if (searchSpace == null) {
			searchSpace = root;
		}

		// Finalize searchSpace because Java's scoping rules are the awesome
		final Node finalSearchSpace = searchSpace;

		// Get name of local variable reference
		final String origName = ASTUtil.getNameReflectively(orig);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(searchSpace, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				String name = ASTUtil.getNameReflectively(node);
				return (name != null && name.equals(origName));
			}
		});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(getPositionOfName(searchResult, searchSpace));
		}
	}

	/**
	 * Collects all corresponding dynamic variable occurrences
	 * 
	 * @param root
	 *            Root node to search
	 * @param orig
	 *            Originating node
	 * @param occurrences
	 */
	private void pushDVarRefs(Node root, Node orig, List<ISourcePosition> occurrences) {
		// System.out.println("Finding occurrences for a local variable " +
		// orig.toString());

		// Find the search space
		Node searchSpace = FirstPrecursorNodeLocator.Instance().findFirstPrecursor(root, orig.getPosition().getStartOffset(), new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ((node instanceof DefnNode) || (node instanceof DefsNode) ); // TODO: Block Body?
			}
		});

		// If no enclosing node found, search the entire space
		if (searchSpace == null) {
			searchSpace = root;
		}

		// Get name of local variable reference
		final String origName = ASTUtil.getNameReflectively(orig);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(searchSpace, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				if (isDVarRef(node)) {
					String name = ASTUtil.getNameReflectively(node);
					return (name != null && name.equals(origName));
				}
				return false;
			}
		});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(getPositionOfName(searchResult, searchSpace));
		}
	}

	/**
	 * Collects all instance variable occurrences
	 * 
	 * @param root
	 * @param orig
	 * @param occurrences
	 */
	private void pushInstVarRefs(Node root, Node orig, List<ISourcePosition> occurrences) {
		// System.out.println("Finding occurrences for an instance variable " +
		// orig.toString() );

		Node searchSpace = determineSearchSpace(root, orig);

		// Finalize searchSpace because Java's scoping rules are the awesome
		// todo: not needed?
		// final Node finalSearchSpace = searchSpace;

		// Get name of local variable reference
		final String origName = ASTUtil.getNameReflectively(orig);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(searchSpace, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				if (isInstanceVarRef(node)) {
					String name = ASTUtil.getNameReflectively(node);
					return (name != null && name.equals(origName));
				}
				return false;
			}
		});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(getPositionOfName(searchResult, searchSpace));
		}

	}

	private Node determineSearchSpace(Node root, Node orig) {

		// Find the name of the enclosing class
		ClassNode enclosingClass = (ClassNode) FirstPrecursorNodeLocator.Instance().findFirstPrecursor(root, orig.getPosition().getStartOffset(), new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return (node instanceof ClassNode);
			}
		});

		// If no enclosing class is identified, search root.
		if (enclosingClass == null) {
			return root;
		}
		// Find the search space - all ClassNodes for that name within root
		// scope
		else {
			final String className = getClassNodeName(enclosingClass);
			List<Node> classNodes = ScopedNodeLocator.Instance().findNodesInScope(root, new INodeAcceptor() {
				public boolean doesAccept(Node node) {
					if (node instanceof ClassNode) {
						return getClassNodeName((ClassNode) node).equals(className);
					}
					return false;
				}
			});
			BlockNode blockNode = new BlockNode(new SourcePosition("", 0));
			for (Node classNode : classNodes) {
				blockNode.add(classNode);
			}
			return blockNode;
		}
	}

	/**
	 * Collects all class variable occurrences
	 * 
	 * @param root
	 * @param orig
	 * @param occurrences
	 */
	private void pushClassVarRefs(Node root, Node orig, List<ISourcePosition> occurrences) {
		// System.out.println("Finding occurrences for an instance variable " +
		// orig.toString() );

		Node searchSpace = determineSearchSpace(root, orig);

		// Finalize searchSpace because Java's scoping rules are the awesome
		// todo: not needed?
		// final Node finalSearchSpace = searchSpace;

		// Get name of local variable reference
		final String origName = ASTUtil.getNameReflectively(orig);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(searchSpace, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				if (isClassVarRef(node)) {
					String name = ASTUtil.getNameReflectively(node);
					return (name != null && name.equals(origName));
				}
				return false;
			}
		});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(getPositionOfName(searchResult, searchSpace));
		}

	}

	/**
	 * Collects all global variable occurrences
	 * 
	 * @param root
	 * @param orig
	 * @param occurrences
	 */
	private void pushGlobalVarRefs(Node root, Node orig, List<ISourcePosition> occurrences) {
		final Node searchSpace = root;
		final String origName = ASTUtil.getNameReflectively(orig);

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(searchSpace, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return isGlobalVarRef(node) && ASTUtil.getNameReflectively(node).equals(origName);
			}
		});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(getPositionOfName(searchResult, searchSpace));
		}
	}

	/**
	 * Collects all symbol occurrences
	 * 
	 * @param root
	 * @param orig
	 * @param occurrences
	 */
	private void pushSymbolRefs(Node root, Node orig, List<ISourcePosition> occurrences) {
		final Node searchSpace = root;
		final String origName = ((SymbolNode) orig).getName();

		// Find all pertinent nodes
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(searchSpace, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return (node instanceof SymbolNode) && ((SymbolNode) node).getName().equals(origName);
			}
		});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(getPositionOfName(searchResult, searchSpace));
		}
	}

	// todo: complete
	// private void pushMethodRefs( Node root, Node orig, List<ISourcePosition>
	// occurrences) {
	//	
	// // DefnNode DefsNode CallNode VCallNode
	//		
	// System.out.println("Finding occurrences for method reference node " +
	// orig.toString() );
	//		
	// final Node searchSpace = root;
	// String origName = getMethodRefName(orig);
	//		
	// // If orig is a method definition, find all occurrences to that selector
	// for the orig's enclosing type
	// if ( orig instanceof DefnNode || orig instanceof DefsNode )
	// {
	// ((DefnNode)orig).g
	// }
	//		
	// Node receiver = getMethodReceiver(orig);
	// }

	/**
	 * Collects all pertinent const occurrences
	 */
	private void pushConstRefs(Node root, Node orig, List<ISourcePosition> occurrences) {
		if (!isConstRef(orig)) {
			return;
		}

		final String matchName = ASTUtil.getNameReflectively(orig);
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(root, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				if (isConstRef(node)) {
					return ASTUtil.getNameReflectively(node).equals(matchName);
				}
				return false;
			}
		});

		for (Node searchResult : searchResults) {
			occurrences.add(getPositionOfName(searchResult, root));
		}
	}

	/**
	 * Collects all pertinent type ref occurrences
	 */
	private void pushTypeRefs(Node root, Node orig, List<ISourcePosition> occurrences) {
		if (!isTypeRef(orig)) {
			return;
		}

		final String matchName = ASTUtil.getNameReflectively(orig);
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(root, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				if (isTypeRef(node)) {
					return getTypeRefName(node).equals(matchName);
				}
				return false;
			}
		});

		for (Node searchResult : searchResults) {
			occurrences.add(getPositionOfName(searchResult, root));
		}
	}
	
	private void pushReturns(Node root, Node orig, List<ISourcePosition> occurrences) {
		// TODO Combine most of this stuff with the stuff in pushLocalVareRefs
		// Find the search space
		Node searchSpace = FirstPrecursorNodeLocator.Instance().findFirstPrecursor(root, orig.getPosition().getStartOffset(), new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ((node instanceof DefnNode) || (node instanceof DefsNode) ); // TODO: Block Body?
			}
		});

		// If no enclosing node found, search the entire space
		if (searchSpace == null) {
			searchSpace = root;
		}
//		 Find all return nodes
		List<Node> searchResults = ScopedNodeLocator.Instance().findNodesInScope(searchSpace, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return (node instanceof ReturnNode);
			}
		});

		// Scrape position from pertinent nodes
		for (Node searchResult : searchResults) {
			occurrences.add(searchResult.getPosition());
		}
	}

	// ****************************************************************************
	// *
	// * Utility methods
	// *
	// ****************************************************************************

	/**
	 * Gets the position of the name for the specified node.
	 * 
	 * @param node
	 *            Node that responds to getName() or some variant
	 * @param scope
	 *            Scope that holds the node (pertinent for locals and args)
	 * @return ISourcePosition that holds the name of the node
	 */
	private ISourcePosition getPositionOfName(Node node, Node scope) {
		ISourcePosition pos = node.getPosition();

		// TODO refactor the getting-of-name
		String name = null;
		if (isLocalVarRef(node) || isDVarRef(node) || isInstanceVarRef(node) || isGlobalVarRef(node) || isClassVarRef(node) || isConstRef(node)) {
			name = ASTUtil.getNameReflectively(node);
		} else if (node instanceof ClassNode) {
			name = getClassNodeName((ClassNode) node);
			String classDeclString = source.substring(pos.getStartOffset(), pos.getEndOffset());
			int begin = pos.getStartOffset() + classDeclString.indexOf(name);
			return new SourcePosition(pos.getFile(), pos.getStartLine(), pos.getEndLine(), begin, begin + name.length());
		} else if (node instanceof ModuleNode) {
			name = getModuleNodeName((ModuleNode) node);
			String moduleDeclString = source.substring(pos.getStartOffset(), pos.getEndOffset());
			int begin = moduleDeclString.indexOf(name);
			return new SourcePosition(pos.getFile(), pos.getStartLine(), pos.getEndLine(), begin, begin + name.length());
		} else if (node instanceof SymbolNode) {
			// XXX: This is a hack to get around improper offsets in my JRuby
			// copy; ":foo" returns offset for ":fo", so compensate by adding
			// one
			name = ((SymbolNode) node).getName();
			return new SourcePosition(pos.getFile(), pos.getStartLine(), pos.getEndLine(), pos.getStartOffset(), pos.getStartOffset() + name.length() + 1);
		}

		if (name == null) {
			throw new RuntimeException("Couldn't get the name for: " + node.toString() + " in " + scope.toString());
		}
		return new SourcePosition(pos.getFile(), pos.getStartLine(), pos.getEndLine(), pos.getStartOffset(), pos.getStartOffset() + name.length());
	}

	/**
	 * Helper method to get the class name froma ClassNode
	 * 
	 * @param classNode
	 * @return
	 */
	private String getClassNodeName(ClassNode classNode) {
		if (classNode.getCPath() instanceof Colon2Node) {
			Colon2Node c2node = (Colon2Node) classNode.getCPath();
			return c2node.getName();
		}
		throw new RuntimeException("ClassNode.getCPath() returned other than Colon2Node: " + classNode.toString());
	}

	/**
	 * Helper method to get the class name from a ModuleNode
	 * 
	 * @param classNode
	 * @return
	 */
	private String getModuleNodeName(ModuleNode moduleNode) {
		if (moduleNode.getCPath() instanceof Colon2Node) {
			Colon2Node c2node = (Colon2Node) moduleNode.getCPath();
			return c2node.getName();
		}
		throw new RuntimeException("ModuleNode.getCPath() returned other than Colon2Node: " + moduleNode.toString());
	}

	/**
	 * Helper method to get the class name from a const ref node (Class/Module)
	 * 
	 * @param node
	 * @return
	 */
	private String getTypeRefName(Node node) {
		if (node instanceof ClassNode) {
			return getClassNodeName((ClassNode) node);
		}
		if (node instanceof ModuleNode) {
			return getModuleNodeName((ModuleNode) node);
		}
		return ASTUtil.getNameReflectively(node);
	}

}
