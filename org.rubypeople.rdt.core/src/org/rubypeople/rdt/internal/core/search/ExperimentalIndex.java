package org.rubypeople.rdt.internal.core.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyModelManager;

public class ExperimentalIndex implements IElementChangedListener {

	private static List<String> fgTypes = new ArrayList<String>();
	private static ExperimentalIndex fgInstance;

	private ExperimentalIndex() {
		
	}
	
	public void elementChanged(ElementChangedEvent event) {
		processDelta(event.getDelta());
	}

	public static List<String> getTypes() {
		return Collections.unmodifiableList(fgTypes);
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
			switch (element.getElementType()) {
			case IRubyElement.TYPE:
				fgTypes.remove(element.getElementName());
				break;
			}
			break;
		case IRubyElementDelta.ADDED:
			addElement(element);
			break;
		}
	}

	void addElement(IRubyElement element) {
		switch (element.getElementType()) {
		case IRubyElement.TYPE:
			fgTypes.add(element.getElementName());
			break;
		}
	}

	public static ExperimentalIndex instance() {
		if (fgInstance == null) {
			fgInstance = new ExperimentalIndex();
		}
		return fgInstance;
	}

	public static void start() {
		Job job = new ExperimentalIndexJob(instance());
        job.schedule();
	}
	
	private static class ExperimentalIndexJob extends Job {
		private ExperimentalIndex index;

		public ExperimentalIndexJob(ExperimentalIndex index) {
			super("Experimental Index Job");
			this.index = index;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			IRubyModel model = RubyModelManager.getRubyModelManager().getRubyModel();
			addChildren(model);			
			return Status.OK_STATUS;
		}

		private void addChildren(IParent parent) {
			try {
				IRubyElement[] children = parent.getChildren();
				for (int i = 0; i < children.length; i++) {
					index.addElement(children[i]);
					if (children[i] instanceof IParent) {
						IParent newParent = (IParent) children[i];
						addChildren(newParent);
					}
				}
			} catch (RubyModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
		
	}
}
