package org.rubypeople.rdt.internal.ti;

import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.ti.data.ConstNodeTypeNames;
import org.rubypeople.rdt.internal.ti.data.TypicalMethodReturnNames;

public class TypeInferenceVisitor extends InOrderVisitor {
	
	private Scope globalScope;	
	private Scope currentScope;	
	
	// todo: init globalScope to null, push in first non-null node as
	// globalScope
	public TypeInferenceVisitor( Node rootNode ) {
		System.out.println("Instantiating new TypeInferenceVisitor with root node " + stringifyNode(rootNode) );
		globalScope = new Scope( rootNode, null );
		currentScope = globalScope;
	}
	
	
	public Instruction handleNode(Node iVisited) {
		
		if ( iVisited != null )
		{
			String pos = "";
			String cls = "";
			if ( iVisited.getPosition() != null ) pos = Integer.toString(iVisited.getPosition().getStartLine());
			if ( iVisited.getClass() != null )    cls = iVisited.getClass().getName();
//			System.out.println("Visiting " + iVisited.getClass().getSimpleName() + "\tat line " + pos + " of class " + cls );
			System.out.println("  - Spanning " + iVisited.getPosition().getStartOffset() + "-" + iVisited.getPosition().getEndOffset());
		}
		return super.handleNode(iVisited);
	}
	
	/**
	 * Visit a ModuleNode, and extract its local variables from the embedded
	 * body ScopeNode
	 */
	public Instruction visitModuleNode(ModuleNode iVisited) {		
		Scope newScope = pushScope( iVisited );
		Variable.insertLocalsFromScopeNode(iVisited.getScope(), newScope);
		return super.visitModuleNode(iVisited);
	}
	
	/**
	 * Visit a ClassNode, and extract its local variables from the embedded body
	 * ScopeNode
	 */
	public Instruction visitClassNode(ClassNode iVisited) {
		Scope newScope = pushScope( iVisited );
		Variable.insertLocalsFromScopeNode(iVisited.getScope(), newScope);
		return super.visitClassNode(iVisited);
	}
	
	/**
	 * Visit a DefnNode, and extract its local variables from the embedded body
	 * ScopeNode
	 */
	public Instruction visitDefnNode(DefnNode iVisited) {
		Scope newScope = pushScope( iVisited );
		Variable.insertLocalsFromScopeNode(iVisited.getScope(), newScope);
		// todo: insert from argsNodes
		return super.visitDefnNode(iVisited);
	}
	
	/**
	 * Visit a DefsNode, and extract its local variables from the embedded body
	 * ScopeNode
	 */
	public Instruction visitDefsNode(DefsNode iVisited) {
		Scope newScope = pushScope( iVisited );
		Variable.insertLocalsFromScopeNode(iVisited.getScope(), newScope);
		// todo: insert from argsNodes
		return super.visitDefsNode(iVisited);
	}
	
	/**
	 * Visit an IterNode, and extract variable references from it
	 */
	public Instruction visitIterNode(IterNode iVisited) {
		// todo: push iterator var into the iter's scope.
		Scope newScope = pushScope(iVisited);
// newScope.getVariables().add( new Variable( newScope, ))
		// todo: insert from varNode; either DAsgnNode or LocalAsgnNode
		// depending... (see: block local var ambiguity)
		pushScope( iVisited );
		return super.visitIterNode(iVisited);
	}
	
	/**
	 * Pushes a new scope onto the stack based on the specified node.
	 * 
	 * @param node
	 *            Node which signifies the scope being pushed
	 * @return newly pushed Scope
	 */
	private Scope pushScope( Node node ) {
		System.out.println("Pushing Scope for Node: " + stringifyNode(node) );
		Scope newScope = new Scope( node, currentScope );
		currentScope = newScope;
		return newScope;
	}
	
	// todo: how to tell when to do this?
	// todo: perhaps model IndexUpdater rather than InOrderVisitor
	private void popScope()
	{
		currentScope = currentScope.getParentScope();
	}
	
