package org.rubypeople.rdt.internal.core.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jruby.ast.ArgsNode;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DStrNode;
import org.jruby.ast.FalseNode;
import org.jruby.ast.FixnumNode;
import org.jruby.ast.HashNode;
import org.jruby.ast.ListNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.NilNode;
import org.jruby.ast.Node;
import org.jruby.ast.ScopeNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.TrueNode;
import org.jruby.ast.ZArrayNode;

public abstract class ASTUtil {
	/**
	 * @param argsNode
	 * @param bodyNode 
	 * @return
	 */
	public static String[] getArgs(Node argsNode, ScopeNode bodyNode) {
		if (argsNode == null) return new String[0];
		ArgsNode args = (ArgsNode) argsNode;
		boolean hasRest = false;
		if (args.getRestArg() != -1)
			hasRest = true;
		
		boolean hasBlock = false;
		if (args.getBlockArgNode() != null)
			hasBlock = true;

		int optArgCount = 0;
		if (args.getOptArgs() != null)
			optArgCount = args.getOptArgs().size();
		List arguments = getArguments(args.getArgs());
		if (optArgCount > 0) {
			arguments.addAll(getArguments(args.getOptArgs()));
		}
		if (hasRest) {
			String restName = "*";
			if (args.getRestArg() != -2) {
				restName += bodyNode.getLocalNames()[args.getRestArg()];
			}
			arguments.add(restName);
		}
		if (hasBlock)
			arguments.add("&" + (String) bodyNode.getLocalNames()[args.getBlockArgNode().getCount()]);
		return stringListToArray(arguments);
	}
	
	private static String[] stringListToArray(List list) {
		String[] array = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			array[i] = (String) list.get(i);
		}
		return array;
	}
	
	private static List getArguments(ListNode argList) {
		if (argList == null) return new ArrayList();
		List arguments = new ArrayList();
		for (Iterator iter = argList.iterator(); iter.hasNext();) {
			Object node = iter.next();
			if (node instanceof ArgumentNode) {
				arguments.add(((ArgumentNode) node).getName());
			} else if (node instanceof LocalAsgnNode) {
				LocalAsgnNode local = (LocalAsgnNode) node;
				String argString = local.getName();
				argString += " = ";
				argString += stringRepresentation(local.getValueNode());
				arguments.add(argString);
			} else {
					System.err
						.println("Reached argument node type we can't handle");
			}
		}
		return arguments;
	}
	
	public static String stringRepresentation(Node node) {
		if (node == null) return "";
		if (node instanceof HashNode)
			return "{}";
		if (node instanceof SelfNode)
			return "self";
		if (node instanceof NilNode)
			return "nil";
		if (node instanceof TrueNode)
			return "true";
		if (node instanceof FalseNode)
			return "false";
		if (node instanceof ConstNode)
			return ((ConstNode)node).getName();
		if (node instanceof ZArrayNode)
			return "[]";
		if (node instanceof FixnumNode)
			return "" + ((FixnumNode) node).getValue();
		if (node instanceof DStrNode)
			return stringRepresentation((DStrNode) node);
		if (node instanceof StrNode)
			return ((StrNode) node).getValue();
		System.err.println("Reached node type we don't know how to represent: "
				+ node.getClass().getName());
		return node.toString();
	}

	private static String stringRepresentation(DStrNode node) {
		List children = node.childNodes();
		StringBuffer buffer = new StringBuffer();
		buffer.append("\"");
		for (Iterator iter = children.iterator(); iter.hasNext();) {
			Node child = (Node) iter.next();
			buffer.append(stringRepresentation(child));
		}
		buffer.append("\"");
		return buffer.toString();
	}

}