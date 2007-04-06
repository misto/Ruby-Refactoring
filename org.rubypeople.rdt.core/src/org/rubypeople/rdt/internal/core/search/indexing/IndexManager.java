package org.rubypeople.rdt.internal.core.search.indexing;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.search.SearchDocument;

public class IndexManager implements IElementChangedListener {

	private static IndexManager fgInstance;
	public static Map<IPath, SearchDocument> documents;

	private IndexManager() {
		documents = new HashMap<IPath, SearchDocument>();
	}

	public void elementChanged(ElementChangedEvent event) {
		processDelta(event.getDelta());
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

	void addElement(IRubyElement element) {
		if ((element.isType(IRubyElement.RUBY_MODEL)) || 
			(element.isType(IRubyElement.RUBY_PROJECT)) ||
			(element.isType(IRubyElement.SCRIPT)) ||
			(element.isType(IRubyElement.SOURCE_FOLDER_ROOT)) ||
			(element.isType(IRubyElement.SOURCE_FOLDER))) return;
		SearchDocument doc = documents.get(element.getPath());
		if (doc == null) {
			doc = new SearchDocument(element.getPath().toString());
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
