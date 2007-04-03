/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_x.x.x/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.internal.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jruby.ast.AliasNode;
import org.jruby.ast.ArrayNode;
import org.jruby.ast.AssignableNode;
import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.ConstDeclNode;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DAsgnNode;
import org.jruby.ast.DStrNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.FCallNode;
import org.jruby.ast.GlobalAsgnNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.IterNode;
import org.jruby.ast.LocalAsgnNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.RootNode;
import org.jruby.ast.SClassNode;
import org.jruby.ast.SelfNode;
import org.jruby.ast.SplatNode;
import org.jruby.ast.StrNode;
import org.jruby.ast.VCallNode;
import org.jruby.evaluator.Instruction;
import org.jruby.runtime.Visibility;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor.FieldInfo;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor.MethodInfo;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor.TypeInfo;
import org.rubypeople.rdt.internal.core.parser.InOrderVisitor;
import org.rubypeople.rdt.internal.core.util.ASTUtil;

/**
 * @author Chris
 * 
 */
public class SourceParser extends InOrderVisitor {

	private static final String EMPTY_STRING = "";
	private static final String PROTECTED = "protected";
	private static final String PRIVATE = "private";
	private static final String PUBLIC = "public";
	private static final String INCLUDE = "include";
	private static final String LOAD = "load";
	private static final String REQUIRE = "require";
	private static final String ALIAS = "alias :";
	private static final String MODULE = "Module";
	private static final String CONSTRUCTOR_NAME = "initialize";
	private static final String NAMESPACE_DELIMETER = "::";
	private static final String OBJECT = "Object";
	private Visibility currentVisibility = Visibility.PUBLIC;
	private boolean inSingletonClass;
	private ISourceElementRequestor requestor;

