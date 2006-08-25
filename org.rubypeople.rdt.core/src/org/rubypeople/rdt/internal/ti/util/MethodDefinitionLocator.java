package org.rubypeople.rdt.internal.ti.util;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.evaluator.Instruction;
import org.rubypeople.rdt.internal.ti.ITypeGuess;
import org.rubypeople.rdt.internal.ti.ITypeInferrer;
import org.rubypeople.rdt.internal.ti.TypeInferenceHelper;

/**
 * Visitor to find all method definitions within a specific scope.
 * @author Jason Morrison
 */
public class MethodDefinitionLocator extends NodeLocator {

	//Singleton pattern
	private MethodDefinitionLocator() {}
	private static MethodDefinitionLocator staticInstance = new MethodDefinitionLocator();
	public static MethodDefinitionLocator Instance()
	{
		return staticInstance;
	}
	
	/** Inference helper */
	private TypeInferenceHelper helper = TypeInferenceHelper.Instance();

	/** Type of receiver to look for */
	private String typeName;
	
	/** Name of method to search for invocations of */
	private String methodName;
	
	/** Stack of names of types (Class/Module) enclosing the visitor cursor as we traverse */
	private List<String> typeNameStack;
	
	/** Running total of results */
	private List<Node> locatedNodes;
	
	/** Source to search within */
	private String source;
	
	/**
	 * Finds all method definition node within rootNode whose enclosing type is of type typeName and method is named methodName
	 * @param rootNode Node to search within
	 * @param source Source to search within
	 * @param typeName Name of type of method-send-expr receiver
	 * @param methodName Name of method to find
	 * @return
	 */
	public List<Node> findMethodDefinitions( Node rootNode, String source, String typeName, String methodName ) {
		if ( rootNode == null ) { return null; }
		
		this.locatedNodes = new LinkedList<Node>();
		this.typeNameStack = new LinkedList<String>(); 
		this.typeName = typeName;
		this.methodName = methodName;
		
		typeNameStack.add("Kernel");
		
		// Traverse to find all matches
		rootNode.accept(this);
		
		// Return the matches
		return locatedNodes;
	}
	

	public Instruction handleNode(Node iVisited) {
		if ( ( iVisited instanceof DefnNode ) || ( iVisited instanceof DefsNode ) ) {
			if ( peekType().equals(typeName)) {
				String methodName = helper.getMethodDefinitionNodeName( iVisited );
				if ( methodName.equals(methodName) ) {
					locatedNodes.add(iVisited);
				}
			}
		}
		
		return super.handleNode(iVisited);
	}
	
	public Instruction visitClassNode(ClassNode iVisited) {
		pushType( helper.getTypeNodeName( iVisited ) );
		super.visitClassNode( iVisited );
		popType();

		return null;
	}

	public Instruction visitModuleNode(ModuleNode iVisited) {
		pushType( helper.getTypeNodeName( iVisited ) );
		super.visitModuleNode( iVisited );
		popType();

		return null;
	}
	
	private void pushType( String typeName ) {
		typeNameStack.add( 0, typeName );
	}
	
	private void popType() {
		typeNameStack.remove(0);
	}
	
	private String peekType() {
		return typeNameStack.get(0);
	}
}
