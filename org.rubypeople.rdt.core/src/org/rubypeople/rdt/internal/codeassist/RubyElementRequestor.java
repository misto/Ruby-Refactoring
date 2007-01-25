package org.rubypeople.rdt.internal.codeassist;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.rubypeople.rdt.core.IImportDeclaration;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolder;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;

public class RubyElementRequestor {

	private static final String SEPARATOR_CHARS = "\\/";
	private static final String RUBY_FILE_EXTENSION = ".rb";
	private IRubyScript script;

	public RubyElementRequestor(IRubyScript script) {
		this.script = script;
	}

	public IType[] findType(String typeName) {
		List<IType> types = new ArrayList<IType>();
		IRubyProject rubyProject = script.getRubyProject();
		try {
			ISourceFolderRoot[] roots = rubyProject.getSourceFolderRoots();
			for (int i = 0; i < roots.length; i++) {
				types.addAll(getTypeInSourceFolderRoot(roots[i]));
			}
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
		List<IType> matches = new ArrayList<IType>();
		for (IType type : types) {
			if (type.getElementName().equals(typeName)) matches.add(type);
		}
		return (IType[]) types.toArray(new IType[matches.size()]);
	}

	private List<IType> getTypeInSourceFolderRoot(ISourceFolderRoot root) {
		List<IType> types = new ArrayList<IType>();
		try {
			IImportDeclaration[] imports = script.getImports();
			for (int j = 0; j < imports.length; j++) {
				types.addAll(getTypeInImport(root, imports[j]));		
			}
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
		return types;
	}
	private List<IType> getTypeInImport(ISourceFolderRoot root, IImportDeclaration importDecl) {
		String path = importDecl.getElementName();
		StringTokenizer tokenizer = new StringTokenizer(path, SEPARATOR_CHARS);
		List<String> tokens = new ArrayList<String>();
		while(tokenizer.hasMoreTokens()) {
			tokens.add(tokenizer.nextToken());
		}					
		String name = tokens.remove(tokens.size() - 1) + RUBY_FILE_EXTENSION;
		String[] pckgs =  (String[]) tokens.toArray(new String[tokens.size()]);
		ISourceFolder folder = root.getSourceFolder(pckgs);
		if (!folder.exists()) return new ArrayList<IType>();
		IRubyScript otherScript = folder.getRubyScript(name);
		if (!otherScript.exists()) return new ArrayList<IType>();
		return getTypes(otherScript);		
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
			// ignore
		}
		return types;
	}
}
