/*
 * Created on Jan 14, 2005
 *
 */
package org.rubypeople.rdt.internal.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.WorkingCopyOwner;

/**
 * @author cawilliams
 * 
 */
public class RubyModelManager {

	/**
	 * The singleton manager
	 */
	private final static RubyModelManager MANAGER = new RubyModelManager();

	/**
	 * Unique handle onto the RubyModel
	 */
	final RubyModel rubyModel = new RubyModel();

	/*
	 * Temporary cache of newly opened elements
	 */
	private ThreadLocal temporaryCache = new ThreadLocal();

	/**
	 * Set of elements which are out of sync with their buffers.
	 */
	protected Map elementsOutOfSynchWithBuffers = new HashMap(11);

	/*
	 * A HashSet that contains the IJavaProject whose classpath is being
	 * resolved.
	 */
	private ThreadLocal classpathsBeingResolved = new ThreadLocal();

	/**
	 * Infos cache.
	 */
	protected RubyModelCache cache = new RubyModelCache();

	/**
	 * Table from IProject to PerProjectInfo. NOTE: this object itself is used
	 * as a lock to synchronize creation/removal of per project infos
	 */
	protected Map perProjectInfos = new HashMap(5);

	/**
	 * Table from WorkingCopyOwner to a table of ICompilationUnit (working copy
	 * handle) to PerWorkingCopyInfo. NOTE: this object itself is used as a lock
	 * to synchronize creation/removal of per working copy infos
	 */
	protected Map perWorkingCopyInfos = new HashMap(5);

	public static boolean VERBOSE = false;
	public static boolean CP_RESOLVE_VERBOSE = false;

	/**
	 * Constructs a new RubyModelManager
	 */
	private RubyModelManager() {
	// singleton: prevent others from creating a new instance
	}

	/**
	 * Returns the singleton RubyModelManager
	 */
	public final static RubyModelManager getRubyModelManager() {
		return MANAGER;
	}

	/**
	 * Returns the info for the element.
	 */
	public synchronized Object getInfo(IRubyElement element) {
		HashMap tempCache = (HashMap) this.temporaryCache.get();
		if (tempCache != null) {
			Object result = tempCache.get(element);
			if (result != null) { return result; }
		}
		return this.cache.getInfo(element);
	}

	/*
	 * Removes all cached info for the given element (including all children)
	 * from the cache. Returns the info for the given element, or null if it was
	 * closed.
	 */
	public synchronized Object removeInfoAndChildren(RubyElement element) throws RubyModelException {
		Object info = this.cache.peekAtInfo(element);
		if (info != null) {
			element.closing(info);
			if (element instanceof IParent && info instanceof RubyElementInfo) {
				IRubyElement[] children = ((RubyElementInfo) info).getChildren();
				for (int i = 0, size = children.length; i < size; ++i) {
					RubyElement child = (RubyElement) children[i];
					child.close();
				}
			}
			this.cache.removeInfo(element);
			return info;
		}
		return null;
	}

	/**
	 * Returns the info for this element without disturbing the cache ordering.
	 */
	protected synchronized Object peekAtInfo(IRubyElement element) {
		HashMap tempCache = (HashMap) this.temporaryCache.get();
		if (tempCache != null) {
			Object result = tempCache.get(element);
			if (result != null) { return result; }
		}
		return this.cache.peekAtInfo(element);
	}

	/*
	 * Puts the infos in the given map (keys are IRubyElements and values are
	 * RubyElementInfos) in the Ruby model cache in an atomic way. First checks
	 * that the info for the opened element (or one of its ancestors) has not
	 * been added to the cache. If it is the case, another thread has opened the
	 * element (or one of its ancestors). So returns without updating the cache.
	 */
	protected synchronized void putInfos(IRubyElement openedElement, Map newElements) {
		// remove children
		Object existingInfo = this.cache.peekAtInfo(openedElement);
		if (openedElement instanceof IParent && existingInfo instanceof RubyElementInfo) {
			IRubyElement[] children = ((RubyElementInfo) existingInfo).getChildren();
			for (int i = 0, size = children.length; i < size; ++i) {
				RubyElement child = (RubyElement) children[i];
				try {
					child.close();
				} catch (RubyModelException e) {
					// ignore
				}
			}
		}

		Iterator iterator = newElements.keySet().iterator();
		while (iterator.hasNext()) {
			IRubyElement element = (IRubyElement) iterator.next();
			Object info = newElements.get(element);
			this.cache.putInfo(element, info);
		}
	}

