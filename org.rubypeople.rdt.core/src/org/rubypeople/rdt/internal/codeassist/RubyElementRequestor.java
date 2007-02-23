package org.rubypeople.rdt.internal.codeassist;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.IPath;
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
import org.rubypeople.rdt.internal.core.search.ExperimentalIndex;
import org.rubypeople.rdt.internal.core.util.Util;

public class RubyElementRequestor {

	private static final String SEPARATOR_CHARS = "\\/";
	private static final String RUBY_FILE_EXTENSION = ".rb";
	private IRubyScript script;

	public RubyElementRequestor(IRubyScript script) {
		this.script = script;
	}

	public IType[] findType(String fullyQualifiedName) {
		IType[] types = findTypeWithSimpleName(Util.getSimpleName(fullyQualifiedName));
		List<IType> matches = new ArrayList<IType>();
		for (int i = 0; i < types.length; i++) {
			if (Util.parentsMatch(types[i], fullyQualifiedName)) matches.add(types[i]);
		}
		return matches.toArray(new IType[matches.size()]);		
	}
		
	private IType[] findTypeWithSimpleName(String typeName) {
		List<IType> types = new ArrayList<IType>();
		IRubyProject rubyProject = script.getRubyProject();
		try {
			// FIXME Search the roots in a particular order? Return first match?
			ISourceFolderRoot[] roots = rubyProject.getSourceFolderRoots();
			for (int i = 0; i < roots.length; i++) {
				types.addAll(getImportedTypesInSourceFolderRoot(roots[i], typeName));
			}
			if (types.size() == 0) { // Couldn't find any!
				// Do a full search
				types.addAll(ExperimentalIndex.findType(typeName));
			}
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
		return (IType[]) types.toArray(new IType[types.size()]);
	}

	private List<IType> filterToMatches(String typeName, List<IType> types) {
		List<IType> matches = new ArrayList<IType>();
		for (IType type : types) {
			if (type.getElementName().equals(typeName)) matches.add(type);
		}
		return matches;
	}

	private List<IType> getImportedTypesInSourceFolderRoot(ISourceFolderRoot root, String typeName) {
		List<IType> types = new ArrayList<IType>();
		try {
			IPath rootPath = root.getPath();
//	FIXME this is an ugly hack to search the core library in a special way (no need to look at imports)
			if (rootPath.toString().contains("org.rubypeople.rdt.launching")) {
				types.addAll(getTypesInImport(root, typeName.toLowerCase()));
			} else {			
				IImportDeclaration[] imports = script.getImports();
				for (int j = 0; j < imports.length; j++) {
					String path = imports[j].getElementName();
					types.addAll(getTypesInImport(root, path));		
				}
			}
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}
		
		return filterToMatches(typeName, types);
	}
	
	/**
	 * Searches the root for the path given (and appends the typical ".rb" extension). 
	 * If we find a match, grab the types inside the script.
	 * @param root The ISourceFolderRoot to search
	 * @param path The internal path to search.
	 * @return a List of ITypes which seem to be a match
	 */
	private List<IType> getTypesInImport(ISourceFolderRoot root, String path) {
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
