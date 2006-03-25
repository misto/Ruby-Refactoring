package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.PerformanceStats;
import org.eclipse.core.runtime.Platform;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.core.builder.RubyBuilder;
import org.rubypeople.rdt.internal.core.util.Util;

public class DeltaProcessor {

    public static final int DEFAULT_CHANGE_EVENT = 0; // must not collide with
    private final static int NON_RUBY_RESOURCE = -1;
    // ElementChangedEvent
    // event masks
    public static boolean DEBUG;
    public static boolean VERBOSE;
    public static boolean PERF = false;

    /*
     * Used to update the RubyModel for <code>IRubyElementDelta</code>s.
     */
    private final ModelUpdater modelUpdater = new ModelUpdater();

    /* A set of IRubyProject whose caches need to be reset */
    private HashSet projectCachesToReset = new HashSet();

    /*
     * A list of IRubyElement used as a scope for external archives refresh
     * during POST_CHANGE. This is null if no refresh is needed.
     */
    private HashSet refreshedElements;

    private DeltaProcessingState state;
    private RubyModelManager manager;

    /*
     * Turns delta firing on/off. By default it is on.
     */
    private boolean isFiring = true;

    /*
     * Queue of deltas created explicily by the Ruby Model that have yet to be
     * fired.
     */
    public ArrayList javaModelDeltas = new ArrayList();

    /*
     * Queue of reconcile deltas on working copies that have yet to be fired.
     * This is a table form IWorkingCopy to IRubyElementDelta
     */
    public HashMap reconcileDeltas = new HashMap();

    /*
     * The ruby element that was last created (see createElement(IResource)).
     * This is used as a stack of ruby elements (using getParent() to pop it,
     * and using the various get*(...) to push it.
     */
    private Openable currentElement;

    /*
     * The <code>RubyElementDelta</code> corresponding to the <code>IResourceDelta</code>
     * being translated.
     */
    private RubyElementDelta currentDelta;

    /*
     * Type of event that should be processed no matter what the real event type
     * is.
     */
    public int overridenEventType = -1;

    public DeltaProcessor(DeltaProcessingState state, RubyModelManager manager) {
        this.state = state;
        this.manager = manager;
    }

    public void registerRubyModelDelta(IRubyElementDelta delta) {
        this.javaModelDeltas.add(delta);
    }

    public void updateRubyModel(IRubyElementDelta customDelta) {
        if (customDelta == null) {
            for (int i = 0, length = this.javaModelDeltas.size(); i < length; i++) {
                IRubyElementDelta delta = (IRubyElementDelta) this.javaModelDeltas.get(i);
                this.modelUpdater.processRubyDelta(delta);
            }
        } else {
            this.modelUpdater.processRubyDelta(customDelta);
        }

    }

    /*
     * Fire Java Model delta, flushing them after the fact after post_change
     * notification. If the firing mode has been turned off, this has no effect.
     */
    public void fire(IRubyElementDelta customDelta, int eventType) {
        if (!this.isFiring) return;

        if (DEBUG) {
            System.out
                    .println("-----------------------------------------------------------------------------------------------------------------------");//$NON-NLS-1$
        }

        IRubyElementDelta deltaToNotify;
        if (customDelta == null) {
            deltaToNotify = this.mergeDeltas(this.javaModelDeltas);
        } else {
            deltaToNotify = customDelta;
        }

        // Refresh internal scopes
        // TODO Notify Search scopes of deltas
        // if (deltaToNotify != null) {
        // Iterator scopes = this.manager.searchScopes.keySet().iterator();
        // while (scopes.hasNext()) {
        // AbstractSearchScope scope = (AbstractSearchScope) scopes.next();
        // scope.processDelta(deltaToNotify);
        // }
        // RubyWorkspaceScope workspaceScope = this.manager.workspaceScope;
        // if (workspaceScope != null)
        // workspaceScope.processDelta(deltaToNotify);
        // }

        // Notification

        // Important: if any listener reacts to notification by updating the
        // listeners list or mask, these lists will
        // be duplicated, so it is necessary to remember original lists in a
        // variable (since field values may change under us)
        IElementChangedListener[] listeners = this.state.elementChangedListeners;
        int[] listenerMask = this.state.elementChangedListenerMasks;
        int listenerCount = this.state.elementChangedListenerCount;

        switch (eventType) {
        case DEFAULT_CHANGE_EVENT:
            firePostChangeDelta(deltaToNotify, listeners, listenerMask, listenerCount);
            fireReconcileDelta(listeners, listenerMask, listenerCount);
            break;
        case ElementChangedEvent.POST_CHANGE:
            firePostChangeDelta(deltaToNotify, listeners, listenerMask, listenerCount);
            fireReconcileDelta(listeners, listenerMask, listenerCount);
            break;
        }
    }

