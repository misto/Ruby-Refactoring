package org.rubypeople.rdt.internal.core.search.indexing;

import java.util.Stack;

import org.rubypeople.rdt.core.Flags;
import org.rubypeople.rdt.core.compiler.CategorizedProblem;
import org.rubypeople.rdt.internal.compiler.ISourceElementRequestor;

public class SourceIndexerRequestor implements ISourceElementRequestor {

	private SourceIndexer indexer;
	private Stack<String> typeStack;

	public SourceIndexerRequestor(SourceIndexer sourceIndexer) {
		this.indexer = sourceIndexer;
		typeStack = new Stack<String>();
	}

	public void acceptConstructorReference(String name, int argCount, int offset) {
		indexer.addConstructorReference(name.toCharArray(), argCount);
	}

	public void acceptFieldReference(String name, int offset) {
		// TODO Auto-generated method stub

	}

	public void acceptImport(String value, int startOffset, int endOffset) {
		// TODO Auto-generated method stub

	}

	public void acceptMethodReference(String name, int argCount, int offset) {
		indexer.addMethodReference(name.toCharArray(), argCount);
	}

	public void acceptMixin(String string) {
		// TODO Auto-generated method stub

	}

	public void acceptProblem(CategorizedProblem problem) {
		// TODO Auto-generated method stub

	}

	public void acceptTypeReference(String name, int startOffset, int endOffset) {
		indexer.addTypeReference(name.toCharArray());
	}

	public void acceptUnknownReference(String name, int startOffset,
			int endOffset) {
		// TODO Auto-generated method stub

	}

	public void enterConstructor(MethodInfo constructor) {
		indexer.addConstructorDeclaration(constructor.name.toCharArray(), constructor.parameterNames.length);
	}

	public void enterField(FieldInfo field) {
		indexer.addFieldDeclaration(null, field.name.toCharArray());
	}

	public void enterMethod(MethodInfo method) {
		indexer.addMethodDeclaration(method.name.toCharArray(), method.parameterNames.length);
	}

	public void enterScript() {
		// TODO Auto-generated method stub

	}

	public void enterType(TypeInfo type) {		
		String[] modules = type.modules;
		char[][] mod = new char[modules.length][];
		for (int i = 0; i < modules.length; i++) {
			mod[i] = modules[i].toCharArray();
		}
		char[] packName = new char[0];
		char[] superclass = new char[0];
		if (type.superclass != null) {
			superclass = type.superclass.toCharArray();
		}
		indexer.addClassDeclaration(type.isModule ? Flags.AccModule : 0, packName, type.name.toCharArray(), getEnclosingTypeNames(), superclass, mod, type.secondary);
	    typeStack.push(type.name);
	}

	private char[][] getEnclosingTypeNames() {
		char[][] names = new char[typeStack.size()][];
		int i = 0;
		for (String name : typeStack) {
			names[i++] = name.toCharArray();
		}
		return names;
	}

	public void exitConstructor(int endOffset) {
		// TODO Auto-generated method stub

	}

	public void exitField(int endOffset) {
		// TODO Auto-generated method stub

	}

	public void exitMethod(int endOffset) {
		// TODO Auto-generated method stub

	}

	public void exitScript(int endOffset) {
		// TODO Auto-generated method stub

	}

	public void exitType(int endOffset) {
		typeStack.pop();
	}

}
