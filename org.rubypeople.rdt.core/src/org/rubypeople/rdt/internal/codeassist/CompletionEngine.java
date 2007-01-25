package org.rubypeople.rdt.internal.codeassist;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.jruby.ast.ClassNode;
import org.jruby.ast.ClassVarAsgnNode;
import org.jruby.ast.ClassVarDeclNode;
import org.jruby.ast.ClassVarNode;
import org.jruby.ast.Colon2Node;
import org.jruby.ast.ConstNode;
import org.jruby.ast.DefnNode;
import org.jruby.ast.DefsNode;
import org.jruby.ast.InstAsgnNode;
import org.jruby.ast.InstVarNode;
import org.jruby.ast.MethodDefNode;
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.parser.StaticScope;
import org.rubypeople.rdt.core.CompletionProposal;
import org.rubypeople.rdt.core.CompletionRequestor;
import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.RubyType;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.search.ExperimentalIndex;
import org.rubypeople.rdt.internal.ti.DefaultTypeInferrer;
import org.rubypeople.rdt.internal.ti.ITypeGuess;
import org.rubypeople.rdt.internal.ti.ITypeInferrer;
import org.rubypeople.rdt.internal.ti.util.AttributeLocator;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ti.util.ScopedNodeLocator;

public class CompletionEngine {
	private CompletionRequestor requestor;
	private String prefix;

	public CompletionEngine(CompletionRequestor requestor) {
		this.requestor = requestor;
	}

	public void complete(IRubyScript script, int offset) throws RubyModelException {
		this.prefix = null;
		this.requestor.beginReporting();
		if (offset < 0)
			offset = 0;
		StringBuffer source = new StringBuffer(script.getSource());
		int replaceStart = offset + 1;
		// Read from offset back until we hit a: space, period
		// if we hit a period, use character before period as offset for
		// inferrer
		// if we hit a space, use character after space?
		// TODO We need to handle other bad syntax like invoking completion
		// right after an @
		StringBuffer tmpPrefix = new StringBuffer();
		boolean isMethod = false;
		for (int i = offset; i >= 0; i--) {
			char curChar = source.charAt(i);
			if (curChar == '.') {
				isMethod = true;
				if (offset == i) { // if it's the first character we looked at,
					// fix syntax
					source.deleteCharAt(i);
					offset--;
					break;
				}
				offset = i - 1;
				break;
			}
			if (Character.isWhitespace(curChar)) {
				offset = i + 1;
				break;
			}
			tmpPrefix.insert(0, curChar);
		}
		this.prefix = tmpPrefix.toString();
		if (this.prefix != null)
			replaceStart -= this.prefix.length();

		if (isConstant()) { // type or constant
			suggestTypeNames(replaceStart);
			suggestConstantNames(replaceStart);
		} else { // method or variable
			ITypeInferrer inferrer = new DefaultTypeInferrer();
			List<ITypeGuess> guesses = inferrer.infer(source.toString(), offset);

			IRubyProject rubyProject = script.getRubyProject();
			ISourceFolderRoot[] roots = rubyProject.getSourceFolderRoots();
			// ILoadpathEntry[] loadpaths =
			// rubyProject.getResolvedLoadpath(true);
			for (int i = 0; i < roots.length; i++) {
				ISourceFolderRoot root = roots[i];
				IImportDeclaration[] imports = script.getImports();
				for (int j = 0; j < imports.length; j++) {
					String path = imports[j].getElementName();
					StringTokenizer tokenizer = new StringTokenizer(path, "\\/");
					List<String> tokens = new ArrayList<String>();
					while(tokenizer.hasMoreTokens()) {
						tokens.add(tokenizer.nextToken());
					}					
					String name = tokens.remove(tokens.size() - 1) + ".rb";
					String[] pckgs =  (String[]) tokens.toArray(new String[tokens.size()]);
					ISourceFolder folder = root.getSourceFolder(pckgs);
					if (!folder.exists()) continue;
					IRubyScript otherScript = folder.getRubyScript(name);
					if (!otherScript.exists()) continue;
					List<IType> types = getTypes(otherScript);
					for (IType type : types) {
						for (ITypeGuess guess : guesses) {
							if (guess.getType().equals(type.getElementName())) {
								IMethod[] methods = type.getMethods();
								for(int x = 0; x < methods.length; x++) {
									addProposal(replaceStart, CompletionProposal.METHOD_REF, methods[x].getElementName());
								}
							}
						}
					}
				}
			}
//			bruteForceMethodSuggestion(script, replaceStart, guesses);
			if (!isMethod)
				getDocumentsRubyElementsInScope(script, source.toString(), offset, replaceStart);
		}
		this.requestor.endReporting();
	}

