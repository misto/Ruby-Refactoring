/*
 * Created on Jan 13, 2005
 *
 */
package org.rubypeople.rdt.internal.core;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IBuffer;
import org.rubypeople.rdt.core.IOpenable;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.RubyModelException;

/**
 * @author cawilliams
 * 
 */
public abstract class SourceRefElement extends RubyElement implements ISourceReference {

	/*
	 * A count to uniquely identify this element in the case that a duplicate
	 * named element exists. For example, if there are two fields in a
	 * compilation unit with the same name, the occurrence count is used to
	 * distinguish them. The occurrence count starts at 1 (thus the first
	 * occurrence is occurrence 1, not occurrence 0).
	 */
	public int occurrenceCount = 1;

	/**
	 * @param name
	 */
	public SourceRefElement(RubyElement parent) {
		super(parent);
	}

	/**
	 * Return the first instance of IOpenable in the hierarchy of this type
	 * (going up the hierarchy from this type);
	 */
	public IOpenable getOpenableParent() {
		IRubyElement current = getParent();
		while (current != null) {
			if (current instanceof IOpenable) { return (IOpenable) current; }
			current = current.getParent();
		}
		return null;
	}
	
	/**
	 * Elements within compilation units and class files have no
	 * corresponding resource.
	 *
	 * @see IRubyElement
	 */
	public IResource getCorrespondingResource() throws RubyModelException {
		if (!exists()) throw newNotPresentException();
		return null;
	}

	/**
	 * @see IRubyElement
	 */
	public boolean isStructureKnown() throws RubyModelException {
		// structure is always known inside an openable
		return true;
	}

	/**
	 * @throws RubyModelException
	 * @see ISourceReference
	 */
	public String getSource() throws RubyModelException {
		IOpenable openable = getOpenableParent();
		IBuffer buffer = openable.getBuffer();
		if (buffer == null) { return null; }
		ISourceRange range = getSourceRange();
		int offset = range.getOffset();
		int length = range.getLength();
		if (offset == -1 || length == 0) { return null; }
		try {
			return buffer.getText(offset, length);
		} catch (RuntimeException e) {
			return null;
		}
	}

	/**
	 * @see ISourceReference
	 */
	public ISourceRange getSourceRange() throws RubyModelException {
		SourceRefElementInfo info = (SourceRefElementInfo) getElementInfo();
		return info.getSourceRange();
	}

	/*
	 * @see IRubyElement
	 */
	public IPath getPath() {
		return this.getParent().getPath();
	}

	public IResource getResource() {
		return this.getParent().getResource();
	}

	/**
	 * @see IRubyElement
	 */
	public IResource getUnderlyingResource() throws RubyModelException {
		if (!exists()) throw newNotPresentException();
		return getParent().getUnderlyingResource();
	}

	/**
	 * This element is being closed. Do any necessary cleanup.
	 */
	protected void closing(Object info) throws RubyModelException {
	// Do any necessary cleanup
	}

	/*
	 * @see RubyElement#generateInfos
	 */
	protected void generateInfos(Object info, HashMap newElements, IProgressMonitor pm) throws RubyModelException {
		Openable openableParent = (Openable) getOpenableParent();
		if (openableParent == null) return;

		RubyElementInfo openableParentInfo = (RubyElementInfo) RubyModelManager.getRubyModelManager().getInfo(openableParent);
		if (openableParentInfo == null) {
			openableParent.generateInfos(openableParent.createElementInfo(), newElements, pm);
		}
	}

	/**
	 * Returns a new element info for this element.
	 */
	protected Object createElementInfo() {
		return null; // not used for source ref elements
	}

	public boolean equals(Object o) {
		if (!(o instanceof SourceRefElement)) return false;
		return this.occurrenceCount == ((SourceRefElement) o).occurrenceCount && super.equals(o);
	}

}
