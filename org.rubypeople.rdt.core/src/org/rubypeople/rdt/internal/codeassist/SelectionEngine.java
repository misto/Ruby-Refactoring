package org.rubypeople.rdt.internal.codeassist;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.jruby.ast.ArgumentNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.LocalVarNode;
import org.jruby.ast.Node;
import org.jruby.ast.types.INameNode;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
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
			// TODO Find constants too (not just types)
			// TODO Search scopes outward!
			IRubyProject[] projects = new IRubyProject[1];
			projects[0] = script.getRubyProject();
			RubyElementRequestor completer = new RubyElementRequestor(projects);
			IType type = completer.findType(name);
			return new IRubyElement[] { type };
		}
		if (isLocalVarRef(selected)) {
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
		return new IRubyElement[0];
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