	/**
	 * Returns the temporary cache for newly opened elements for the current
	 * thread. Creates it if not already created.
	 */
	public HashMap getTemporaryCache() {
		HashMap result = (HashMap) this.temporaryCache.get();
		if (result == null) {
			result = new HashMap();
			this.temporaryCache.set(result);
		}
		return result;
	}

	/*
	 * Returns whether there is a temporary cache for the current thread.
	 */
	public boolean hasTemporaryCache() {
		return this.temporaryCache.get() != null;
	}

	/*
	 * Resets the temporary cache for newly created elements to null.
	 */
	public void resetTemporaryCache() {
		this.temporaryCache.set(null);
	}

	/**
	 * Returns the handle to the active Ruby Model.
	 */
	public final RubyModel getRubyModel() {
		return this.rubyModel;
	}

	public static class PerWorkingCopyInfo {

		int useCount = 0;

		IRubyScript workingCopy;

		public PerWorkingCopyInfo(IRubyScript workingCopy) {
			this.workingCopy = workingCopy;
		}

		public IRubyScript getWorkingCopy() {
			return this.workingCopy;
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Info for "); //$NON-NLS-1$
			buffer.append(((RubyElement) this.workingCopy).toString());
			buffer.append("\nUse count = "); //$NON-NLS-1$
			buffer.append(this.useCount);
			return buffer.toString();
		}
	}

	/**
	 * @param script
	 * @param create
	 * @param recordUsage
	 * @param object
	 * @return
	 */
	public PerWorkingCopyInfo getPerWorkingCopyInfo(RubyScript workingCopy, boolean create, boolean recordUsage) {
		synchronized (this.perWorkingCopyInfos) { // use the
			// perWorkingCopyInfo
			// collection as its own
			// lock
			WorkingCopyOwner owner = workingCopy.owner;
			Map workingCopyToInfos = (Map) this.perWorkingCopyInfos.get(owner);
			if (workingCopyToInfos == null && create) {
				workingCopyToInfos = new HashMap();
				this.perWorkingCopyInfos.put(owner, workingCopyToInfos);
			}

			PerWorkingCopyInfo info = workingCopyToInfos == null ? null : (PerWorkingCopyInfo) workingCopyToInfos.get(workingCopy);
			if (info == null && create) {
				info = new PerWorkingCopyInfo(workingCopy);
				workingCopyToInfos.put(workingCopy, info);
			}
			if (info != null && recordUsage) info.useCount++;
			return info;
		}
	}

	/*
	 * Discards the per working copy info for the given working copy (making it
	 * a compilation unit) if its use count was 1. Otherwise, just decrement the
	 * use count. If the working copy is primary, computes the delta between its
	 * state and the original compilation unit and register it. Close the
	 * working copy, its buffer and remove it from the shared working copy
	 * table. Ignore if no per-working copy info existed. NOTE: it must NOT be
	 * synchronized as it may interact with the element info cache (if useCount
	 * is decremented to 0), see bug 50667. Returns the new use count (or -1 if
	 * it didn't exist).
	 */
	public int discardPerWorkingCopyInfo(RubyScript workingCopy) throws RubyModelException {
		PerWorkingCopyInfo info = null;
		synchronized (this.perWorkingCopyInfos) {
			WorkingCopyOwner owner = workingCopy.owner;
			Map workingCopyToInfos = (Map) this.perWorkingCopyInfos.get(owner);
			if (workingCopyToInfos == null) return -1;

			info = (PerWorkingCopyInfo) workingCopyToInfos.get(workingCopy);
			if (info == null) return -1;

			if (--info.useCount == 0) {
				// remove per working copy info
				workingCopyToInfos.remove(workingCopy);
				if (workingCopyToInfos.isEmpty()) {
					this.perWorkingCopyInfos.remove(owner);
				}
			}
		}
		if (info.useCount == 0) { // info cannot be null here (check was done
			// above)
			// remove infos + close buffer (since no longer working copy)
			// outside the perWorkingCopyInfos lock (see bug 50667)
			removeInfoAndChildren(workingCopy);
			workingCopy.closeBuffer();
		}
		return info.useCount;
	}

	/**
	 * Returns the set of elements which are out of synch with their buffers.
	 */
	protected Map getElementsOutOfSynchWithBuffers() {
		return this.elementsOutOfSynchWithBuffers;
	}

