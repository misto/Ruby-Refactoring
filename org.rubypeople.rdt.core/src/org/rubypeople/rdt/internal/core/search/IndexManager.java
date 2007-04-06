package org.rubypeople.rdt.internal.core.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceFolderRoot;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.search.indexing.IndexAllJob;

public class IndexManager implements IElementChangedListener {

	private static IndexManager fgInstance;
	private static Map<IPath, SearchDocument> documents;

	private IndexManager() {
		documents = new HashMap<IPath, SearchDocument>();
	}

	public void elementChanged(ElementChangedEvent event) {
		processDelta(event.getDelta());
	}
	
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
			for (IPath path : documents.keySet()) {
				// If path is in loadpath of script's project, add it
				for (int i = 0; i < roots.length; i++) {
					if (roots[i].getPath().isPrefixOf(path)) matches.add(documents.get(path));
				}
			}
			return matches;
		} catch (RubyModelException e) {
			// ignore?
			return documents.values();
		}
	}

	public static Set<IType> findType(String name) {
		Set<IType> types = new HashSet<IType>();
		for (SearchDocument doc : documents.values()) {
			IType type = doc.findType(name);
			if (type != null)
				types.add(type);
		}
		return types;
	}

	public static Set<String> getGlobalNames(IRubyScript script) {
		return getElementNames(IRubyElement.GLOBAL, script);
	}

	private void processDelta(IRubyElementDelta delta) {
		IRubyElement element = delta.getElement();
		switch (delta.getKind()) {
		case IRubyElementDelta.CHANGED:
			IRubyElementDelta[] children = delta.getAffectedChildren();
			for (int i = 0, length = children.length; i < length; i++) {
				IRubyElementDelta child = children[i];
				this.processDelta(child);
			}
			break;
		case IRubyElementDelta.REMOVED:
			removeElement(element);
			break;
		case IRubyElementDelta.ADDED:
			addElement(element);
			break;
		}
	}

	void removeElement(IRubyElement element) {
		if ((element.isType(IRubyElement.RUBY_MODEL)) || 
				(element.isType(IRubyElement.RUBY_PROJECT)) ||
				(element.isType(IRubyElement.SCRIPT)) ||
				(element.isType(IRubyElement.SOURCE_FOLDER_ROOT)) ||
				(element.isType(IRubyElement.SOURCE_FOLDER))) return;
		SearchDocument doc = documents.get(element.getPath());
		if (doc == null)
			return;
		doc.removeElement(element);
		if (doc.isEmpty())
			documents.remove(element.getPath());
	}

	public void addElement(IRubyElement element) {
		if ((element.isType(IRubyElement.RUBY_MODEL)) || 
			(element.isType(IRubyElement.RUBY_PROJECT)) ||
			(element.isType(IRubyElement.SCRIPT)) ||
			(element.isType(IRubyElement.SOURCE_FOLDER_ROOT)) ||
			(element.isType(IRubyElement.SOURCE_FOLDER))) return;
		SearchDocument doc = documents.get(element.getPath());
		if (doc == null) {
			doc = new SearchDocument(element.getPath());
			documents.put(element.getPath(), doc);
		}
		doc.addElement(element);
	}

	public static IndexManager instance() {
		if (fgInstance == null) {
			fgInstance = new IndexManager();
		}
		return fgInstance;
	}

	public static void start() {
		Job job = new IndexAllJob(instance());
		job.schedule();
	}
}