	/**
	 * Used to build STIGuess instances by recording instances of method
	 * invocation against variables.
	 */
	public Instruction visitCallNode(CallNode iVisited) {
		Variable var = getVariableByVarNode( iVisited.getReceiverNode() );
		if ( var != null )
		{
//			System.out.println("Call: " + var.getName() + "." + iVisited.getName() );
			// todo: add call to list
		}
		return super.visitCallNode(iVisited);
	}
	
	/**
	 * Gets a Variable reference by a Node
	 * 
	 * @param node -
	 *            LocalVarNode, InstVarNode, GlobalVarNode, ClassVarNode, or
	 *            DVarNode
	 * @return Variable or null
	 */
	private Variable getVariableByVarNode( Node node )
	{
		// For local variables, search current scope for the variable by count.
		if (node instanceof LocalVarNode) {
			LocalVarNode localVarNode = (LocalVarNode) node;
			return currentScope.getLocalVariableByCount(localVarNode.getIndex());
		}
		// todo: InstVarNode
		// todo: GlobalVarNode
		// todo: ClassVarNode
		// todo: DVarNode
		return null;
	}
	
	/**
	 * Local assignment may provide a concrete type from the rvalue
	 */
	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {
//		System.out.println("Visiting LocalAsgnNode: " + stringifyNode(iVisited));
		Variable var = currentScope.getLocalVariableByCount( iVisited.getIndex() );
		if ( var == null )
		{
			// Local Variable cannot be found... are we in the global scope?
			// (i.e. no localNames given by JRuby)
			if ( currentScope == globalScope )
			{
				// Yes - stick this variable into the global scope.
				// todo: Shouldn't JRuby give a ScopeNode w/ a .getLocalNames()
				// for the global script?
				var = new Variable( globalScope, iVisited.getName(), iVisited.getIndex() );
				currentScope.getVariables().add(var);
			}
		}
		
		System.out.print("Associating a type to Variable " + var.getName() + ": " );
		
		Node valueNode = iVisited.getValueNode();

		// Try seeing if the rvalue is a constant (5, "foo", [1,2,3], etc.)
		String concreteGuess = ConstNodeTypeNames.get(valueNode.getClass().getSimpleName()); 
		if ( concreteGuess != null )
		{
    		var.getTypeGuesses().add( new BasicTypeGuess( concreteGuess, 100 ) );
		}
//    	else if ( valueNode instanceof LocalVarNode ) {
//    		// todo: this method needs to be fixed... see
//			// ReferenceTypeGuess.java
//    		LocalVarNode rhsNode = (LocalVarNode)valueNode;
//    		Variable rhsVar = currentScope.getLocalVariableByCount(rhsNode.getCount());
//    		var.getTypeGuesses().add( new ReferenceTypeGuess( rhsVar ) );
//    	}
		
		// Try seeing if the rvalue is a well-known method call such as 5.to_s or FooClass.new
		else if (valueNode instanceof CallNode)
		{
			CallNode callValueNode = (CallNode)valueNode;
			String method = callValueNode.getName();
			
			// Try ConstNode.new			
			if ( method.equals("new") && callValueNode.getReceiverNode() instanceof ConstNode)
			{
				var.getTypeGuesses().add( new BasicTypeGuess( ((ConstNode)callValueNode.getReceiverNode()).getName() , 100 ) );
			}
			
			else {
				// Try some well-known method from built-in classes.  I.e. 5.to_s yields a String
				String methodReturnTypeGuess = TypicalMethodReturnNames.get(method);
				if ( methodReturnTypeGuess != null )
				{
					var.getTypeGuesses().add( new BasicTypeGuess( methodReturnTypeGuess, 100 ) );
				}
			}
		}

		// Print list of types now assoc'd with the var
		System.out.print("[");
		for ( ITypeGuess guess : var.getTypeGuesses() )
		{
			System.out.print(guess.getType() + ",");
		}
		System.out.print("]");
		
		
    	System.out.println("");
		return super.visitLocalAsgnNode(iVisited);
	}
	
	
	
	
	
	/**
	 * Similar to Node.toString(),. but with the beginning line number.
	 * 
	 * @param node
	 * @return
	 */
	private String stringifyNode(Node node)
	{
		return node.getClass().getName() + "@ :" + node.getPosition().getStartLine();
	}

}