	/**
	 * 
	 * @param requestor The {@link ISourceElementRequestor} that wants to be notified of the source structure
	 */
	public SourceParser(ISourceElementRequestor requestor) {
		super();
		this.requestor = requestor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jruby.ast.visitor.NodeVisitor#visitClassNode(org.jruby.ast.ClassNode)
	 */
	public Instruction visitClassNode(ClassNode iVisited) {
		// This resets the visibility when opening or declaring a class to
		// public
		currentVisibility = Visibility.PUBLIC;

		TypeInfo typeInfo = new TypeInfo();
		typeInfo.name = getFullyQualifiedName(iVisited.getCPath());
		typeInfo.declarationStart = iVisited.getPosition().getStartOffset();
		typeInfo.nameSourceStart = iVisited.getCPath().getPosition().getStartOffset();
		typeInfo.nameSourceEnd = iVisited.getCPath().getPosition().getEndOffset() - 1;
		if (!typeInfo.name.equals(OBJECT)) {
		  String superClass = getSuperClassName(iVisited.getSuperNode());
		  typeInfo.superclass = superClass;
		}
		typeInfo.isModule = false;
		typeInfo.modules = new String[0]; // FIXME Set up the modules as we go, or proactively dive into AST to grab these?
		typeInfo.secondary = false; // TODO Set secondary to true if we're enclosed by another type?
		requestor.enterType(typeInfo);
		
		Instruction ins = super.visitClassNode(iVisited);
		
		requestor.exitType(iVisited.getPosition().getEndOffset());
		return ins;
	}
	
	@Override
	public Instruction visitModuleNode(ModuleNode iVisited) {
		TypeInfo typeInfo = new TypeInfo();
		typeInfo.name = getFullyQualifiedName(iVisited.getCPath());
		typeInfo.declarationStart = iVisited.getPosition().getStartOffset();
		typeInfo.nameSourceStart = iVisited.getCPath().getPosition().getStartOffset();
		typeInfo.nameSourceEnd = iVisited.getCPath().getPosition().getEndOffset() - 1;
		typeInfo.superclass = MODULE; // FIXME Is this really true? Should it be null?
		typeInfo.isModule = true;
		typeInfo.modules = new String[0];
		typeInfo.secondary = false; // TODO Set secondary to true if we're enclosed by another type?
		requestor.enterType(typeInfo);
		
		Instruction ins = super.visitModuleNode(iVisited);
		
		requestor.exitType(iVisited.getPosition().getEndOffset());
		return ins;
	}
	
	@Override
	public Instruction visitDefnNode(DefnNode iVisited) {
		Visibility visibility = currentVisibility;		
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.declarationStart = iVisited.getPosition().getStartOffset();
		methodInfo.name = iVisited.getName();
		methodInfo.nameSourceStart = iVisited.getNameNode().getPosition().getStartOffset();
		methodInfo.nameSourceEnd = iVisited.getNameNode().getPosition().getEndOffset() - 1;
		if (methodInfo.name.equals(CONSTRUCTOR_NAME)) {
			visibility = Visibility.PROTECTED;
			methodInfo.isConstructor = true;
		} else {
			methodInfo.isConstructor = false;
		}
		methodInfo.isClassLevel = inSingletonClass;
		methodInfo.visibility = convertVisibility(visibility);
		methodInfo.parameterNames = ASTUtil.getArgs(iVisited.getArgsNode(), iVisited.getScope());
		
		if (methodInfo.isConstructor) {
			requestor.enterConstructor(methodInfo);
		} else {
			requestor.enterMethod(methodInfo);
		}

		Instruction ins = super.visitDefnNode(iVisited); // now traverse it's body
		
		if (methodInfo.isConstructor) {
			requestor.exitConstructor(iVisited.getPosition().getEndOffset());
		} else {
			requestor.exitMethod(iVisited.getPosition().getEndOffset());
		}
		return ins;
	}
	
	@Override
	public Instruction visitDefsNode(DefsNode iVisited) {
		/*
		 * Get the name of the current static method and add the name of the
		 * class or module to the beginning of it. This aInstructions instance
		 * method naming conflicts. e.g.: class A def self.method; end def
		 * method; end end will give us: A.method method in the Outline View.
		 */
		String fullName;
		String receiver = ASTUtil.stringRepresentation(iVisited.getReceiverNode());
		if (receiver != null && receiver.trim().length() > 0) {
			fullName = receiver + "." + iVisited.getName();
		} else {
			fullName = iVisited.getName();
		}

		MethodInfo methodInfo = new MethodInfo();
		methodInfo.declarationStart = iVisited.getPosition().getStartOffset();
		methodInfo.name = fullName;
		methodInfo.nameSourceStart = iVisited.getNameNode().getPosition().getStartOffset();
		methodInfo.nameSourceEnd = iVisited.getNameNode().getPosition().getEndOffset() - 1;
		methodInfo.isConstructor = false;
		methodInfo.isClassLevel = true;
		methodInfo.visibility = convertVisibility(currentVisibility);
		methodInfo.parameterNames = ASTUtil.getArgs(iVisited.getArgsNode(), iVisited.getScope());
		requestor.enterMethod(methodInfo);

		Instruction ins = super.visitDefsNode(iVisited); // now traverse it's body
		
		requestor.exitMethod(iVisited.getPosition().getEndOffset());
		return ins;
	}
	
	/**
	 * @param visibility
	 * @return
	 */
	private int convertVisibility(Visibility visibility) {
		// FIXME What about the module function and public-protected
		// visibilities?
		if (visibility == Visibility.PUBLIC)
			return IMethod.PUBLIC;
		if (visibility == Visibility.PROTECTED)
			return IMethod.PROTECTED;
		return IMethod.PRIVATE;
	}
	
	@Override
	public Instruction visitRootNode(RootNode iVisited) {
		requestor.enterScript();
		Instruction ins = super.visitRootNode(iVisited);
		requestor.exitScript(-1); // FIXME Actually grab the correct end offset somehow!
		return ins;
	}
	
	private String getFullyQualifiedName(Node node) {
		if (node == null)
			return EMPTY_STRING;
		if (node instanceof ConstNode) {
			ConstNode constNode = (ConstNode) node;
			return constNode.getName();
		}
		if (node instanceof Colon2Node) {
			Colon2Node colonNode = (Colon2Node) node;
			String prefix = getFullyQualifiedName(colonNode.getLeftNode());
			if (prefix.length() > 0)
				prefix = prefix + NAMESPACE_DELIMETER;
			return prefix + colonNode.getName();
		}
		return EMPTY_STRING;
	}
	
	/**
	 * Build up the fully qualified name of the super class for a class
	 * declaration
	 * 
	 * @param superNode
	 * @return
	 */
	private String getSuperClassName(Node superNode) {
		if (superNode == null)
			return OBJECT;
		return getFullyQualifiedName(superNode);
	}
	
	@Override
	public Instruction visitConstDeclNode(ConstDeclNode iVisited) {			
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitConstDeclNode(iVisited);
	}
	
	public Instruction visitClassVarAsgnNode(ClassVarAsgnNode iVisited) {
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitClassVarAsgnNode(iVisited);
	}
	
	public Instruction visitLocalAsgnNode(LocalAsgnNode iVisited) {	
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitLocalAsgnNode(iVisited);
	}
	
	@Override
	public Instruction visitInstAsgnNode(InstAsgnNode iVisited) {
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitInstAsgnNode(iVisited);
	}
	
	@Override
	public Instruction visitGlobalAsgnNode(GlobalAsgnNode iVisited) {
		FieldInfo field = createFieldInfo(iVisited);
		field.name = iVisited.getName();
		requestor.enterField(field);
		exitField(iVisited);
		return super.visitGlobalAsgnNode(iVisited);
	}

	private void exitField(AssignableNode iVisited) {
		requestor.exitField(iVisited.getPosition().getEndOffset() - 1);
	}

	private FieldInfo createFieldInfo(AssignableNode iVisited) {
		FieldInfo field = new FieldInfo();	
		field.declarationStart = iVisited.getPosition().getStartOffset();
		field.nameSourceStart = iVisited.getPosition().getStartOffset();
		String name = ASTUtil.getNameReflectively(iVisited);
		field.nameSourceEnd = iVisited.getPosition().getStartOffset() + name.length() - 1;
		return field;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jruby.ast.visitor.NodeVisitor#visitIterNode(org.jruby.ast.IterNode)
	 */
	public Instruction visitIterNode(IterNode iVisited) {
//		RubyBlock block = new RubyBlock(modelStack.peek()); FIXME Add method to notify of blocks?
		return super.visitIterNode(iVisited);
	}
	

	@Override
	public Instruction visitDAsgnNode(DAsgnNode iVisited) {
//		RubyDynamicVar var = new RubyDynamicVar(modelStack.peek(), iVisited.getName()); FIXME Notify like a normal local var?
		return super.visitDAsgnNode(iVisited);
	}
	
	@Override
	public Instruction visitSClassNode(SClassNode iVisited) {
		Node receiver = iVisited.getReceiverNode();
		if (receiver instanceof SelfNode) {
			inSingletonClass = true;
			Instruction ins = super.visitSClassNode(iVisited);
			inSingletonClass = false;
			return ins;
		}	
		return super.visitSClassNode(iVisited);
	}
	
	public Instruction visitFCallNode(FCallNode iVisited) {
		String functionName = iVisited.getName();
		if (functionName.equals(REQUIRE) || functionName.equals(LOAD)) {
			addImport(iVisited);
		} else if (functionName.equals(INCLUDE)) { // Collect included mixins
			includeModule(iVisited); 
		}
		return super.visitFCallNode(iVisited);
	}
	
	private void addImport(FCallNode iVisited) {
		ArrayNode node = (ArrayNode) iVisited.getArgsNode();
		String arg = getString(node);
		if (arg != null) {
			requestor.acceptImport(arg, iVisited.getPosition().getStartOffset(), iVisited.getPosition().getEndOffset());
		}
	}
	
	/**
	 * @param node
	 * @return
	 */
	private String getString(ArrayNode node) {
		Object tmp = node.childNodes().iterator().next();
		if (tmp instanceof DStrNode) {
			DStrNode dstrNode = (DStrNode) tmp;
			tmp = dstrNode.childNodes().iterator().next();
		}
		if (tmp instanceof StrNode) {
			StrNode strNode = (StrNode) tmp;
			return strNode.getValue().toString();
		}
		return null;
	}

	private void includeModule(FCallNode iVisited) {
		List<String> mixins = new LinkedList<String>();
		Node argsNode = iVisited.getArgsNode();
		Iterator iter = null;
		if (argsNode instanceof SplatNode) {
			SplatNode splat = (SplatNode) argsNode;
			iter = splat.childNodes().iterator();
		} else if (argsNode instanceof ArrayNode) {
			ArrayNode arrayNode = (ArrayNode) iVisited.getArgsNode();
			iter = arrayNode.childNodes().iterator();
		}
		for (; iter.hasNext();) {
			Node mixinNameNode = (Node) iter.next();
			if (mixinNameNode instanceof StrNode) {
				mixins.add(((StrNode) mixinNameNode).getValue().toString());
			}
			if (mixinNameNode instanceof DStrNode) {
				Node next = (Node) ((DStrNode) mixinNameNode).childNodes().iterator().next();
				if (next instanceof StrNode) {
					mixins.add(((StrNode) next).getValue().toString());
				}
			}
			if (mixinNameNode instanceof ConstNode) {
				mixins.add(((ConstNode) mixinNameNode).getName());
			}
		}
		for (String string : mixins) {
			requestor.acceptMixin(string);
		}
	}
	
	public Instruction visitVCallNode(VCallNode iVisited) {
		// XXX If the call has arguments, we need to find the method matching the
		// symbols and mark their visibility differently
		String functionName = iVisited.getName();
		if (functionName.equals(PUBLIC)) {
			currentVisibility = Visibility.PUBLIC;
		} else if (functionName.equals(PRIVATE)) {
			currentVisibility = Visibility.PRIVATE;
		} else if (functionName.equals(PROTECTED)) {
			currentVisibility = Visibility.PROTECTED;
		}
		return super.visitVCallNode(iVisited);
	}
	
	public Instruction visitAliasNode(AliasNode iVisited) {
		String name = iVisited.getNewName();		
		MethodInfo method = new MethodInfo();
		// TODO Use the visibility for the original method that this is aliasing?
		Visibility visibility = currentVisibility;
		if (name.equals(CONSTRUCTOR_NAME)) {
			visibility = Visibility.PROTECTED;
			method.isConstructor = true;
		} else {
			method.isConstructor = false;
		}
		method.declarationStart = iVisited.getPosition().getStartOffset();
		method.isClassLevel = inSingletonClass;
		method.name = name;
		method.visibility = convertVisibility(visibility);
		method.nameSourceStart = iVisited.getPosition().getStartOffset() + ALIAS.length();
		method.nameSourceEnd = iVisited.getPosition().getStartOffset() + ALIAS.length() + iVisited.getNewName().length() - 1;
		method.parameterNames = new String[0]; // TODO Find the existing method and steal it's parameter names	
		requestor.enterMethod(method);
		requestor.exitMethod(iVisited.getPosition().getEndOffset());
		return super.visitAliasNode(iVisited);
	}
}