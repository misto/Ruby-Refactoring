package org.rubypeople.rdt.internal.ui.text.ruby;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IEditorPart;
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
import org.jruby.ast.ModuleNode;
import org.jruby.ast.Node;
import org.jruby.ast.ScopeNode;
import org.jruby.lexer.yacc.SyntaxException;
import org.jruby.parser.RubyParserPool;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.codeassist.RubyElementRequestor;
import org.rubypeople.rdt.internal.core.RubyElement;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.RubyScriptStructureBuilder;
import org.rubypeople.rdt.internal.core.RubyType;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.corext.template.ruby.RubyContextType;
import org.rubypeople.rdt.internal.ti.util.AttributeLocator;
import org.rubypeople.rdt.internal.ti.util.ClosestSpanningNodeLocator;
import org.rubypeople.rdt.internal.ti.util.INodeAcceptor;
import org.rubypeople.rdt.internal.ti.util.MethodDefinitionLocator;
import org.rubypeople.rdt.internal.ti.util.ScopedNodeLocator;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.text.template.contentassist.RubyTemplateAccess;
import org.rubypeople.rdt.internal.ui.text.template.contentassist.TemplateEngine;
import org.rubypeople.rdt.internal.ui.text.template.contentassist.TemplateProposal;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.text.RubyTextTools;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposal;

