package org.rubypeople.rdt.internal.core.search;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.search.SearchDocument;
import org.rubypeople.rdt.internal.core.search.indexing.IndexManager;

public class BasicSearchEngine {
	
	// FIXME We're doing poor man's scoping by passing in the script. We should actually create scope classes which could tell if a document fell in our out of it...
	public static Set<String> getTypeNames(IRubyScript script) {
		return getElementNames(IRubyElement.TYPE, script);
	}

	public static Set<String> getConstantNames(IRubyScript script) {
		return getElementNames(IRubyElement.CONSTANT, script);
	}

	private static Set<String> getElementNames(int type, IRubyScript script) {
		Set<String> names = new HashSet<String>();
		Collection<SearchDocument> documents = getDocumentsInScope(script);
		for (SearchDocument doc : documents) {
			Set<String> elements = doc.getElementNamesOfType(type);
			for (String element : elements) {
				names.add(element);
			}
		}
		return names;
	}

	private static Collection<SearchDocument> getDocumentsInScope(IRubyScript script) {
		try {
			Set<SearchDocument> matches = new HashSet<SearchDocument>();
			IRubyProject project = script.getRubyProject();
			ISourceFolderRoot[] roots = project.getSourceFolderRoots();
			for (IPath path : documents().keySet()) {
				// If path is in loadpath of script's project, add it
				for (int i = 0; i < roots.length; i++) {
					if (roots[i].getPath().isPrefixOf(path)) matches.add(documents().get(path));
				}
			}
			return matches;
		} catch (RubyModelException e) {
			// ignore?
			return documents().values();
		}
	}

	public static Set<IType> findType(String name) {
		Set<IType> types = new HashSet<IType>();
		for (SearchDocument doc : documents().values()) {
			IType type = doc.findType(name);
			if (type != null)
				types.add(type);
		}
		return types;
	}
	
	private static Map<IPath, SearchDocument> documents() {
		return IndexManager.instance().documents;
	}

	public static Set<String> getGlobalNames(IRubyScript script) {
		return getElementNames(IRubyElement.GLOBAL, script);
	}
}