    /*
     * Merges all awaiting deltas.
     */
    private IRubyElementDelta mergeDeltas(Collection deltas) {
        if (deltas.size() == 0) return null;
        if (deltas.size() == 1) return (IRubyElementDelta) deltas.iterator().next();

        if (VERBOSE) {
            System.out
                    .println("MERGING " + deltas.size() + " DELTAS [" + Thread.currentThread() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        Iterator iterator = deltas.iterator();
        RubyElementDelta rootDelta = new RubyElementDelta(this.manager.rubyModel);
        boolean insertedTree = false;
        while (iterator.hasNext()) {
            RubyElementDelta delta = (RubyElementDelta) iterator.next();
            if (VERBOSE) {
                System.out.println(delta.toString());
            }
            IRubyElement element = delta.getElement();
            if (this.manager.rubyModel.equals(element)) {
                IRubyElementDelta[] children = delta.getAffectedChildren();
                for (int j = 0; j < children.length; j++) {
                    RubyElementDelta projectDelta = (RubyElementDelta) children[j];
                    rootDelta.insertDeltaTree(projectDelta.getElement(), projectDelta);
                    insertedTree = true;
                }
                IResourceDelta[] resourceDeltas = delta.getResourceDeltas();
                if (resourceDeltas != null) {
                    for (int i = 0, length = resourceDeltas.length; i < length; i++) {
                        rootDelta.addResourceDelta(resourceDeltas[i]);
                        insertedTree = true;
                    }
                }
            } else {
                rootDelta.insertDeltaTree(element, delta);
                insertedTree = true;
            }
        }
        if (insertedTree) return rootDelta;
        return null;
    }

    private void firePostChangeDelta(IRubyElementDelta deltaToNotify,
            IElementChangedListener[] listeners, int[] listenerMask, int listenerCount) {

        // post change deltas
        if (DEBUG) {
            System.out.println("FIRING POST_CHANGE Delta [" + Thread.currentThread() + "]:"); //$NON-NLS-1$//$NON-NLS-2$
            System.out.println(deltaToNotify == null ? "<NONE>" : deltaToNotify.toString()); //$NON-NLS-1$
        }
        if (deltaToNotify != null) {
            // flush now so as to keep listener reactions to post their own
            // deltas for subsequent iteration
            this.flush();

            notifyListeners(deltaToNotify, ElementChangedEvent.POST_CHANGE, listeners,
                    listenerMask, listenerCount);
        }
    }

    private void fireReconcileDelta(IElementChangedListener[] listeners, int[] listenerMask,
            int listenerCount) {

        IRubyElementDelta deltaToNotify = mergeDeltas(this.reconcileDeltas.values());
        if (DEBUG) {
            System.out.println("FIRING POST_RECONCILE Delta [" + Thread.currentThread() + "]:"); //$NON-NLS-1$//$NON-NLS-2$
            System.out.println(deltaToNotify == null ? "<NONE>" : deltaToNotify.toString()); //$NON-NLS-1$
        }
        if (deltaToNotify != null) {
            // flush now so as to keep listener reactions to post their own
            // deltas for subsequent iteration
            this.reconcileDeltas = new HashMap();

            notifyListeners(deltaToNotify, ElementChangedEvent.POST_RECONCILE, listeners,
                    listenerMask, listenerCount);
        }
    }

    /*
     * Flushes all deltas without firing them.
     */
    public void flush() {
        this.javaModelDeltas = new ArrayList();
    }

    private void notifyListeners(IRubyElementDelta deltaToNotify, int eventType,
            IElementChangedListener[] listeners, int[] listenerMask, int listenerCount) {
        final ElementChangedEvent extraEvent = new ElementChangedEvent(deltaToNotify, eventType);
        for (int i = 0; i < listenerCount; i++) {
            if ((listenerMask[i] & eventType) != 0) {
                final IElementChangedListener listener = listeners[i];
                long start = -1;
                if (VERBOSE) {
                    System.out.print("Listener #" + (i + 1) + "=" + listener.toString());//$NON-NLS-1$//$NON-NLS-2$
                    start = System.currentTimeMillis();
                }
                // wrap callbacks with Safe runnable for subsequent listeners to
                // be called when some are causing grief
                Platform.run(new ISafeRunnable() {

                    public void handleException(Throwable exception) {
                        Util
                                .log(exception,
                                        "Exception occurred in listener of Java element change notification"); //$NON-NLS-1$
                    }

                    public void run() throws Exception {
                        PerformanceStats stats = null;
                        if (PERF) {
                            stats = PerformanceStats.getStats(RubyModelManager.DELTA_LISTENER_PERF,
                                    listener);
                            stats.startRun();
                        }
                        listener.elementChanged(extraEvent);
                        if (PERF) {
                            stats.endRun();
                        }
                    }
                });
                if (VERBOSE) {
                    System.out.println(" -> " + (System.currentTimeMillis() - start) + "ms"); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }

    /*
     * Notification that some resource changes have happened on the platform,
     * and that the Ruby Model should update any required internal structures
     * such that its elements remain consistent. Translates <code>IResourceDeltas</code>
     * into <code>IRubyElementDeltas</code>.
     * 
     * @see IResourceDelta
     * @see IResource
     */
    public void resourceChanged(IResourceChangeEvent event) {
        if (event.getSource() instanceof IWorkspace) {
            int eventType = this.overridenEventType == -1 ? event.getType()
                    : this.overridenEventType;
            IResource resource = event.getResource();
            IResourceDelta delta = event.getDelta();

            switch (eventType) {
            case IResourceChangeEvent.PRE_DELETE:
                try {
                    if (resource.getType() == IResource.PROJECT
                            && ((IProject) resource).hasNature(RubyCore.NATURE_ID)) {

                        deleting((IProject) resource);
                    }
                } catch (CoreException e) {
                    // project doesn't exist or is not open: ignore
                }
                return;

            case IResourceChangeEvent.POST_CHANGE:
                if (isAffectedBy(delta)) { // avoid populating for SYNC or
                    // MARKER deltas
                    try {
                        try {
                            stopDeltas();
                            checkProjectsBeingAddedOrRemoved(delta);
                            IRubyElementDelta translatedDelta = processResourceDelta(delta);
                            if (translatedDelta != null) {
                                registerRubyModelDelta(translatedDelta);
                            }
                        } finally {
                            startDeltas();
                        }
                        // notifyTypeHierarchies(this.state.elementChangedListeners,
                        // this.state.elementChangedListenerCount);
                        fire(null, ElementChangedEvent.POST_CHANGE);
                    } finally {
                        // workaround for bug 15168 circular errors not reported
                        this.state.modelProjectsCache = null;
                    }
                }
                return;
            }
        }
    }

    /*
     * Converts a <code>IResourceDelta</code> rooted in a <code>Workspace</code>
     * into the corresponding set of <code>IRubyElementDelta</code>, rooted
     * in the relevant <code>RubyModel</code>s.
     */
    private IRubyElementDelta processResourceDelta(IResourceDelta changes) {

        try {
            IRubyModel model = this.manager.getRubyModel();
            if (!model.isOpen()) {
                // force opening of ruby model so that ruby element delta are
                // reported
                try {
                    model.open(null);
                } catch (RubyModelException e) {
                    if (VERBOSE) {
                        e.printStackTrace();
                    }
                    return null;
                }
            }
            this.state.initializeRoots();
            this.currentElement = null;

            // get the workspace delta, and start processing there.
            IResourceDelta[] deltas = changes.getAffectedChildren();
            for (int i = 0; i < deltas.length; i++) {
                IResourceDelta delta = deltas[i];
                IResource res = delta.getResource();

                // find out the element type
                int elementType;
                IProject proj = (IProject) res;
                boolean wasJavaProject = this.manager.getRubyModel().findRubyProject(proj) != null;
                boolean isJavaProject = RubyProject.hasRubyNature(proj);
                if (!wasJavaProject && !isJavaProject) {
                    elementType = NON_RUBY_RESOURCE;
                } else {
                    elementType = IRubyElement.RUBY_PROJECT;
                }

                // traverse delta
                this.traverseDelta(delta, elementType);

                if (elementType == NON_RUBY_RESOURCE
                        || (wasJavaProject != isJavaProject && (delta.getKind()) == IResourceDelta.CHANGED)) { // project
                    // has
                    // changed
                    // nature
                    // (description
                    // or
                    // open/closed)
                    try {
                        // add child as non ruby resource
                        nonRubyResourcesChanged((RubyModel) model, delta);
                    } catch (RubyModelException e) {
                        // ruby model could not be opened
                    }
                }

            }
            resetProjectCaches();

            return this.currentDelta;
        } finally {
            this.currentDelta = null;
            this.projectCachesToReset.clear();
        }
    }

    private RubyElementDelta currentDelta() {
        if (this.currentDelta == null) {
            this.currentDelta = new RubyElementDelta(this.manager.getRubyModel());
        }
        return this.currentDelta;
    }

    /*
     * Traverse the set of projects which have changed namespace, and reset
     * their caches and their dependents
     */
    private void resetProjectCaches() {
        Iterator iterator = this.projectCachesToReset.iterator();
        HashMap projectDepencies = this.state.projectDependencies;
        HashSet affectedDependents = new HashSet();
        while (iterator.hasNext()) {
            RubyProject project = (RubyProject) iterator.next();
            project.resetCaches();
            addDependentProjects(project, projectDepencies, affectedDependents);
        }
        // reset caches of dependent projects
        iterator = affectedDependents.iterator();
        while (iterator.hasNext()) {
            RubyProject project = (RubyProject) iterator.next();
            project.resetCaches();
        }
    }

    /*
     * Adds the dependents of the given project to the list of the projects to
     * update.
     */
    private void addDependentProjects(IRubyProject project, HashMap projectDependencies,
            HashSet result) {
        IRubyProject[] dependents = (IRubyProject[]) projectDependencies.get(project);
        if (dependents == null) return;
        for (int i = 0, length = dependents.length; i < length; i++) {
            IRubyProject dependent = dependents[i];
            if (result.contains(dependent)) continue; // no need to go further
            // as the project is
            // already known
            result.add(dependent);
            addDependentProjects(dependent, projectDependencies, result);
        }
    }

    /*
     * Generic processing for elements with changed contents:<ul> <li>The
     * element is closed such that any subsequent accesses will re-open the
     * element reflecting its new structure. <li>An entry is made in the delta
     * reporting a content change (K_CHANGE with F_CONTENT flag set). </ul>
     */
    private void nonRubyResourcesChanged(Openable element, IResourceDelta delta)
            throws RubyModelException {

        // reset non-java resources if element was open
        if (element.isOpen()) {
            RubyElementInfo info = (RubyElementInfo) element.getElementInfo();
            switch (element.getElementType()) {
            case IRubyElement.RUBY_MODEL:
                ((RubyModelInfo) info).nonRubyResources = null;
                currentDelta().addResourceDelta(delta);
                return;
            case IRubyElement.RUBY_PROJECT:
                ((RubyProjectElementInfo) info).setNonRubyResources(null);
                break;
            }
        }

        RubyElementDelta current = currentDelta();
        RubyElementDelta elementDelta = current.find(element);
        if (elementDelta == null) {
            // don't use find after creating the delta as it can be null (see
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=63434)
            elementDelta = current.changed(element, IRubyElementDelta.F_CONTENT);
        }
        elementDelta.addResourceDelta(delta);
    }

    /*
     * Turns the firing mode to off. That is, deltas that are/have been
     * registered will not be fired until deltas are started again.
     */
    private void stopDeltas() {
        this.isFiring = false;
    }

    /*
     * Turns the firing mode to on. That is, deltas that are/have been
     * registered will be fired.
     */
    private void startDeltas() {
        this.isFiring = true;
    }

    /*
     * Note that the project is about to be deleted.
     */
    private void deleting(IProject project) {

        try {
            RubyProject rubyProject = (RubyProject) RubyCore.create(project);
            rubyProject.close();

            // workaround for bug 15168 circular errors not reported
            if (this.state.modelProjectsCache == null) {
                this.state.modelProjectsCache = this.manager.getRubyModel().getRubyProjects();
            }
            removeFromParentInfo(rubyProject);

            // remove preferences from per project info
            this.manager.resetProjectPreferences(rubyProject);
        } catch (RubyModelException e) {
            // ruby project doesn't exist: ignore
        }
    }

    /*
     * Removes the given element from its parents cache of children. If the
     * element does not have a parent, or the parent is not currently open, this
     * has no effect.
     */
    private void removeFromParentInfo(Openable child) {

        Openable parent = (Openable) child.getParent();
        if (parent != null && parent.isOpen()) {
            try {
                RubyElementInfo info = (RubyElementInfo) parent.getElementInfo();
                info.removeChild(child);
            } catch (RubyModelException e) {
                // do nothing - we already checked if open
            }
        }
    }

    /*
     * Returns whether a given delta contains some information relevant to the
     * JavaModel, in particular it will not consider SYNC or MARKER only deltas.
     */
    private boolean isAffectedBy(IResourceDelta rootDelta) {
        // if (rootDelta == null) System.out.println("NULL DELTA");
        // long start = System.currentTimeMillis();
        if (rootDelta != null) {
            // use local exception to quickly escape from delta traversal
            class FoundRelevantDeltaException extends RuntimeException {

                private static final long serialVersionUID = 7137113252936111022L; // backward
                // compatible
                // only the class name is used (to differenciate from other
                // RuntimeExceptions)
            }
            try {
                rootDelta.accept(new IResourceDeltaVisitor() {

                    public boolean visit(IResourceDelta delta) /*
                                                                 * throws
                                                                 * CoreException
                                                                 */{
                        switch (delta.getKind()) {
                        case IResourceDelta.ADDED:
                        case IResourceDelta.REMOVED:
                            throw new FoundRelevantDeltaException();
                        case IResourceDelta.CHANGED:
                            // if any flag is set but SYNC or MARKER, this delta
                            // should be considered
                            if (delta.getAffectedChildren().length == 0 // only
                                    // check
                                    // leaf
                                    // delta
                                    // nodes
                                    && (delta.getFlags() & ~(IResourceDelta.SYNC | IResourceDelta.MARKERS)) != 0) { throw new FoundRelevantDeltaException(); }
                        }
                        return true;
                    }
                });
            } catch (FoundRelevantDeltaException e) {
                // System.out.println("RELEVANT DELTA detected in: "+
                // (System.currentTimeMillis() - start));
                return true;
            } catch (CoreException e) { // ignore delta if not able to traverse
            }
        }
        // System.out.println("IGNORE SYNC DELTA took: "+
        // (System.currentTimeMillis() - start));
        return false;
    }

    /*
     * Process the given delta and look for projects being added, opened, closed
     * or with a java nature being added or removed. Note that projects being
     * deleted are checked in deleting(IProject). In all cases, add the
     * project's dependents to the list of projects to update so that the
     * classpath related markers can be updated.
     */
    private void checkProjectsBeingAddedOrRemoved(IResourceDelta delta) {
        IResource resource = delta.getResource();
        boolean processChildren = false;

        switch (resource.getType()) {
        case IResource.ROOT:
            // workaround for bug 15168 circular errors not reported
            if (this.state.modelProjectsCache == null) {
                try {
                    this.state.modelProjectsCache = this.manager.getRubyModel().getRubyProjects();
                } catch (RubyModelException e) {
                    // ruby model doesn't exist: never happens
                }
            }
            processChildren = true;
            break;
        case IResource.PROJECT:
            // NB: No need to check project's nature as if the project is not a
            // ruby project:
            // - if the project is added or changed this is a noop for
            // projectsBeingDeleted
            // - if the project is closed, it has already lost its ruby nature
            IProject project = (IProject) resource;
            RubyProject rubyProject = (RubyProject) RubyCore.create(project);
            switch (delta.getKind()) {
            case IResourceDelta.ADDED:
                // workaround for bug 15168 circular errors not reported
                if (RubyProject.hasRubyNature(project)) {
                    this.addToParentInfo(rubyProject);
                }
                break;

            case IResourceDelta.CHANGED:
                if ((delta.getFlags() & IResourceDelta.OPEN) != 0) {
                    // workaround for bug 15168 circular errors not reported
                    if (project.isOpen()) {
                        if (RubyProject.hasRubyNature(project)) {
                            this.addToParentInfo(rubyProject);
                        }
                    } else {
                        try {
                            rubyProject.close();
                        } catch (RubyModelException e) {
                            // ruby project doesn't exist: ignore
                        }
                        this.removeFromParentInfo(rubyProject);
                        this.manager.removePerProjectInfo(rubyProject);
                    }
                } else if ((delta.getFlags() & IResourceDelta.DESCRIPTION) != 0) {
                    boolean wasJavaProject = this.manager.getRubyModel().findRubyProject(project) != null;
                    boolean isJavaProject = RubyProject.hasRubyNature(project);
                    if (wasJavaProject != isJavaProject) {
                        // workaround for bug 15168 circular errors not reported
                        if (isJavaProject) {
                            this.addToParentInfo(rubyProject);
                        } else {
                            // remove classpath cache so that initializeRoots()
                            // will not consider the project has a classpath
                            this.manager.removePerProjectInfo((RubyProject) RubyCore
                                    .create(project));
                            // close project
                            try {
                                rubyProject.close();
                            } catch (RubyModelException e) {
                                // java project doesn't exist: ignore
                            }
                            this.removeFromParentInfo(rubyProject);
                        }
                    } else {
                        // in case the project was removed then added then
                        // changed (see bug 19799)
                        if (isJavaProject) { // need nature check - 18698
                            this.addToParentInfo(rubyProject);
                            processChildren = true;
                        }
                    }
                } else {
                    // workaround for bug 15168 circular errors not reported
                    // in case the project was removed then added then changed
                    if (RubyProject.hasRubyNature(project)) { // need nature
                        // check - 18698
                        this.addToParentInfo(rubyProject);
                        processChildren = true;
                    }
                }
                break;

            case IResourceDelta.REMOVED:

                // remove classpath cache so that initializeRoots() will not
                // consider the project has a classpath
                this.manager.removePerProjectInfo((RubyProject) RubyCore.create(resource));
                break;
            }

            // in all cases, refresh the external jars for this project
            addForRefresh(rubyProject);

            break;
        }
        if (processChildren) {
            IResourceDelta[] children = delta.getAffectedChildren();
            for (int i = 0; i < children.length; i++) {
                checkProjectsBeingAddedOrRemoved(children[i]);
            }
        }
    }

    /*
     * Adds the given element to the list of elements used as a scope for
     * external jars refresh.
     */
    public void addForRefresh(IRubyElement element) {
        if (this.refreshedElements == null) {
            this.refreshedElements = new HashSet();
        }
        this.refreshedElements.add(element);
    }

    /*
     * Adds the given child handle to its parent's cache of children.
     */
    private void addToParentInfo(Openable child) {
        Openable parent = (Openable) child.getParent();
        if (parent != null && parent.isOpen()) {
            try {
                RubyElementInfo info = (RubyElementInfo) parent.getElementInfo();
                info.addChild(child);
            } catch (RubyModelException e) {
                // do nothing - we already checked if open
            }
        }
    }

    /*
     * Converts an <code>IResourceDelta</code> and its children into the
     * corresponding <code>IRubyElementDelta</code>s.
     */
    private void traverseDelta(IResourceDelta delta, int elementType) {

        IResource res = delta.getResource();

        // process current delta
        boolean processChildren = true;
        if (res instanceof IProject) {
            processChildren = updateCurrentDeltaAndIndex(delta, elementType);
        } else {
            // not yet inside a package fragment root
            processChildren = true;
        }

        // process children if needed
        if (processChildren) {
            IResourceDelta[] children = delta.getAffectedChildren();
            boolean oneChildOnClasspath = false;
            int length = children.length;
            IResourceDelta[] orphanChildren = null;
            Openable parent = null;
            boolean isValidParent = true;
            if (orphanChildren != null && (oneChildOnClasspath // orphan
                    // children are
                    // siblings of a
                    // package
                    // fragment root
                    || res instanceof IProject)) { // non-java resource
                // directly under a project

                // attach orphan children
                IProject rscProject = res.getProject();
                RubyProject adoptiveProject = (RubyProject) RubyCore.create(rscProject);
                if (adoptiveProject != null && RubyProject.hasRubyNature(rscProject)) { // delta
                    // iff
                    // Ruby
                    // project
                    // (18698)
                    for (int i = 0; i < length; i++) {
                        if (orphanChildren[i] != null) {
                            try {
                                nonRubyResourcesChanged(adoptiveProject, orphanChildren[i]);
                            } catch (RubyModelException e) {
                                // ignore
                            }
                        }
                    }
                }
            } // else resource delta will be added by parent
        } // else resource delta will be added by parent
    }

    /*
     * Update the current delta (ie. add/remove/change the given element) and
     * update the correponding index. Returns whether the children of the given
     * delta must be processed. @throws a RubyModelException if the delta
     * doesn't correspond to a ruby element of the given type.
     */
    public boolean updateCurrentDeltaAndIndex(IResourceDelta delta, int elementType) {
        Openable element;
        switch (delta.getKind()) {
        case IResourceDelta.ADDED:
            IResource deltaRes = delta.getResource();
            element = createElement(deltaRes, elementType);
            if (element == null) { return false; }
            elementAdded(element, delta);
            return false;
        case IResourceDelta.REMOVED:
            deltaRes = delta.getResource();
            element = createElement(deltaRes, elementType);
            if (element == null) { return false; }
            elementRemoved(element, delta);

            if (deltaRes.getType() == IResource.PROJECT) {
                // reset the corresponding project built state, since cannot
                // reuse if added back
                if (RubyBuilder.DEBUG)
                    System.out.println("Clearing last state for removed project : " + deltaRes); //$NON-NLS-1$
            }
            return false;
        case IResourceDelta.CHANGED:
            int flags = delta.getFlags();
            if ((flags & IResourceDelta.CONTENT) != 0 || (flags & IResourceDelta.ENCODING) != 0) {
                // content or encoding has changed
                element = createElement(delta.getResource(), elementType);
                if (element == null) return false;
                contentChanged(element);
            } else if (elementType == IRubyElement.RUBY_PROJECT) {
                if ((flags & IResourceDelta.OPEN) != 0) {
                    // project has been opened or closed
                    IProject res = (IProject) delta.getResource();
                    element = createElement(res, elementType);
                    if (element == null) { return false; }
                    if (res.isOpen()) {
                        if (RubyProject.hasRubyNature(res)) {
                            addToParentInfo(element);
                            currentDelta().opened(element);

                            // refresh pkg fragment roots and caches of the
                            // project (and its dependents)
                            this.projectCachesToReset.add(element);
                        }
                    } else {
                        RubyModel javaModel = this.manager.getRubyModel();
                        boolean wasJavaProject = javaModel.findRubyProject(res) != null;
                        if (wasJavaProject) {
                            close(element);
                            removeFromParentInfo(element);
                            currentDelta().closed(element);
                        }
                    }
                    return false; // when a project is open/closed don't
                    // process children
                }
                if ((flags & IResourceDelta.DESCRIPTION) != 0) {
                    IProject res = (IProject) delta.getResource();
                    RubyModel javaModel = this.manager.getRubyModel();
                    boolean wasJavaProject = javaModel.findRubyProject(res) != null;
                    boolean isJavaProject = RubyProject.hasRubyNature(res);
                    if (wasJavaProject != isJavaProject) {
                        // project's nature has been added or removed
                        element = this.createElement(res, elementType);
                        if (element == null) return false; // note its
                        // resources are
                        // still visible as
                        // roots to other
                        // projects
                        if (isJavaProject) {
                            elementAdded(element, delta);
                        } else {
                            elementRemoved(element, delta);
                            // reset the corresponding project built state,
                            // since cannot reuse if added back
                            if (RubyBuilder.DEBUG)
                                System.out
                                        .println("Clearing last state for project losing Ruby nature: " + res); //$NON-NLS-1$

                        }
                        return false; // when a project's nature is
                        // added/removed don't process children
                    }
                }
            }
            return true;
        }
        return true;
    }

    /*
     * Closes the given element, which removes it from the cache of open
     * elements.
     */
    private void close(Openable element) {
        try {
            element.close();
        } catch (RubyModelException e) {
            // do nothing
        }
    }

    /*
     * Creates the openables corresponding to this resource. Returns null if
     * none was found.
     */
    private Openable createElement(IResource resource, int elementType) {
        if (resource == null) return null;

        IPath path = resource.getFullPath();
        IRubyElement element = null;
        switch (elementType) {

        case IRubyElement.RUBY_PROJECT:

            // note that non-ruby resources rooted at the project level will
            // also enter this code with
            // an elementType PROJECT (see #elementType(...)).
            if (resource instanceof IProject) {

                this.popUntilPrefixOf(path);

                if (this.currentElement != null
                        && this.currentElement.getElementType() == IRubyElement.RUBY_PROJECT
                        && ((IRubyProject) this.currentElement).getProject().equals(resource)) { return this.currentElement; }
                IProject proj = (IProject) resource;
                if (RubyProject.hasRubyNature(proj)) {
                    element = RubyCore.create(proj);
                } else {
                    // java project may have been been closed or removed (look
                    // for
                    // element amongst old ruby project s list).
                    element = this.manager.getRubyModel().findRubyProject(proj);
                }
            }
            break;
        case IRubyElement.SCRIPT:
            // find the element that encloses the resource
            this.popUntilPrefixOf(path);
            element = RubyCore.create(resource);
            break;
        }
        if (element == null) return null;
        this.currentElement = (Openable) element;
        return this.currentElement;
    }

    private void popUntilPrefixOf(IPath path) {
        while (this.currentElement != null) {
            IPath currentElementPath = null;
            IResource currentElementResource = this.currentElement.getResource();
            if (currentElementResource != null) {
                currentElementPath = currentElementResource.getFullPath();
            }

            if (currentElementPath != null) {
                if (currentElementPath.isPrefixOf(path)) { return; }
            }
            this.currentElement = (Openable) this.currentElement.getParent();
        }
    }

    /*
     * Processing for an element that has been added:<ul> <li>If the element
     * is a project, do nothing, and do not process children, as when a project
     * is created it does not yet have any natures - specifically a java nature.
     * <li>If the elemet is not a project, process it as added (see <code>basicElementAdded</code>.
     * </ul> Delta argument could be null if processing an external JAR change
     */
    private void elementAdded(Openable element, IResourceDelta delta) {
        int elementType = element.getElementType();

        if (elementType == IRubyElement.RUBY_PROJECT) {
            // project add is handled by RubyProject.configure() because
            // when a project is created, it does not yet have a java nature
            if (delta != null && RubyProject.hasRubyNature((IProject) delta.getResource())) {
                addToParentInfo(element);
                if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0) {
                    Openable movedFromElement = (Openable) element.getRubyModel().getRubyProject(
                            delta.getMovedFromPath().lastSegment());
                    currentDelta().movedTo(element, movedFromElement);
                } else {
                    currentDelta().added(element);
                }

                // refresh pkg fragment roots and caches of the project (and its
                // dependents)
                this.projectCachesToReset.add(element);
            }
        } else {
            if (delta == null || (delta.getFlags() & IResourceDelta.MOVED_FROM) == 0) {
                // regular element addition
                if (isPrimaryWorkingCopy(element, elementType)) {
                    // filter out changes to primary compilation unit in working
                    // copy mode
                    // just report a change to the resource (see
                    // https://bugs.eclipse.org/bugs/show_bug.cgi?id=59500)
                    currentDelta().changed(element, IRubyElementDelta.F_PRIMARY_RESOURCE);
                } else {
                    addToParentInfo(element);

                    // Force the element to be closed as it might have been
                    // opened
                    // before the resource modification came in and it might
                    // have a new child
                    // For example, in an IWorkspaceRunnable:
                    // 1. create a package fragment p using a java model
                    // operation
                    // 2. open package p
                    // 3. add file X.java in folder p
                    // When the resource delta comes in, only the addition of p
                    // is notified,
                    // but the package p is already opened, thus its children
                    // are not recomputed
                    // and it appears empty.
                    close(element);

                    currentDelta().added(element);
                }
            } else {
                // element is moved
                addToParentInfo(element);
                close(element);

                IPath movedFromPath = delta.getMovedFromPath();
                IResource res = delta.getResource();
                IResource movedFromRes;
                if (res instanceof IFile) {
                    movedFromRes = res.getWorkspace().getRoot().getFile(movedFromPath);
                } else {
                    movedFromRes = res.getWorkspace().getRoot().getFolder(movedFromPath);
                }

                // find the element type of the moved from element
                int movedFromType = this.elementType(movedFromRes, IResourceDelta.REMOVED, element
                        .getParent().getElementType());

                // reset current element as it might be inside a nested root
                // (popUntilPrefixOf() may use the outer root)
                this.currentElement = null;

                // create the moved from element
                Openable movedFromElement = elementType != IRubyElement.RUBY_PROJECT
                        && movedFromType == IRubyElement.RUBY_PROJECT ? null : // outside
                                                                            // classpath
                        this.createElement(movedFromRes, movedFromType);
                if (movedFromElement == null) {
                    // moved from outside classpath
                    currentDelta().added(element);
                } else {
                    currentDelta().movedTo(element, movedFromElement);
                }
            }
        }
    }

    /*
     * Returns whether the given element is a primary compilation unit in
     * working copy mode.
     */
    private boolean isPrimaryWorkingCopy(IRubyElement element, int elementType) {
        if (elementType == IRubyElement.SCRIPT) {
            RubyScript cu = (RubyScript) element;
            return cu.isPrimary() && cu.isWorkingCopy();
        }
        return false;
    }

    /*
     * Generic processing for a removed element:<ul> <li>Close the element,
     * removing its structure from the cache <li>Remove the element from its
     * parent's cache of children <li>Add a REMOVED entry in the delta </ul>
     * Delta argument could be null if processing an external JAR change
     */
    private void elementRemoved(Openable element, IResourceDelta delta) {

        int elementType = element.getElementType();
        if (delta == null || (delta.getFlags() & IResourceDelta.MOVED_TO) == 0) {
            // regular element removal
            if (isPrimaryWorkingCopy(element, elementType)) {
                // filter out changes to primary compilation unit in working
                // copy mode
                // just report a change to the resource (see
                // https://bugs.eclipse.org/bugs/show_bug.cgi?id=59500)
                currentDelta().changed(element, IRubyElementDelta.F_PRIMARY_RESOURCE);
            } else {
                close(element);
                removeFromParentInfo(element);
                currentDelta().removed(element);
            }
        } else {
            // element is moved
            close(element);
            removeFromParentInfo(element);
            IPath movedToPath = delta.getMovedToPath();
            IResource res = delta.getResource();
            IResource movedToRes;
            switch (res.getType()) {
            case IResource.PROJECT:
                movedToRes = res.getWorkspace().getRoot().getProject(movedToPath.lastSegment());
                break;
            case IResource.FOLDER:
                movedToRes = res.getWorkspace().getRoot().getFolder(movedToPath);
                break;
            case IResource.FILE:
                movedToRes = res.getWorkspace().getRoot().getFile(movedToPath);
                break;
            default:
                return;
            }

            // find the element type of the moved from element
            int movedToType = this.elementType(movedToRes, IResourceDelta.ADDED, element
                    .getParent().getElementType());

            // reset current element as it might be inside a nested root
            // (popUntilPrefixOf() may use the outer root)
            this.currentElement = null;

            // create the moved To element
            Openable movedToElement = elementType != IRubyElement.RUBY_PROJECT
                    && movedToType == IRubyElement.RUBY_PROJECT ? null : // outside
                                                                    // classpath
                    this.createElement(movedToRes, movedToType);
            if (movedToElement == null) {
                // moved outside classpath
                currentDelta().removed(element);
            } else {
                currentDelta().movedFrom(element, movedToElement);
            }
        }

        switch (elementType) {
        case IRubyElement.RUBY_PROJECT:

            // refresh pkg fragment roots and caches of the project (and its
            // dependents)
            this.projectCachesToReset.add(element);

            break;
        }
    }

    /*
     * Generic processing for elements with changed contents:<ul> <li>The
     * element is closed such that any subsequent accesses will re-open the
     * element reflecting its new structure. <li>An entry is made in the delta
     * reporting a content change (K_CHANGE with F_CONTENT flag set). </ul>
     * Delta argument could be null if processing an external JAR change
     */
    private void contentChanged(Openable element) {

        boolean isPrimary = false;
        boolean isPrimaryWorkingCopy = false;
        if (element.getElementType() == IRubyElement.SCRIPT) {
            RubyScript cu = (RubyScript) element;
            isPrimary = cu.isPrimary();
            isPrimaryWorkingCopy = isPrimary && cu.isWorkingCopy();
        }
        if (isPrimaryWorkingCopy) {
            // filter out changes to primary compilation unit in working copy
            // mode
            // just report a change to the resource (see
            // https://bugs.eclipse.org/bugs/show_bug.cgi?id=59500)
            currentDelta().changed(element, IRubyElementDelta.F_PRIMARY_RESOURCE);
        } else {
            close(element);
            int flags = IRubyElementDelta.F_CONTENT;
            if (isPrimary) {
                flags |= IRubyElementDelta.F_PRIMARY_RESOURCE;
            }
            currentDelta().changed(element, flags);
        }
    }

    /*
     * Returns the type of the ruby element the given delta matches to. Returns
     * NON_RUBY_RESOURCE if unknown (e.g. a non-ruby resource or excluded .rb
     * file)
     */
    private int elementType(IResource res, int kind, int parentType) {
        switch (parentType) {
        case IRubyElement.RUBY_MODEL:
            // case of a movedTo or movedFrom project (other cases are handled
            // in processResourceDelta(...)
            return IRubyElement.RUBY_PROJECT;

        case NON_RUBY_RESOURCE:
        case IRubyElement.RUBY_PROJECT:
            if (res.getType() == IResource.FOLDER) { return NON_RUBY_RESOURCE; }
            String fileName = res.getName();
            if (Util.isValidRubyScriptName(fileName)) {
                return IRubyElement.SCRIPT;
            } else {
                return NON_RUBY_RESOURCE;
            }

        default:
            return NON_RUBY_RESOURCE;
        }
    }
}
