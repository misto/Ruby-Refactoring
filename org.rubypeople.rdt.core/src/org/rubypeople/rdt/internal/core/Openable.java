/*
 * Created on Jan 13, 2005

 */
package org.rubypeople.rdt.internal.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.rubypeople.rdt.core.BufferChangedEvent;
import org.rubypeople.rdt.core.IBuffer;
import org.rubypeople.rdt.core.IBufferChangedListener;
import org.rubypeople.rdt.core.IOpenable;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModelStatusConstants;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.buffer.BufferManager;

/**
 * @author cawilliams
 * 
 */
public abstract class Openable extends RubyElement implements IOpenable, IBufferChangedListener {

	/**
	 * @param parent
	 */
	public Openable(RubyElement parent) {
		super(parent);
	}

	/**
	 * The buffer associated with this element has changed. Registers this
	 * element as being out of synch with its buffer's contents. If the buffer
	 * has been closed, this element is set as NOT out of synch with the
	 * contents.
	 * 
	 * @see IBufferChangedListener
	 */
	public void bufferChanged(BufferChangedEvent event) {
		if (event.getBuffer().isClosed()) {
			RubyModelManager.getRubyModelManager().getElementsOutOfSynchWithBuffers().remove(this);
			getBufferManager().removeBuffer(event.getBuffer());
		} else {
			RubyModelManager.getRubyModelManager().getElementsOutOfSynchWithBuffers().put(this, this);
		}
	}

	/**
	 * Note: a buffer with no unsaved changes can be closed by the Ruby Model
	 * since it has a finite number of buffers allowed open at one time. If this
	 * is the first time a request is being made for the buffer, an attempt is
	 * made to create and fill this element's buffer. If the buffer has been
	 * closed since it was first opened, the buffer is re-created.
	 * 
	 * @see IOpenable
	 */
	public IBuffer getBuffer() throws RubyModelException {
		if (hasBuffer()) {
			// ensure element is open
			// Object info = getElementInfo();
			IBuffer buffer = getBufferManager().getBuffer(this);
			if (buffer == null) {
				// try to (re)open a buffer
				buffer = openBuffer(null);
			}
			return buffer;
		}
		return null;
	}

	/**
	 * Opens a buffer on the contents of this element, and returns the buffer,
	 * or returns <code>null</code> if opening fails. By default, do nothing -
	 * subclasses that have buffers must override as required.
	 */
	protected IBuffer openBuffer(IProgressMonitor pm) {
		return null;
	}

	/*
	 * Returns whether the buffer of this element can be removed from the Ruby
	 * model cache to make space.
	 */
	public boolean canBufferBeRemovedFromCache(IBuffer buffer) {
		return !buffer.hasUnsavedChanges();
	}

	/**
	 * Returns true if this element may have an associated source buffer,
	 * otherwise false. Subclasses must override as required.
	 */
	protected boolean hasBuffer() {
		return false;
	}

	/**
	 * 
	 * @see IOpenable
	 */
	public boolean isOpen() {
		return RubyModelManager.getRubyModelManager().getInfo(this) != null;
	}

	/**
	 * Returns the buffer manager for this element.
	 */
	protected BufferManager getBufferManager() {
		return BufferManager.getDefaultBufferManager();
	}

	/*
	 * Returns whether this element can be removed from the Ruby model cache to
	 * make space.
	 */
	public boolean canBeRemovedFromCache() {
		try {
			return !hasUnsavedChanges();
		} catch (RubyModelException e) {
			return false;
		}
	}

	/**
	 * Subclasses must override as required.
	 * 
	 * @see IOpenable
	 */
	public boolean isConsistent() {
		return true;
	}

	/**
	 * @see IRubyElement
	 */
	public boolean isStructureKnown() throws RubyModelException {
		return ((OpenableElementInfo) getElementInfo()).isStructureKnown();
	}

	/**
	 * @see IOpenable
	 */
	public boolean hasUnsavedChanges() throws RubyModelException {
		if (isReadOnly() || !isOpen()) { return false; }
		IBuffer buf = this.getBuffer();
		if (buf != null && buf.hasUnsavedChanges()) { return true; }
		// for package fragments, package fragment roots, and projects must
		// check open buffers
		// to see if they have an child with unsaved changes
		int elementType = getElementType();
		if (elementType == PROJECT || elementType == RUBY_MODEL) { // fix for
			// 1FWNMHH
			Enumeration openBuffers = getBufferManager().getOpenBuffers();
			while (openBuffers.hasMoreElements()) {
				IBuffer buffer = (IBuffer) openBuffers.nextElement();
				if (buffer.hasUnsavedChanges()) {
					IRubyElement owner = (IRubyElement) buffer.getOwner();
					if (isAncestorOf(owner)) { return true; }
				}
			}
		}

		return false;
	}

	/**
	 * This element is being closed. Do any necessary cleanup.
	 */
	protected void closing(Object info) {
		closeBuffer();
	}