public class RubyCompletionProcessor extends TemplateCompletionProcessor
		implements IContentAssistProcessor {

	private static String[] keywordProposals;

	protected IContextInformationValidator contextInformationValidator = new RubyContextInformationValidator();

	private static String[] preDefinedGlobals = { "$!", "$@", "$_", "$.", "$&",
			"$n", "$~", "$=", "$/", "$\\", "$0", "$*", "$$", "$?", "$:" };

	private static String[] globalContexts = { "error message",
			"position of an error occurrence", "latest read string by `gets'",
			"latest read number of line by interpreter",
			"latest matched string by the regexep.",
			"latest matched string by nth parentheses of regexp.",
			"data for latest matche for regexp",
			"whether or not case-sensitive in string matching",
			"input record separator", "output record separator",
			"the name of the ruby scpript file",
			"command line arguments for the ruby scpript",
			"PID for ruby interpreter",
			"status of the latest executed child process",
			"array of paths that ruby interpreter searches for files" };

	// FIXME This is an ugly hack, just hard-coding method names
	// FIXME Create a model for Ruby core in our Ruby Model!
	private static String[] KERNEL_METHODS = { "abort", "at_exit", "autoload",
			"binding", "block_given?", "callcc", "caller", "catch", "chomp",
			"chomp!", "chop", "chop!", "eval", "exec", "exit", "exit!", "fail",
			"fork", "format", "gets", "global_variables", "gsub", "gsub!",
			"iterator?", "lambda", "load", "local_variables", "loop", "open",
			"p", "print", "printf", "proc", "putc", "puts", "raise", "rand",
			"readline", "readlines", "require", "scan", "select",
			"set_trace_func", "singleton_method_added", "sleep", "split",
			"sprintf", "srand", "sub", "sub!", "syscall", "system", "test",
			"throw", "trace_var", "trap", "untrace_var" };

	/**
	 * The prefix for the current content assist
	 */
	protected String currentPrefix = null;

	/**
	 * Cursor position, counted from the beginning of the document.
	 * <P>
	 * The first position has index '0'.
	 */
	protected int cursorPosition = -1;

	/**
	 * The text viewer.
	 */
	private ITextViewer viewer;

	private IWorkingCopyManager fManager;

	private IEditorPart fEditor;

	private TemplateEngine fRubyTemplateEngine;

	public RubyCompletionProcessor(IEditorPart editor) {
		super();
		fEditor = editor;
		fManager = RubyPlugin.getDefault().getWorkingCopyManager();

		TemplateContextType contextType = RubyPlugin.getDefault()
				.getTemplateContextRegistry().getContextType(
						RubyContextType.NAME);
		if (contextType == null) {
			contextType = new RubyContextType();
			RubyPlugin.getDefault().getTemplateContextRegistry()
					.addContextType(contextType);
		}
		if (contextType != null)
			fRubyTemplateEngine = new TemplateEngine(contextType);
		else
			fRubyTemplateEngine = null;

	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int documentOffset) {
		this.viewer = viewer;
		ITextSelection selection = (ITextSelection) viewer
				.getSelectionProvider().getSelection();
		cursorPosition = selection.getOffset() + selection.getLength();

		ICompletionProposal[] normal = determineRubyElementProposals(viewer,
				documentOffset);
		List templates = determineTemplateProposals(viewer, documentOffset);
		ICompletionProposal[] templateArray = new ICompletionProposal[templates
				.size()];
		int i = 0;
		for (Iterator iter = templates.iterator(); iter.hasNext(); i++) {
			templateArray[i] = (ICompletionProposal) iter.next();
		}
		ICompletionProposal[] merged = merge(normal, templateArray);

		ICompletionProposal[] keywords = determineKeywordProposals(viewer,
				documentOffset);
		ICompletionProposal[] mergedTwo = merge(merged, keywords);
		
		ICompletionProposal[] completions = codeComplete(documentOffset);
		return merge(mergedTwo, completions);
	}
	
	private ICompletionProposal[] codeComplete(int offset) {
		try {
			IRubyScript script = fManager.getWorkingCopy(fEditor.getEditorInput());
			RubyScriptCompletion requestor = new RubyScriptCompletion(script); // TODO Instantiate a real one
			requestor.beginReporting();
			script.codeComplete(offset - 1, requestor);			
			requestor.endReporting();
			return requestor.getRubyCompletionProposals();
		} catch (Exception e) {
			// TODO Do something
			return new ICompletionProposal[0];
		}		
	}

	/**
	 * @param arrayOne
	 * @param arrayTwo
	 * @return
	 */
	private ICompletionProposal[] merge(ICompletionProposal[] arrayOne,
			ICompletionProposal[] arrayTwo) {
		ICompletionProposal[] merged = new ICompletionProposal[arrayOne.length
				+ arrayTwo.length];
		System.arraycopy(arrayOne, 0, merged, 0, arrayOne.length);
		System.arraycopy(arrayTwo, 0, merged, arrayOne.length, arrayTwo.length);
		return merged;
	}

	/**
	 * @param viewer
	 * @param documentOffset
	 * @return
	 */
	private ICompletionProposal[] determineRubyElementProposals(
			ITextViewer viewer, int documentOffset) {
		Collection completionProposals = getDocumentsRubyElementsInScope(documentOffset);
		String prefix = getCurrentPrefix(viewer.getDocument().get(),
				documentOffset);
		// following the JDT convention, if there's no text already entered,
		// then don't suggest imported elements
		if (prefix.length() > 0) {
			// FIXME Add elements from required/loaded files!
		}

		List possibleProposals = new ArrayList();
		for (Iterator iter = completionProposals.iterator(); iter.hasNext();) {
			String proposal = (String) iter.next();
			if (proposal.startsWith(prefix)) {
				String message = "{0}";
				IContextInformation info = new ContextInformation(proposal,
						MessageFormat
								.format(message, new Object[] { proposal }));
				possibleProposals
						.add(new CompletionProposal(proposal.substring(prefix
								.length(), proposal.length()), documentOffset,
								0, proposal.length() - prefix.length(), null,
								proposal, info, MessageFormat.format(
										"Ruby keyword: {0}",
										new Object[] { proposal })));
			}
		}
		ICompletionProposal[] result = new ICompletionProposal[possibleProposals
				.size()];
		possibleProposals.toArray(result);
		return result;
	}

	private Collection addKernelMethods() {
		Collection kernelProposals = new ArrayList();
		for (int i = 0; i < KERNEL_METHODS.length; i++) {
			kernelProposals.add(KERNEL_METHODS[i]);
		}
		return kernelProposals;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getImage(org.eclipse.jface.text.templates.Template)
	 */
	protected Image getImage(Template template) {
		return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_TEMPLATE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getContextType(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion)
	 */
	protected TemplateContextType getContextType(ITextViewer textViewer,
			IRegion region) {
		return RubyTemplateAccess.getDefault().getContextTypeRegistry()
				.getContextType(RubyContextType.NAME);
	}

	/**
	 * @return
	 */
	private List determineTemplateProposals(ITextViewer refViewer,
			int documentOffset) {
		TemplateEngine engine = fRubyTemplateEngine;
	
		if (engine != null) {
			IRubyScript unit = fManager
					.getWorkingCopy(fEditor.getEditorInput());
			if (unit == null)
				return Collections.EMPTY_LIST;

			engine.reset();
			engine.complete(refViewer, documentOffset,
					unit);

			TemplateProposal[] templateProposals = engine.getResults();
			List result = new ArrayList(Arrays.asList(templateProposals));

			IRubyCompletionProposal[] keyWordResults = getKeywordProposals(documentOffset);
			if (keyWordResults.length > 0) {
				// update relevance of template proposals that match with a
				// keyword
				// give those templates slightly more relevance than the keyword
				// to
				// sort them first
				// remove keyword templates that don't have an equivalent
				// keyword proposal
				if (keyWordResults.length > 0) {
					outer: for (int k = 0; k < templateProposals.length; k++) {
						TemplateProposal curr = templateProposals[k];
						String name = curr.getTemplate().getName();
						for (int i = 0; i < keyWordResults.length; i++) {
							String keyword = keyWordResults[i]
									.getDisplayString();
							if (name.startsWith(keyword)) {
								curr.setRelevance(keyWordResults[i]
										.getRelevance() + 1);
								continue outer;
							}
						}

					}
				}
			}
			return result;
		}

		return Collections.EMPTY_LIST;

	}

	private IRubyCompletionProposal[] getKeywordProposals(int documentOffset) {
		List keywords = getKeywords();
		List fKeywords = new ArrayList();
		for (Iterator iter = keywords.iterator(); iter.hasNext();) {
			String keyword = (String) iter.next();
			String prefix = getCurrentPrefix(viewer.getDocument().get(),
					documentOffset);
			if (prefix.length() >= keyword.length())
				continue;
			fKeywords.add(createKeywordProposal(keyword, prefix, documentOffset));
		}
		return (IRubyCompletionProposal[]) fKeywords
				.toArray(new RubyCompletionProposal[fKeywords.size()]);
	}

	private IRubyCompletionProposal createKeywordProposal(String keyword,
			String prefix, int documentOffset) {
		String completion = keyword
				.substring(prefix.length(), keyword.length());
		return new RubyCompletionProposal(completion, documentOffset,
				completion.length(), RubyPluginImages
						.get(RubyPluginImages.IMG_OBJS_TEMPLATE), keyword, 0);
	}

	private List getKeywords() {
		List list = new ArrayList();
		String[] keywords = RubyTextTools.getKeyWords();
		for (int i = 0; i < keywords.length; i++) {
			list.add(keywords[i]);
		}
		return list;
	}

	/**
	 * @param proposal
	 * @return
	 */
	private String getContext(String proposal) {
		for (int i = 0; i < preDefinedGlobals.length; i++) {
			if (proposal.equals(preDefinedGlobals[i]))
				return globalContexts[i];
		}
		return "";
	}

	/**
	 * @param proposal
	 * @return
	 */
	private boolean isPredefinedGlobal(String proposal) {
		for (int i = 0; i < preDefinedGlobals.length; i++) {
			if (proposal.equals(preDefinedGlobals[i]))
				return true;
		}
		return false;
	}

	/**
	 * Gets all the distinct elements in the current RubyScript
	 * @param offset 
	 * 
	 * @return a List of the names of all the elements in the current RubyScript
	 */
	private Collection getDocumentsRubyElementsInScope(int offset) {
		IRubyScript script = fManager.getWorkingCopy(fEditor.getEditorInput());
		

//		Collection elements = getElementsInScope(script, offset);
		
		String source = "";
		Collection elements = new ArrayList();
		try {
			// Get the script's source.  If possible, get the most recent contents.
			if ( script instanceof RubyScript ) {
				source = new String(((RubyScript)script).getContents());
			} else {
				source = script.getSource();
			}
			
			// Get all references projects
			List<IRubyProject> projects = new ArrayList<IRubyProject>();
			projects.add(script.getRubyProject());
			projects.addAll(script.getRubyProject().getReferencedProjects());
			
			// Parse
			Node rootNode = (new RubyParser()).parse(source);
			if ( rootNode == null ) { return elements; }

			// Find the enclosing method to get locals and args
			Node enclosingMethodNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, offset, new INodeAcceptor() {
				public boolean doesAccept(Node node) {
					return ( node instanceof DefnNode || node instanceof DefsNode );
				}
			});

			// Add local vars and arguments
			if ( enclosingMethodNode != null ) {
				ScopeNode scopeNode = null;			
				if ( enclosingMethodNode instanceof DefnNode ) { scopeNode = (ScopeNode)((DefnNode)enclosingMethodNode).getBodyNode(); }
				if ( enclosingMethodNode instanceof DefsNode ) { scopeNode = (ScopeNode)((DefsNode)enclosingMethodNode).getBodyNode(); }
				if ( scopeNode != null && scopeNode.getLocalNames().length > 0 ) {
					elements.addAll( Arrays.asList (scopeNode.getLocalNames()) );
				}
			}

			// Find the enclosing type (class or module) to get instance and classvars from
			Node enclosingTypeNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(rootNode, offset, new INodeAcceptor() {
				public boolean doesAccept(Node node) {
					return ( node instanceof ClassNode || node instanceof ModuleNode );
				}
			});

			// Add members from enclosing type
			if ( enclosingTypeNode != null ) {
				elements.addAll( getMembersAvailableInsideType( enclosingTypeNode, script ) );				
			}

			// Add all globals, classes, and modules
			for (Iterator iter = projects.iterator(); iter.hasNext();) {
				IRubyProject nextProject = (IRubyProject)(iter.next());
				
				System.out.println("*** Adding globals/classes/modules available in project: " + nextProject.getElementName() );
				
				elements.addAll(getElementsOfType( nextProject, new int[] { IRubyElement.GLOBAL }));
				elements.addAll(addClassesAndModulesInProject( nextProject ));
			}
			

			// always add Kernel methods
			elements.addAll(addKernelMethods());
			

		} catch ( RubyModelException rme ) {
			System.out.println("RubyModelException in RubyCompletionProcessor::getElementsInScope()");
			rme.printStackTrace();
			// Return empty 'elements'
		} catch ( SyntaxException se ) {
			System.out.println("SyntaxError in RubyCompletionProcessor::getElementsInScope()");
			se.printStackTrace();
			// Return empty 'elements'
		}

		

		return elements;
	}

	private Collection addClassesAndModulesInProject(IRubyProject project) {
		return getElementsOfType(project, new int[] { IRubyElement.TYPE });
	}

	private Collection getElementsOfType(IParent element, int[] types) {
		Collection suggestions = new ArrayList();
		try {
			IRubyElement[] elements = element.getChildren();
			if (elements == null)
				return suggestions;
			for (int x = 0; x < elements.length; x++) {
				IRubyElement child = elements[x];
				for (int i = 0; i < types.length; i++) {
					if (child.getElementType() == types[i]) {
						suggestions.add(child.getElementName());
						break;
					}
				}
				if (child instanceof IParent)
					suggestions
							.addAll(getElementsOfType((IParent) child, types));
			}
		} catch (RubyModelException e) {
			e.printStackTrace();
		}
		return suggestions;
	}

	/**
	 * @param script
	 * @return
	 */
	private Collection getElements(IParent element) {
		return getElementsOfType(element, new int[] { IRubyElement.TYPE,
				IRubyElement.METHOD, IRubyElement.GLOBAL,
				IRubyElement.CONSTANT, IRubyElement.CLASS_VAR,
				IRubyElement.INSTANCE_VAR });
	}
	
	
	/**
	 * Gets the memebrs available inside a type node (ModuleNode, ClassNode):
	 *  - Instance variables
	 *  - Class variables
	 *  - Methods
	 * 
	 * @param typeNode
	 * @return
	 */
	private List<String> getMembersAvailableInsideType(Node typeNode, IRubyScript script) throws RubyModelException {
		List<String> elements = new LinkedList<String>();
		if ( typeNode == null ) { return elements; }
		
		// Get type name
		String typeName = null;
		if ( typeNode instanceof ClassNode )  { typeName =  ((Colon2Node)((ClassNode)typeNode).getCPath()).getName(); }
		if ( typeNode instanceof ModuleNode ) { typeName = ((Colon2Node)((ModuleNode)typeNode).getCPath()).getName(); }
		if ( typeName == null ) { return elements; }
			
		// XXX rubyType may not be in script, but rather be defined in another script
//			IType rubyType = new RubyType( (RubyElement)script, typeName );
		//Better method:
		// Find the named type
//		IType rubyType = findTypeFromAllProjects(typeName, script);

//		System.out.println(" -- Located RubyType info.");
//		System.out.println(" -- Superclass: " + rubyType.getSuperclassName() );

//		if ( rubyType != null ) {
//			String[] includedModuleNames = rubyType.getIncludedModuleNames();
//			if ( includedModuleNames != null ) {
//				for ( String moduleName : rubyType.getIncludedModuleNames() ) {
//					System.out.println(" -- Includes module: " + moduleName);
//				}
//			}
//		}			
		
		
		
		// Get superclass and add its public members
		List<Node> superclassNodes = getSuperclassNodes( typeNode, script );
		
		for ( Node superclassNode : superclassNodes ) {
			elements.addAll( getMembersAvailableInsideType( superclassNode, script ) );
		}
		
		// Get public members of mixins
		List<String> mixinNames = getIncludedMixinNames( typeName, script );
		for ( String mixinName : mixinNames ) {
			List<Node> mixinDeclarations = getTypeDeclarationNodes( mixinName, script );
			for ( Node mixinDeclaration : mixinDeclarations ) {
				elements.addAll( getMembersAvailableInsideType( mixinDeclaration, script ) );
			}
		}
		
		// Get instance and class variables available in the enclosing type
		List<Node> instanceAndClassVars = ScopedNodeLocator.Instance().findNodesInScope(typeNode, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ( node instanceof InstVarNode ||
						 node instanceof InstAsgnNode ||
						 node instanceof ClassVarNode ||
						 node instanceof ClassVarDeclNode ||
						 node instanceof ClassVarAsgnNode );
			}
		});
		
		if ( instanceAndClassVars != null ) {
			// Get the unique names of instance and class variables
			Set instanceAndClassVarNames = new HashSet(instanceAndClassVars.size());
			for ( Node varNode : instanceAndClassVars ) {
				String name = getNameReflectively(varNode);
				if ( name != null ) {
					instanceAndClassVarNames.add(name);
				}
			}
		
			// Add instance and class variables to matched elements
			elements.addAll( instanceAndClassVarNames );
		}
		
		// Get method names defined by DefnNodes and DefsNodes
		List<Node> methodDefinitions = ScopedNodeLocator.Instance().findNodesInScope(typeNode, new INodeAcceptor() {
			public boolean doesAccept(Node node) {
				return ( node instanceof DefnNode ) || ( node instanceof DefsNode );
			}
		});
		for ( Node methodDefinition : methodDefinitions ) {
			if ( methodDefinition instanceof DefnNode ) { elements.add( ((DefnNode)methodDefinition).getName() ); }
			if ( methodDefinition instanceof DefsNode ) { elements.add( ((DefsNode)methodDefinition).getName() ); }
		}
		
		// Get instance and class vars defined by [c]attr_* calls
		elements.addAll( AttributeLocator.Instance().findInstanceAttributesInScope(typeNode) );
		
		return elements;
	}

	/**
	 * Finds all nodes that declare a type that is a superclass of the specified node.  Example:
	 * 
	 * """
	 * class Klass;def meth_1;1;end;end
	 * class Klass;def meth_2;2;end;end
	 * 
	 * class SubKlass < Klass;end
	 * """
	 * 
	 * Issuing getSuperClassNodes() on the ClassNode declaring SubKlass would return two ClassNodes;
	 * one for each definition of Klass.
	 * 
	 * @param typeNode Node to find superclass nodes of
	 * @return List of ClassNode or ModuleNode
	 */
	private List<Node> getSuperclassNodes( Node typeNode, IRubyScript script ) {
		if ( typeNode instanceof ClassNode ) {
			Node superNode = ((ClassNode)typeNode).getSuperNode();
			if ( superNode instanceof ConstNode ) {
				String superclassName = ((ConstNode)superNode).getName();
				return getTypeDeclarationNodes( superclassName, script );
			}
		}
		
		return new ArrayList<Node>();
	}
	
	private IType findTypeFromAllProjects(String typeName, IRubyScript rootScript) {
		// Grab the project and all referred projects
		List<IRubyProject> projects = new LinkedList<IRubyProject>();
		projects.add(rootScript.getRubyProject());
		projects.addAll(rootScript.getRubyProject().getReferencedProjects());

		List<IRubyProject> refProjects = rootScript.getRubyProject().getReferencedProjects();

		// Find the named type
		RubyElementRequestor completer = new RubyElementRequestor(projects.toArray(new IRubyProject[]{}));
		return completer.findType(typeName);
	}
	
	/** Lookup type declaration nodes */
	private List<Node> getTypeDeclarationNodes( String typeName, IRubyScript script ) {
		System.out.println("Being asked for the type decl node for " + typeName );
		
		// Find the named type
		IType type = findTypeFromAllProjects(typeName, script);
		
		try {
			if ( type instanceof RubyType ) {

				// FIXME This feels a little hacky and backwards - RubyType.getSource() and then parse... consider reworking the clients to this method to accept RubyTypes or something similar?				
				// Find source and parse
				RubyType rubyType = (RubyType)type;
				String source = rubyType.getSource();
				
				// FIXME Why does the parser balk on \r chars?
				source = source.replace('\r', ' ');
				Node rootNode = (new RubyParser()).parse( source );
				
				// Bail if the parse fails
				if ( rootNode == null ) { return new ArrayList(); }

				// Return any type declaration nodes in included source 
				return ScopedNodeLocator.Instance().findNodesInScope(rootNode, new INodeAcceptor() {
					public boolean doesAccept(Node node) {
						return ( node instanceof ClassNode ) ||
						       ( node instanceof ModuleNode );
					}
				});
			}
			
		} catch ( RubyModelException rme ) {
			rme.printStackTrace();
		}
		
		return new ArrayList<Node>(0);
	}
	
	private List<String> getIncludedMixinNames( String typeName, IRubyScript script ) {
		IType rubyType = new RubyType( (RubyElement)script, typeName );
		
		try {
			String[] includedModuleNames = rubyType.getIncludedModuleNames();
			if ( includedModuleNames != null ) {
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
	 * @param node
	 * @return name or null
	 */
	// TODO Copy/pasted from DefaultOccurrencesFinder, refactor these two methods to a common location.
	private String getNameReflectively( Node node ) {
		try {
			Method getNameMethod = node.getClass().getMethod("getName", new Class[]{});
			Object name = getNameMethod.invoke( node, new Object[0] );
			return (String)name;
		} catch (Exception e) {
			return null;
		}
	}
	

	private ICompletionProposal[] determineKeywordProposals(ITextViewer viewer,
			int documentOffset) {
		initKeywordProposals();

		String prefix = getCurrentPrefix(viewer.getDocument().get(),
				documentOffset);
		// following the JDT convention, if there's no text already entered,
		// then don't suggest keywords
		if (prefix.length() < 1) {
			return new ICompletionProposal[0];
		}
		List completionProposals = Arrays.asList(keywordProposals);

		// FIXME Refactor to combine the copied code in
		// determineRubyElementProposals
		List possibleProposals = new ArrayList();
		for (int i = 0; i < completionProposals.size(); i++) {
			String proposal = (String) completionProposals.get(i);
			if (proposal.startsWith(prefix)) {
				String message;
				if (isPredefinedGlobal(proposal)) {
					message = "{0} " + getContext(proposal);
				} else {
					message = "{0}";
				}
				IContextInformation info = new ContextInformation(proposal,
						MessageFormat
								.format(message, new Object[] { proposal }));
				possibleProposals
						.add(new CompletionProposal(proposal.substring(prefix
								.length(), proposal.length()), documentOffset,
								0, proposal.length() - prefix.length(), null,
								proposal, info, MessageFormat.format(
										"Ruby keyword: {0}",
										new Object[] { proposal })));
			}
		}
		ICompletionProposal[] result = new ICompletionProposal[possibleProposals
				.size()];
		possibleProposals.toArray(result);
		return result;
	}

	/**
	 * 
	 */
	private void initKeywordProposals() {
		if (keywordProposals == null) {
			String[] keywords = RubyTextTools.getKeyWords();
			keywordProposals = new String[keywords.length
					+ preDefinedGlobals.length];
			System.arraycopy(keywords, 0, keywordProposals, 0, keywords.length);
			System.arraycopy(preDefinedGlobals, 0, keywordProposals,
					keywords.length, preDefinedGlobals.length);
		}
	}

	protected String getCurrentPrefix(String documentString, int documentOffset) {
		int tokenLength = 0;
		while ((documentOffset - tokenLength > 0)
				&& !Character.isWhitespace(documentString.charAt(documentOffset
						- tokenLength - 1)))
			tokenLength++;
		return documentString.substring((documentOffset - tokenLength),
				documentOffset);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.templates.TemplateCompletionProcessor#getTemplates(java.lang.String)
	 */
	protected Template[] getTemplates(String contextTypeId) {
		return RubyTemplateAccess.getDefault().getTemplateStore()
				.getTemplates();
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int documentOffset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return null;
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return new char[] { '#' };
	}

	public IContextInformationValidator getContextInformationValidator() {
		return contextInformationValidator;
	}

	public String getErrorMessage() {
		return null;
	}

	protected class RubyContextInformationValidator implements
			IContextInformationValidator, IContextInformationPresenter {

		protected int installDocumentPosition;

		/**
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#install(IContextInformation,
		 *      ITextViewer, int)
		 */
		public void install(IContextInformation info, ITextViewer viewer,
				int documentPosition) {
			installDocumentPosition = documentPosition;
		}

		/**
		 * @see org.eclipse.jface.text.contentassist.IContextInformationValidator#isContextInformationValid(int)
		 */
		public boolean isContextInformationValid(int documentPosition) {
			return Math.abs(installDocumentPosition - documentPosition) < 1;
		}

		/**
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int,
		 *      TextPresentation)
		 */
		public boolean updatePresentation(int documentPosition,
				TextPresentation presentation) {
			return false;
		}
	}
}