	private List<IType> getTypes(IParent script) {
		List<IType> types = new ArrayList<IType>();
		try {
			IRubyElement[] children = script.getChildren();
			for (int i = 0; i < children.length; i++) {
				if (children[i].isType(IRubyElement.TYPE)) {
					types.add((IType) children[i]);
				}
				if (children[i] instanceof IParent) {
					types.addAll(getTypes((IParent) children[i]));
				}
			}
		} catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return types;
	}

	private void bruteForceMethodSuggestion(IRubyScript script, int replaceStart, List<ITypeGuess> guesses) throws RubyModelException {
		RubyElementRequestor completer = new RubyElementRequestor(script.getRubyProject());
		// TODO Search the loadpath + imports!
		for (Iterator iter = guesses.iterator(); iter.hasNext();) {
			ITypeGuess guess = (ITypeGuess) iter.next();
			IType type = completer.findType(guess.getType());
			suggestMethods(replaceStart, completer, guess, type);
		}
	}

	private void suggestTypeNames(int replaceStart) {
		List<String> types = ExperimentalIndex.getTypes();
		// TODO Remove duplicates? Sort?
		for (String name : types) {
			if (this.prefix != null && !name.startsWith(this.prefix))
				continue;
			addProposal(replaceStart, CompletionProposal.TYPE_REF, name);
		}
	}

	private CompletionProposal addProposal(int replaceStart, int type, String name) {
		CompletionProposal proposal = new CompletionProposal(type, name, 100);
		proposal.setReplaceRange(replaceStart, replaceStart + name.length());
		requestor.accept(proposal);
		return proposal;
	}

	private void suggestConstantNames(int replaceStart) {
		List<String> types = ExperimentalIndex.getConstants();
		// TODO Remove duplicates? Sort?
		for (String name : types) {
			if (this.prefix != null && !name.startsWith(this.prefix))
				continue;
			CompletionProposal proposal = new CompletionProposal(CompletionProposal.FIELD_REF, name, 100);
			proposal.setReplaceRange(replaceStart, replaceStart + name.length());
			requestor.accept(proposal);
		}
	}

	private boolean isConstant() {
		return this.prefix != null && this.prefix.length() > 0 && Character.isUpperCase(this.prefix.charAt(0));
	}

	private void suggestMethods(int replaceStart, RubyElementRequestor completer, ITypeGuess guess, IType type) throws RubyModelException {
		if (type == null)
			return;

		suggestMethods(replaceStart, guess.getConfidence(), type);
		// Now grab methods from all the included modules
		String[] modules = type.getIncludedModuleNames();
		if (modules != null) {
			for (int x = 0; x < modules.length; x++) {
				IType tmpType = completer.findType(modules[x]);
				suggestMethods(replaceStart, guess.getConfidence(), tmpType);
			}
		}
		String superClass = type.getSuperclassName();
		if (superClass == null)
			return;
		// FIXME This shouldn't happen! Object shouldn't be a parent of itself!
		if (type.getElementName().equals("Object") && superClass.equals("Object"))
			return;
		IType parentClass = completer.findType(superClass);
		suggestMethods(replaceStart, completer, guess, parentClass);
	}

	private void suggestMethods(int replaceStart, int confidence, IType type) throws RubyModelException {
		if (type == null)
			return;
		IMethod[] methods = type.getMethods();
		for (int k = 0; k < methods.length; k++) {
			IMethod method = methods[k];
			String name = method.getElementName();
			if (prefix != null && !name.startsWith(prefix))
				continue;
			CompletionProposal proposal = new CompletionProposal(CompletionProposal.METHOD_REF, name, confidence);
			proposal.setReplaceRange(replaceStart, replaceStart + name.length());
			int flags = Flags.AccDefault;
			if (method.isSingleton()) {
				flags |= Flags.AccStatic;
			}
			switch (method.getVisibility()) {
			case IMethod.PRIVATE:
				flags |= Flags.AccPrivate;
				break;
			case IMethod.PUBLIC:
				flags |= Flags.AccPublic;
				break;
			case IMethod.PROTECTED:
				flags |= Flags.AccProtected;
				break;
			default:
				break;
			}
			proposal.setFlags(flags);
			requestor.accept(proposal);
		}
	}

