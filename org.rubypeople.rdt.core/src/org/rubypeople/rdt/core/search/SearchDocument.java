package org.rubypeople.rdt.core.search;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.Openable;
import org.rubypeople.rdt.internal.core.search.HandleFactory;
import org.rubypeople.rdt.internal.core.search.indexing.InternalSearchDocument;

public class SearchDocument extends InternalSearchDocument {
	
	private static HandleFactory factory = new HandleFactory();
	private IRubyScript script;
	
	private static final String SEPARATOR = "/";
	
	private List<String> indices = new ArrayList<String>();
	
	private String documentPath;

	public SearchDocument(String documentPath) {
		this.documentPath = documentPath;
	}
	
	public void addIndexEntry(char[] category, char[] key) {
		super.addIndexEntry(category, key);
	}
	
	/**
	 * Removes all index entries from the index for the given document.
	 * This method must be called from 
	 * {@link SearchParticipant#indexDocument(SearchDocument document, org.eclipse.core.runtime.IPath indexPath)}.
	 */
	public void removeAllIndexEntries() {
		super.removeAllIndexEntries();
	}

	public Set<String> getElementNamesOfType(int type) {
		Set<String> names = new HashSet<String>();
		for (String indexKey : indices) {
			if (getTypeFromKey(indexKey) != type) continue;
			names.add(getNameFromKey(indexKey));
		}
		return names;
	}

	public List<IRubyElement> getElementsOfType(int type) {
		IRubyScript script = getScript();
		return getChildrenOfType(script, type);
	}

	private IRubyScript getScript() {
		if (this.script == null) {
			Openable openable = factory.createOpenable(documentPath);
			this.script = (IRubyScript) openable;
		}
		return this.script;
	}	

	private List<IRubyElement> getChildrenOfType(IParent parent, int type) {
		List<IRubyElement> elements = new ArrayList<IRubyElement>();
		if (parent == null) return elements;
		try {
			IRubyElement[] children = parent.getChildren();
			if (children == null)
				return elements;
			for (int i = 0; i < children.length; i++) {
				if (children[i].isType(type))
					elements.add(children[i]);
				if (children[i] instanceof IParent) {
					IParent childParent = (IParent) children[i];
					elements.addAll(getChildrenOfType(childParent, type));
				}
			}
		} catch (RubyModelException e) {
			// ignore
		}
		return elements;
	}

	public boolean isEmpty() {
		return indices.isEmpty();
	}

	public void removeElement(IRubyElement element) {
		indices.remove(createKey(element));
	}

	private String createKey(IRubyElement element) {
		return createKey(element.getElementType(), element.getElementName());
	}

	private String createKey(int type, String name) {
		return type + SEPARATOR + name;
	}

	public void addElement(IRubyElement element) {
		indices.add(createKey(element));
	}

	public IType findType(String name) {
		return (IType) findElement(createKey(IRubyElement.TYPE, name));
	}

	private IRubyElement findElement(String key) {
		for (String indexKey : indices) {
			if (!indexKey.equals(key))
				continue;
			IRubyScript script = getScript();
			List<IRubyElement> children = getChildrenOfType(script, getTypeFromKey(key));
			for (IRubyElement element : children) {
				if (element.getElementName().equals(getNameFromKey(key)))
					return element;
			}
		}
		return null;
	}

	private String getNameFromKey(String key) {
		String[] parts = key.split(SEPARATOR);
		return parts[1];
	}

	private int getTypeFromKey(String key) {
		String[] parts = key.split(SEPARATOR);
		return Integer.parseInt(parts[0]);
	}
}
