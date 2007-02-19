package org.rubypeople.rdt.internal.codeassist;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.CallNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.Node;
import org.jruby.ast.VCallNode;
import org.jruby.ast.types.INameNode;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.ti.DefaultTypeInferrer;
import org.rubypeople.rdt.internal.ti.ITypeGuess;
import org.rubypeople.rdt.internal.ti.ITypeInferrer;
import org.rubypeople.rdt.internal.ti.util.OffsetNodeLocator;

public class SelectionEngine {

	public IRubyElement[] select(IRubyScript script, int start, int end)
			throws RubyModelException {
		String source = script.getSource();
		RubyParser parser = new RubyParser();
		Node root = parser.parse((IFile) script.getResource(),
				new StringReader(source));
		Node selected = OffsetNodeLocator.Instance().getNodeAtOffset(root,
				start);

		if (selected instanceof ConstNode) {
			ConstNode constNode = (ConstNode) selected;
			String name = constNode.getName();
			// Try to find a matching constant in this script
			IRubyElement element = findChild(name, IRubyElement.CONSTANT, script);
			if (element != null) {
			  return new IRubyElement[] { element };	
			}
			// Now search for a type in this script
			element = findChild(name, IRubyElement.TYPE, script);
			if (element != null) {
			  return new IRubyElement[] { element };	
			}
			RubyElementRequestor completer = new RubyElementRequestor(script);
			return completer.findType(name);
		}
		if (isLocalVarRef(selected)) {
			// TODO Try the local namespace first!			
			List<IRubyElement> possible = getChildrenWithName(script
					.getChildren(), IRubyElement.LOCAL_VARIABLE,
					getName(selected));
			return convertToArray(possible);
		}
		if (isInstanceVarRef(selected)) {
			List<IRubyElement> possible = getChildrenWithName(script
					.getChildren(), IRubyElement.INSTANCE_VAR,
					getName(selected));
			return convertToArray(possible);
		}
		if (isClassVarRef(selected)) {
			List<IRubyElement> possible = getChildrenWithName(script
					.getChildren(), IRubyElement.CLASS_VAR, getName(selected));
			return convertToArray(possible);
		}
		if (isMethodCall(selected)) {
			List<IRubyElement> possible = new ArrayList<IRubyElement>();
			ITypeInferrer inferrer = new DefaultTypeInferrer();
			List<ITypeGuess> guesses = inferrer.infer(source, start);
			RubyElementRequestor requestor = new RubyElementRequestor(script);
			for (ITypeGuess guess : guesses) {
				String name = guess.getType();
				IType[] types = requestor.findType(name);
				for (int i = 0; i < types.length; i++) {
					IType type = types[i];
					IMethod[] methods = type.getMethods();
					for (int j = 0; i < methods.length; j++) {
					  if (methods[j].getElementName().equals(getName(selected))) {
						  possible.add(methods[j]);
					  }
					}
				}
			}
			return convertToArray(possible);
		}
		return new IRubyElement[0];
	}

	private IRubyElement findChild(String name, int type,
			IParent parent) {
		try {
			IRubyElement[] children = parent.getChildren();
			for (int j = 0; j < children.length; j++) {
				IRubyElement child = children[j];
				if (child.getElementName().equals(name) && child.isType(type))
				  return child;
			    if (child instanceof IParent) {
			    	IRubyElement found = findChild(name, type, (IParent) child);
			    	if (found != null) return found;
			    }
			}
		} catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private boolean isMethodCall(Node selected) {
		return (selected instanceof VCallNode) || (selected instanceof FCallNode) || (selected instanceof CallNode);
	}

	private IRubyElement[] convertToArray(List<IRubyElement> possible) {
		IRubyElement[] elements = new IRubyElement[possible.size()];
		int x = 0;
		for (IRubyElement element : possible) {
			elements[x++] = element;
		}
		return elements;
	}

	private List<IRubyElement> getChildrenWithName(IRubyElement[] children,
			int type, String name) throws RubyModelException {
		List<IRubyElement> possible = new ArrayList<IRubyElement>();
		for (int i = 0; i < children.length; i++) {
			IRubyElement child = children[i];
			if (child.getElementType() == type) {
				if (child.getElementName().equals(name))
					possible.add(child);
			}
			if (child instanceof IParent) {
				possible.addAll(getChildrenWithName(((IParent) child)
						.getChildren(), type, name));
			}
		}
		return possible;
	}

	private String getName(Node node) {
		if (node instanceof INameNode) {
			return ((INameNode) node).getName();
		}
		if (node instanceof ClassVarNode) {
			return ((ClassVarNode) node).getName();
		}
		return "";
	}

	private boolean isInstanceVarRef(Node node) {
		return ((node instanceof InstAsgnNode) || (node instanceof InstVarNode));
	}

	private boolean isClassVarRef(Node node) {
		return ((node instanceof ClassVarAsgnNode) || (node instanceof ClassVarNode)|| (node instanceof ClassVarDeclNode));
	}

	private boolean isLocalVarRef(Node node) {
		return ((node instanceof LocalAsgnNode)
				|| (node instanceof ArgumentNode) || (node instanceof LocalVarNode));
	}
}