	/*
	 * Returns the per-project info for the given project. If specified, create
	 * the info if the info doesn't exist.
	 */
	public PerProjectInfo getPerProjectInfo(IProject project, boolean create) {
		synchronized (this.perProjectInfos) { // use the perProjectInfo
			// collection as its own lock
			PerProjectInfo info = (PerProjectInfo) this.perProjectInfos.get(project);
			if (info == null && create) {
				info = new PerProjectInfo(project);
				this.perProjectInfos.put(project, info);
			}
			return info;
		}
	}

	public void removePerProjectInfo(RubyProject rubyProject) {
		synchronized (this.perProjectInfos) { // use the perProjectInfo
			// collection as its own lock
			IProject project = rubyProject.getProject();
			PerProjectInfo info = (PerProjectInfo) this.perProjectInfos.get(project);
			if (info != null) {
				this.perProjectInfos.remove(project);
			}
		}
	}

	public boolean isLoadpathBeingResolved(IRubyProject project) {
		return getLoadpathBeingResolved().contains(project);
	}

	private HashSet getLoadpathBeingResolved() {
		HashSet result = (HashSet) this.classpathsBeingResolved.get();
		if (result == null) {
			result = new HashSet();
			this.classpathsBeingResolved.set(result);
		}
		return result;
	}

	public static class PerProjectInfo {

		public IProject project;
		public Object savedState;
		public boolean triedRead;
		public ILoadpathEntry[] rawClasspath;
		public ILoadpathEntry[] resolvedClasspath;
		public Map resolvedPathToRawEntries; // reverse map from resolved
		// path to raw entries
		public IPath outputLocation;

		public IEclipsePreferences preferences;

		public PerProjectInfo(IProject project) {

			this.triedRead = false;
			this.savedState = null;
			this.project = project;
		}

		// updating raw classpath need to flush obsoleted cached information
		// about resolved entries
		public synchronized void updateClasspathInformation(ILoadpathEntry[] newRawClasspath) {

			this.rawClasspath = newRawClasspath;
			this.resolvedClasspath = null;
			this.resolvedPathToRawEntries = null;
		}

		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Info for "); //$NON-NLS-1$
			buffer.append(this.project.getFullPath());
			buffer.append("\nRaw classpath:\n"); //$NON-NLS-1$
			if (this.rawClasspath == null) {
				buffer.append("  <null>\n"); //$NON-NLS-1$
			} else {
				for (int i = 0, length = this.rawClasspath.length; i < length; i++) {
					buffer.append("  "); //$NON-NLS-1$
					buffer.append(this.rawClasspath[i]);
					buffer.append('\n');
				}
			}
			buffer.append("Resolved classpath:\n"); //$NON-NLS-1$
			ILoadpathEntry[] resolvedCP = this.resolvedClasspath;
			if (resolvedCP == null) {
				buffer.append("  <null>\n"); //$NON-NLS-1$
			} else {
				for (int i = 0, length = resolvedCP.length; i < length; i++) {
					buffer.append("  "); //$NON-NLS-1$
					buffer.append(resolvedCP[i]);
					buffer.append('\n');
				}
			}
			buffer.append("Output location:\n  "); //$NON-NLS-1$
			if (this.outputLocation == null) {
				buffer.append("<null>"); //$NON-NLS-1$
			} else {
				buffer.append(this.outputLocation);
			}
			return buffer.toString();
		}
	}

	/*
	 * Returns the per-project info for the given project. If the info doesn't
	 * exist, check for the project existence and create the info. @throws
	 * RubyModelException if the project doesn't exist.
	 */
	public PerProjectInfo getPerProjectInfoCheckExistence(IProject project) throws RubyModelException {
		RubyModelManager.PerProjectInfo info = getPerProjectInfo(project, false /*
																				 * don't
																				 * create
																				 * info
																				 */);
		if (info == null) {
			if (!RubyProject.hasRubyNature(project)) { throw ((RubyProject) RubyCore.create(project)).newNotPresentException(); }
			info = getPerProjectInfo(project, true /* create info */);
		}
		return info;
	}

	public void setLoadpathBeingResolved(IRubyProject project, boolean classpathIsResolved) {
		if (classpathIsResolved) {
			getLoadpathBeingResolved().add(project);
		} else {
			getLoadpathBeingResolved().remove(project);
		}
	}
}
