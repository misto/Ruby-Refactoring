package org.rubypeople.rdt.internal.core.search;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.IField;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.RubyModelManager;

public class ExperimentalIndex implements IElementChangedListener {
	
	private static ExperimentalIndex fgInstance;
	private static HashSet<IField> fgConstants;
	private static HashSet<IType> fgTypes;
	private static HashSet<IField> fgGlobals;
	
	private ExperimentalIndex() {
		fgTypes = new HashSet<IType>();
		fgConstants = new HashSet<IField>();
		fgGlobals = new HashSet<IField>();
	}
	
	public void elementChanged(ElementChangedEvent event) {
		processDelta(event.getDelta());
	}

	public static Set<String> getTypeNames() {
		Set<IType> types = Collections.unmodifiableSet((HashSet<IType>)fgTypes.clone()); // clone to avoid concurrent modification when iterating
	    Set<String> names = new HashSet<String>();
		for (IType type : types) {
			names.add(type.getElementName());
		}
		return names;
	}
	
	public static Set<String> getConstantNames() {
		Set<IField> types = Collections.unmodifiableSet((HashSet<IField>)fgConstants.clone()); // clone to avoid concurrent modification when iterating
	    Set<String> names = new HashSet<String>();
		for (IField type : types) {
			names.add(type.getElementName());
		}
		return names;
	}
	
	public static Set<IType> findType(String name) {
		Set<IType> types = Collections.unmodifiableSet((HashSet<IType>)fgTypes.clone()); // clone to avoid concurrent modification when iterating
	    Set<IType> matches = new HashSet<IType>();
		for (IType type : types) {
			if (type.getElementName().equals(name))
			  matches.add(type);
		}
		return matches;
	}
	
	public static Set<String> getGlobalNames() {
		Set<IField> types = Collections.unmodifiableSet((HashSet<IField>)fgGlobals.clone()); // clone to avoid concurrent modification when iterating
	    Set<String> names = new HashSet<String>();
		for (IField type : types) {
			names.add(type.getElementName());
		}
		return names;
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
		switch (element.getElementType()) {
		case IRubyElement.TYPE:
			fgTypes.remove(element);
			break;
		case IRubyElement.CONSTANT:
			fgConstants.remove(element);
			break;
		case IRubyElement.GLOBAL:
			fgGlobals.remove(element);
			break;
		}
	}

	void addElement(IRubyElement element) {
		switch (element.getElementType()) {
		case IRubyElement.TYPE:
			fgTypes.add((IType)element);
			break;
		case IRubyElement.CONSTANT:
			fgConstants.add((IField)element);
			break;
		case IRubyElement.GLOBAL:
			fgGlobals.add((IField)element);
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
				RubyCore.log(e);
			}			
		}
		
	}
}