	/**
	 * @see IRubyElement
	 */
	public boolean exists() {
		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		if (manager.getInfo(this) != null) return true;
		if (!parentExists()) return false;
		return super.exists();
	}

	/**
	 * Answers true if the parent exists (null parent is answering true)
	 * 
	 */
	protected boolean parentExists() {
		IRubyElement parentElement = getParent();
		if (parentElement == null) return true;
		return parentElement.exists();
	}

	/**
	 * Close the buffer associated with this element, if any.
	 */
	protected void closeBuffer() {
		if (!hasBuffer()) return; // nothing to do
		IBuffer buffer = getBufferManager().getBuffer(this);
		if (buffer != null) {
			buffer.close();
			buffer.removeBufferChangedListener(this);
		}
	}

	protected void generateInfos(Object info, HashMap newElements, IProgressMonitor monitor) throws RubyModelException {

		if (RubyModelManager.VERBOSE) {
			String element;
			switch (getElementType()) {
			case PROJECT:
				element = "project"; //$NON-NLS-1$
				break;
			case SCRIPT:
				element = "script"; //$NON-NLS-1$
				break;
			default:
				element = "element"; //$NON-NLS-1$
			}
			System.out.println(Thread.currentThread() + " OPENING " + element + " " + this.toString()); //$NON-NLS-1$//$NON-NLS-2$
		}

		// open the parent if necessary
		openParent(info, newElements, monitor);
		if (monitor != null && monitor.isCanceled()) throw new OperationCanceledException();

		// puts the info before building the structure so that questions to the
		// handle behave as if the element existed
		// (case of compilation units becoming working copies)
		newElements.put(this, info);

		// build the structure of the openable (this will open the buffer if
		// needed)
		try {
			OpenableElementInfo openableElementInfo = (OpenableElementInfo) info;
			boolean isStructureKnown = buildStructure(openableElementInfo, monitor, newElements, getResource());
			openableElementInfo.setIsStructureKnown(isStructureKnown);
		} catch (RubyModelException e) {
			newElements.remove(this);
			throw e;
		}

		// remove out of sync buffer for this element
		RubyModelManager.getRubyModelManager().getElementsOutOfSynchWithBuffers().remove(this);

		if (RubyModelManager.VERBOSE) {
			System.out.println(RubyModelManager.getRubyModelManager().cache.toStringFillingRation("-> ")); //$NON-NLS-1$
		}
	}

	/**
	 * Builds this element's structure and properties in the given info object,
	 * based on this element's current contents (reuse buffer contents if this
	 * element has an open buffer, or resource contents if this element does not
	 * have an open buffer). Children are placed in the given newElements table
	 * (note, this element has already been placed in the newElements table).
	 * Returns true if successful, or false if an error is encountered while
	 * determining the structure of this element.
	 */
	protected abstract boolean buildStructure(OpenableElementInfo info, IProgressMonitor pm, Map newElements, IResource underlyingResource) throws RubyModelException;

	/**
	 * Open the parent element if necessary.
	 * 
	 * @param newElements
	 * @param info
	 */
	protected void openParent(Object info, HashMap newElements, IProgressMonitor pm) throws RubyModelException {
		Openable openableParent = (Openable) getOpenableParent();
		if (openableParent != null && !openableParent.isOpen()) {
			openableParent.generateInfos(openableParent.createElementInfo(), newElements, pm);
		}
	}

	/**
	 * @see IOpenable
	 */
	public void makeConsistent(IProgressMonitor monitor) throws RubyModelException {
		if (isConsistent()) return;

		// create a new info and make it the current info
		// (this will remove the info and its children just before storing the
		// new infos)
		RubyModelManager manager = RubyModelManager.getRubyModelManager();
		boolean hadTemporaryCache = manager.hasTemporaryCache();
		try {
			HashMap newElements = manager.getTemporaryCache();
			openWhenClosed(newElements, monitor);
			if (newElements.get(this) == null) {
				// close any buffer that was opened for the new elements
				Iterator iterator = newElements.keySet().iterator();
				while (iterator.hasNext()) {
					IRubyElement element = (IRubyElement) iterator.next();
					if (element instanceof Openable) {
						((Openable) element).closeBuffer();
					}
				}
				throw newNotPresentException();
			}
			if (!hadTemporaryCache) {
				manager.putInfos(this, newElements);
			}
		} finally {
			if (!hadTemporaryCache) {
				manager.resetTemporaryCache();
			}
		}
	}

	/**
	 * @see IOpenable
	 */
	public void save(IProgressMonitor pm, boolean force) throws RubyModelException {
		if (isReadOnly()) { throw new RubyModelException(new RubyModelStatus(IRubyModelStatusConstants.READ_ONLY, this)); }
		IBuffer buf = getBuffer();
		if (buf != null) { // some Openables (like a RubyProject) don't have a
			// buffer
			buf.save(pm, force);
			this.makeConsistent(pm); // update the element info of this
			// element
		}
	}

	/**
	 * @see IOpenable
	 */
	public void open(IProgressMonitor pm) throws RubyModelException {
		getElementInfo(pm);
	}
}
