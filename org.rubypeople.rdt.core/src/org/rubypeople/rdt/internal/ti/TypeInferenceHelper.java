package org.rubypeople.rdt.internal.ti;

import java.util.Iterator;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.GlobalVarNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.VCallNode;
import org.jruby.lexer.yacc.ISourcePosition;
import org.rubypeople.rdt.internal.ti.util.ScopedNodeLocator;

public class TypeInferenceHelper {

	//Singleton pattern
	private TypeInferenceHelper() {}
	private static TypeInferenceHelper staticInstance = new TypeInferenceHelper();
	public static TypeInferenceHelper Instance()	{
		return staticInstance;
	}

	/**
	 * Extracts the name of a variable from a VarNode
	 * @param source Source that contains the node
	 * @param node LocalVarNode, InstVarNode, or GlobalVarNode referring to a variable.
	 * @return Name of the variable.
	 */
	public String getVarName(String source, Node node)
	{
		ISourcePosition pos = null;
		if ( node instanceof InstVarNode ) pos = ((InstVarNode)node).getPosition();
		if ( node instanceof ClassVarNode ) pos = ((ClassVarNode)node).getPosition();
		if ( node instanceof LocalVarNode ) pos = ((LocalVarNode)node).getPosition();
		if ( node instanceof GlobalVarNode ) pos = ((GlobalVarNode)node).getPosition();
		if ( pos != null )
		{
			return source.substring(pos.getStartOffset(), pos.getEndOffset()+1);
		}
		return null;
	}
	
	
	public int getArgIndex(ListNode listNode, String argName)
	{
		int argNumber = 0;
		for ( Iterator iter = listNode.iterator(); iter.hasNext();) {
			if (((ArgumentNode)iter.next()).getName().equals(argName)) { return argNumber; }
			argNumber++;
		}
		return -1;
	}
	
	public String getTypeNodeName( Node node ) {
		if ( node instanceof ClassNode )  { return ((Colon2Node)((ClassNode)node).getCPath()).getName(); }
		if ( node instanceof ModuleNode ) { return ((Colon2Node)((ModuleNode)node).getCPath()).getName(); }
		return null;
	}

	public String getMethodDefinitionNodeName(Node methodNode) {
		if ( methodNode instanceof DefnNode ) return ((DefnNode)methodNode).getName();
		if ( methodNode instanceof DefsNode ) return ((DefsNode)methodNode).getName();
		return null;
	}
	
	public boolean isArgumentInMethod( String varName, Node enclosingScopeNode ) {
		ListNode listNode = getArgsListNode( enclosingScopeNode );
		
		// If the args node cannot be located, varName is probably not an arg ;)
		if ( listNode == null ) {
			return false;
		}

		// See if the method contains the variable by name
		return ( getArgIndex( listNode, varName ) >= 0 );
	}
	
	public ListNode getArgsListNode( Node node ) {
		if ( node instanceof DefnNode ) { return ((ArgsNode)( ((DefnNode)node).getArgsNode() )).getArgs(); }
		if ( node instanceof DefsNode ) { return ((ArgsNode)( ((DefsNode)node).getArgsNode() )).getArgs(); }
		if ( node instanceof CallNode ) { return ((ArgsNode)( ((CallNode)node).getArgsNode() )).getArgs(); }
		
		//TODO: Is ArrayNode the proper cast?
		if ( node instanceof FCallNode ) { return (ArrayNode)( ((FCallNode)node).getArgsNode() ); }
		// VCallNode is a node w/o args
		
		return null;
	}
	
	
	public String getCallNodeMethodName( Node node ) {
		if ( node instanceof CallNode )  { return ((CallNode)node).getName(); }
		if ( node instanceof FCallNode ) { return ((FCallNode)node).getName(); }
		if ( node instanceof VCallNode ) { return ((VCallNode)node).getMethodName(); }
		return null;		
	}
	
	public Node findNthArgExprInSendExpr( int n, Node sendExprNode ) {
		ListNode listNode = getArgsListNode( sendExprNode );
		if ( listNode == null ) {
			return null; 
		}
		
		
		return listNode.get(n);
	}
	

}