	/**
	 * Gets all the distinct elements in the current RubyScript
	 * 
	 * @param offset
	 * @param replaceStart
	 * 
	 * @return a List of the names of all the elements in the current RubyScript
	 */
	private void getDocumentsRubyElementsInScope(IRubyScript script, String source, int offset, int replaceStart) {
		try {
			// FIXME Try to stop all the multiple re-parsing of the source! Can
			// we parse once and pass the root node around?
			// Parse
			Node rootNode = (new RubyParser()).parse(source);
			if (rootNode == null) {
				return;
			}

			// Find the enclosing method to get locals and args
			Node enclosingMethodNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, offset, new INodeAcceptor() {
				public boolean doesAccept(Node node) {
					return (node instanceof DefnNode || node instanceof DefsNode);
				}
			});

			// Add local vars and arguments
			// Add local vars and arguments
			if (enclosingMethodNode != null && enclosingMethodNode instanceof MethodDefNode) {
				StaticScope scope = ((MethodDefNode) enclosingMethodNode).getScope();
				if (scope != null && scope.getVariables().length > 0) {
					List locals = Arrays.asList(scope.getVariables());
					for (Iterator iter = locals.iterator(); iter.hasNext();) {
						String local = (String) iter.next();
						if (prefix != null && !local.startsWith(prefix))
							continue;
						CompletionProposal proposal = new CompletionProposal(CompletionProposal.LOCAL_VARIABLE_REF, local, 100);
						proposal.setReplaceRange(replaceStart, replaceStart + local.length());
						requestor.accept(proposal);
					}
				}
			}

			// Find the enclosing type (class or module) to get instance and
			// classvars from
			Node enclosingTypeNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, offset, new INodeAcceptor() {
				public boolean doesAccept(Node node) {
					return (node instanceof ClassNode || node instanceof ModuleNode);
				}
			});

			// Add members from enclosing type
			if (enclosingTypeNode != null) {
				getMembersAvailableInsideType(enclosingTypeNode, script, replaceStart);
			}

			// Add all globals, classes, and modules
			getElementsOfType(script.getRubyProject(), new int[] { IRubyElement.GLOBAL }, replaceStart);
			addClassesAndModulesInProject(script.getRubyProject(), replaceStart);
		} catch (RubyModelException rme) {
			System.out.println("RubyModelException in CompletionEngine::getElementsInScope()");
			rme.printStackTrace();
		} catch (SyntaxException se) {
			System.out.println("SyntaxError in CompletionEngine::getElementsInScope()");
			se.printStackTrace();
		}
	}

	private void addClassesAndModulesInProject(IRubyProject project, int replaceStart) {
		getElementsOfType(project, new int[] { IRubyElement.TYPE }, replaceStart);
	}

	private void getElementsOfType(IParent element, int[] types, int replaceStart) {
		try {
			IRubyElement[] elements = element.getChildren();
			if (elements == null)
				return;
			for (int x = 0; x < elements.length; x++) {
				IRubyElement child = elements[x];
				for (int i = 0; i < types.length; i++) {
					if (child.getElementType() != types[i])
						continue;
					String name = child.getElementName();
					if (prefix != null && !name.startsWith(prefix))
						continue;
					CompletionProposal proposal = new CompletionProposal(getCompletionProposalType(child), name, 100);
					proposal.setReplaceRange(replaceStart, replaceStart + name.length());
					requestor.accept(proposal);
				}
				if (child instanceof IParent)
					getElementsOfType((IParent) child, types, replaceStart);
			}
		} catch (RubyModelException e) {
			e.printStackTrace();
		}
	}

	private int getCompletionProposalType(IRubyElement child) {
		switch (child.getElementType()) {
		case IRubyElement.DYNAMIC_VAR:
		case IRubyElement.LOCAL_VARIABLE:
			return CompletionProposal.LOCAL_VARIABLE_REF;
		case IRubyElement.METHOD:
			return CompletionProposal.METHOD_REF;
		case IRubyElement.TYPE:
			return CompletionProposal.TYPE_REF;
		case IRubyElement.INSTANCE_VAR:
		case IRubyElement.CLASS_VAR:
			return CompletionProposal.FIELD_REF;
		default:
			return CompletionProposal.KEYWORD;
		}
	}

	/**
	 * Gets the members available inside a type node (ModuleNode, ClassNode): -
	 * Instance variables - Class variables - Methods
	 * 
	 * @param typeNode
	 * @return
	 */
	private void getMembersAvailableInsideType(Node typeNode, IRubyScript script, int replaceStart) throws RubyModelException {
		if (typeNode == null) {
			return;
		}

		// Get type name
		String typeName = null;
		if (typeNode instanceof ClassNode) {
			typeName = ((Colon2Node) ((ClassNode) typeNode).getCPath()).getName();
		}
		if (typeNode instanceof ModuleNode) {
			typeName = ((Colon2Node) ((ModuleNode) typeNode).getCPath()).getName();
		}
		if (typeName == null) {
			return;
		}

		// XXX rubyType may not be in script, but rather be defined in another
		// script
		// IType rubyType = new RubyType( (RubyElement)script, typeName );
		// Better method:
		// Find the named type
		// IType rubyType = findTypeFromAllProjects(typeName, script);

		// System.out.println(" -- Located RubyType info.");
		// System.out.println(" -- Superclass: " + rubyType.getSuperclassName()
		// );

		// if ( rubyType != null ) {
		// String[] includedModuleNames = rubyType.getIncludedModuleNames();
		// if ( includedModuleNames != null ) {
		// for ( String moduleName : rubyType.getIncludedModuleNames() ) {
		// System.out.println(" -- Includes module: " + moduleName);
		// }
		// }
		// }

		// Get superclass and add its public members
		List<Node> superclassNodes = getSuperclassNodes(typeNode, script);
		for (Node superclassNode : superclassNodes) {
			getMembersAvailableInsideType(superclassNode, script, replaceStart);
		}

		// Get public members of mixins
		List<String> mixinNames = getIncludedMixinNames(typeName, script);
		for (String mixinName : mixinNames) {
			List<Node> mixinDeclarations = getTypeDeclarationNodes(mixinName, script);
			for (Node mixinDeclaration : mixinDeclarations) {
				getMembersAvailableInsideType(mixinDeclaration, script, replaceStart);
			}
		}

		// Get instance and class variables available in the enclosing type
		List<Node> instanceAndClassVars = ScopedNodeLocator.Instance().findNodesInScope(typeNode, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return (node instanceof InstVarNode || node instanceof InstAsgnNode || node instanceof ClassVarNode || node instanceof ClassVarDeclNode || node instanceof ClassVarAsgnNode);
			}
		});

		if (instanceAndClassVars != null) {
			// Get the unique names of instance and class variables
			for (Node varNode : instanceAndClassVars) {
				String name = getNameReflectively(varNode);
				if (name == null)
					continue;
				if (prefix != null && !name.startsWith(prefix))
					continue;

				CompletionProposal proposal = new CompletionProposal(CompletionProposal.FIELD_REF, name, 100);
				proposal.setReplaceRange(replaceStart, replaceStart + name.length());
				requestor.accept(proposal);
			}
		}

		// Get method names defined by DefnNodes and DefsNodes
		List<Node> methodDefinitions = ScopedNodeLocator.Instance().findNodesInScope(typeNode, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return (node instanceof DefnNode) || (node instanceof DefsNode);
			}
		});
		for (Node methodDefinition : methodDefinitions) {
			String name = null;
			if (methodDefinition instanceof DefnNode) {
				name = ((DefnNode) methodDefinition).getName();
			}
			if (methodDefinition instanceof DefsNode) {
				name = ((DefsNode) methodDefinition).getName();
			}
			if (name == null)
				continue;
			if (prefix != null && !name.startsWith(prefix))
				continue;
			CompletionProposal proposal = new CompletionProposal(CompletionProposal.METHOD_REF, name, 100);
			proposal.setReplaceRange(replaceStart, replaceStart + name.length());
			requestor.accept(proposal);
		}

		// Get instance and class vars defined by [c]attr_* calls
		List<String> attrs = AttributeLocator.Instance().findInstanceAttributesInScope(typeNode);
		for (Iterator iter = attrs.iterator(); iter.hasNext();) {
			String attr = (String) iter.next();
			if (prefix != null && !attr.startsWith(prefix))
				continue;
			CompletionProposal proposal = new CompletionProposal(CompletionProposal.FIELD_REF, attr, 100);
			proposal.setReplaceRange(replaceStart, replaceStart + attr.length());
			requestor.accept(proposal);
		}

	}

	/**
	 * Finds all nodes that declare a type that is a superclass of the specified
	 * node. Example:
	 * 
	 * """ class Klass;def meth_1;1;end;end class Klass;def meth_2;2;end;end
	 * 
	 * class SubKlass < Klass;end """
	 * 
	 * Issuing getSuperClassNodes() on the ClassNode declaring SubKlass would
	 * return two ClassNodes; one for each definition of Klass.
	 * 
	 * @param typeNode
	 *            Node to find superclass nodes of
	 * @return List of ClassNode or ModuleNode
	 */
	private List<Node> getSuperclassNodes(Node typeNode, IRubyScript script) {
		if (typeNode instanceof ClassNode) {
			Node superNode = ((ClassNode) typeNode).getSuperNode();
			if (superNode instanceof ConstNode) {
				String superclassName = ((ConstNode) superNode).getName();
				return getTypeDeclarationNodes(superclassName, script);
			}
		}
		return new ArrayList<Node>();
	}

	/** Lookup type declaration nodes */
	private List<Node> getTypeDeclarationNodes(String typeName, IRubyScript script) {
		System.out.println("Being asked for the type decl node for " + typeName);

		// Find the named type
		IType type = findTypeFromAllProjects(typeName, script);

		try {
			if (type instanceof RubyType) {

				// FIXME This feels a little hacky and backwards -
				// RubyType.getSource() and then parse... consider reworking the
				// clients to this method to accept RubyTypes or something
				// similar?
				// Find source and parse
				RubyType rubyType = (RubyType) type;
				String source = rubyType.getSource();

				// FIXME Why does the parser balk on \r chars?
				source = source.replace('\r', ' ');
				Node rootNode = (new RubyParser()).parse(source);

				// Bail if the parse fails
				if (rootNode == null) {
					return new ArrayList();
				}

				// Return any type declaration nodes in included source
				return ScopedNodeLocator.Instance().findNodesInScope(rootNode, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						return (node instanceof ClassNode) || (node instanceof ModuleNode);
					}
				});
			}

		} catch (RubyModelException rme) {
			rme.printStackTrace();
		}

		return new ArrayList<Node>(0);
	}

	private IType findTypeFromAllProjects(String typeName, IRubyScript rootScript) {
		// Grab the project and all referred projects
		List<IRubyProject> projects = new LinkedList<IRubyProject>();
		projects.add(rootScript.getRubyProject());
		// FIXME Search the loadpaths!
		// projects.addAll(rootScript.getRubyProject().getReferencedProjects());

		// Find the named type
		RubyElementRequestor completer = new RubyElementRequestor(projects.toArray(new IRubyProject[] {}));
		return completer.findType(typeName);
	}

	private List<String> getIncludedMixinNames(String typeName, IRubyScript script) {
		IType rubyType = new RubyType((RubyElement) script, typeName);

		try {
			String[] includedModuleNames = rubyType.getIncludedModuleNames();
			if (includedModuleNames != null) {
				return Arrays.asList(rubyType.getIncludedModuleNames());
			} else {
				return new ArrayList<String>(0);
			}
		} catch (RubyModelException e) {
			return new ArrayList<String>(0);
		}
	}

	/**
	 * Gets the name of a node by reflectively invoking "getName()" on it;
	 * helper method just to cut many "instanceof/cast" pairs.
	 * 
	 * @param node
	 * @return name or null
	 */
	// TODO Copy/pasted from DefaultOccurrencesFinder, refactor these two
	// methods to a common location.
	private String getNameReflectively(Node node) {
		try {
			Method getNameMethod = node.getClass().getMethod("getName", new Class[] {});
			Object name = getNameMethod.invoke(node, new Object[0]);
			return (String) name;
		} catch (Exception e) {
			return null;
		}
	}

}
