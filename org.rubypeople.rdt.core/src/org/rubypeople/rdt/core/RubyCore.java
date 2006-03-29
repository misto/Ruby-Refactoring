/*
 * Author: 
 *
 * Copyright (c) 2003-2005 RubyPeople.
 *
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
 */
package org.rubypeople.rdt.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.internal.core.BatchOperation;
import org.rubypeople.rdt.internal.core.DefaultWorkingCopyOwner;
import org.rubypeople.rdt.internal.core.RubyCorePreferenceInitializer;
import org.rubypeople.rdt.internal.core.RubyModel;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.SymbolIndexResourceChangeListener;
import org.rubypeople.rdt.internal.core.builder.IndexUpdater;
import org.rubypeople.rdt.internal.core.builder.MassIndexUpdaterJob;
import org.rubypeople.rdt.internal.core.builder.RubyBuilder;
import org.rubypeople.rdt.internal.core.parser.RubyParser;
import org.rubypeople.rdt.internal.core.symbols.ISymbolFinder;
import org.rubypeople.rdt.internal.core.symbols.SymbolIndex;
import org.rubypeople.rdt.internal.core.util.Util;

public class RubyCore extends Plugin {

    private static RubyCore RUBY_CORE_PLUGIN = null;

    public final static String PLUGIN_ID = "org.rubypeople.rdt.core";//$NON-NLS-1$

    private static final String RUBY_PARSER_DEBUG_OPTION = RubyCore.PLUGIN_ID + "/rubyparser";//$NON-NLS-1$
    private static final String MODEL_MANAGER_VERBOSE_OPTION = RubyCore.PLUGIN_ID + "/modelmanager";//$NON-NLS-1$
    private static final String SYMBOL_INDEX_VERBOSE_OPTION = RubyCore.PLUGIN_ID + "/symbolIndex";//$NON-NLS-1$
    private static final String BUILDER_VERBOSE_OPTION = RubyCore.PLUGIN_ID + "/rubyBuilder";//$NON-NLS-1$

    public final static String NATURE_ID = PLUGIN_ID + ".rubynature";//$NON-NLS-1$

    /**
     * New Preferences API
     * 
     * @since 0.6.0
     */
    public static final IEclipsePreferences[] preferencesLookup = new IEclipsePreferences[2];
    static final int PREF_INSTANCE = 0;
    static final int PREF_DEFAULT = 1;

    /**
     * Default task tag
     * 
     * @since 0.6.0
     */
    public static final String DEFAULT_TASK_TAGS = "TODO,FIXME,XXX"; //$NON-NLS-1$

    /**
     * The identifier for the Ruby builder (value
     * <code>"org.rubypeople.rdt.core.rubybuilder"</code>).
     */
    public static final String BUILDER_ID = PLUGIN_ID + ".rubybuilder"; //$NON-NLS-1$

    /**
     * Default task priority
     * 
     * @since 0.6.0
     */
    public static final String DEFAULT_TASK_PRIORITIES = "NORMAL,HIGH,NORMAL"; //$NON-NLS-1$
    /**
     * Possible configurable option ID.
     * 
     * @see #getDefaultOptions()
     * @since 0.6.0
     */
    public static final String COMPILER_TASK_PRIORITIES = PLUGIN_ID + ".compiler.taskPriorities"; //$NON-NLS-1$
    /**
     * Possible configurable option value for COMPILER_TASK_PRIORITIES.
     * 
     * @see #getDefaultOptions()
     * @since 0.6.0
     */
    public static final String COMPILER_TASK_PRIORITY_HIGH = "HIGH"; //$NON-NLS-1$
    /**
     * Possible configurable option value for COMPILER_TASK_PRIORITIES.
     * 
     * @see #getDefaultOptions()
     * @since 0.6.0
     */
    public static final String COMPILER_TASK_PRIORITY_LOW = "LOW"; //$NON-NLS-1$
    /**
     * Possible configurable option value for COMPILER_TASK_PRIORITIES.
     * 
     * @see #getDefaultOptions()
     * @since 0.6.0
     */
    public static final String COMPILER_TASK_PRIORITY_NORMAL = "NORMAL"; //$NON-NLS-1$

    /**
     * Possible configurable option ID.
     * 
     * @see #getDefaultOptions()
     * @since 0.6.0
     */
    public static final String COMPILER_TASK_TAGS = PLUGIN_ID + ".compiler.taskTags"; //$NON-NLS-1$
    /**
     * Possible configurable option ID.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String COMPILER_TASK_CASE_SENSITIVE = PLUGIN_ID
            + ".compiler.taskCaseSensitive"; //$NON-NLS-1$	
    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String ENABLED = "enabled"; //$NON-NLS-1$
    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String DISABLED = "disabled"; //$NON-NLS-1$

    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String TAB = "tab"; //$NON-NLS-1$
    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String SPACE = "space"; //$NON-NLS-1$

    /**
     * Possible configurable option ID.
     * 
     * @see #getDefaultOptions()
     * @since 0.7.0
     */
    public static final String CORE_ENCODING = PLUGIN_ID + ".encoding"; //$NON-NLS-1$

    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.8.0
     */
    public static final String INSERT = "insert"; //$NON-NLS-1$
    /**
     * Possible configurable option value.
     * 
     * @see #getDefaultOptions()
     * @since 0.8.0
     */
    public static final String DO_NOT_INSERT = "do not insert"; //$NON-NLS-1$

	/**
	 * Value of the content-type for Ruby source files. Use this value to retrieve the Ruby content type
	 * from the content type manager, and to add new Ruby-like extensions to this content type.
	 * 
	 * @see org.eclipse.core.runtime.content.IContentTypeManager#getContentType(String)
	 * @see #getRubyLikeExtensions()
	 * @since 0.8.0
	 */
	public static final String RUBY_SOURCE_CONTENT_TYPE = RubyCore.PLUGIN_ID+".rubySource" ; //$NON-NLS-1$

    private SymbolIndex symbolIndex;
    private ISymbolFinder symbolFinder;

    public RubyCore() {
        super();
        RUBY_CORE_PLUGIN = this;
        symbolFinder = symbolIndex = new SymbolIndex();
    }

    /**
     * Returns the single instance of the Ruby core plug-in runtime class.
     * 
     * @return the single instance of the Ruby core plug-in runtime class
     */
    public static RubyCore getPlugin() {
        return RUBY_CORE_PLUGIN;
    }

    public static IWorkspace getWorkspace() {
        return ResourcesPlugin.getWorkspace();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.core.runtime.Plugin#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        RubyModelManager.getRubyModelManager().startup();

        RubyParser.setDebugging(isDebugOptionTrue(RUBY_PARSER_DEBUG_OPTION));
        RubyModelManager.setVerbose(isDebugOptionTrue(MODEL_MANAGER_VERBOSE_OPTION));
        SymbolIndex.setVerbose(isDebugOptionTrue(SYMBOL_INDEX_VERBOSE_OPTION));
        RubyBuilder.setVerbose(isDebugOptionTrue(BUILDER_VERBOSE_OPTION));

        SymbolIndexResourceChangeListener.register(symbolIndex);
        IndexUpdater indexUpdater = new IndexUpdater(symbolIndex);
        List rubyProjects = Arrays.asList(getRubyProjects());
        MassIndexUpdaterJob massUpdater = new MassIndexUpdaterJob(indexUpdater, rubyProjects);
        massUpdater.schedule();
    }

    /*
     * (non-Javadoc) Shutdown the JavaCore plug-in. <p> De-registers the
     * RubyModelManager as a resource changed listener and save participant. <p>
     * 
     * @see org.eclipse.core.runtime.Plugin#stop(BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        try {
            RubyModelManager.getRubyModelManager().shutdown();
        } finally {
            // ensure we call super.stop as the last thing
            super.stop(context);
        }
    }

    private boolean isDebugOptionTrue(String option) {
        String optionText = Platform.getDebugOption(option);
        return optionText == null ? false : optionText.equalsIgnoreCase("true");
    }

    public static boolean upgradeOldProjects() throws CoreException {
        boolean projectUpgraded = false;
        IProject[] projects = RubyCore.getRubyProjects();
        for (int i = 0; i < projects.length; i++) {
            if (upgradeOldProject(projects[i])) {
                projectUpgraded = true;
            }
        }
        return projectUpgraded;
    }

    public static boolean upgradeOldProject(IProject project) throws CoreException {
        RubyModelManager rubyModelManager = RubyModelManager.getRubyModelManager();
        RubyModel rubyModel = rubyModelManager.getRubyModel();
        IRubyProject rubyProject = rubyModel.getRubyProject(project);
        if (rubyProject != null) return rubyProject.upgrade();
        return false;
    }

    public static void trace(String message) {
        if (getPlugin().isDebugging()) System.out.println(message);
    }

    public static void log(Exception e) {
        String msg = e.getMessage();
        if (msg == null) msg = "";
        log(Status.ERROR, msg, e);
    }

    /**
     * @param string
     */
    public static void log(String string) {
        log(IStatus.INFO, string);
    }

    public static void log(int severity, String string) {
        log(severity, string, null);
    }

    public static void log(int severity, String string, Throwable e) {
        getPlugin().getLog().log(new Status(severity, PLUGIN_ID, IStatus.OK, string, e));
        System.out.println(string);
        if (e != null) e.printStackTrace();
    }

    public static String getOSDirectory(Plugin plugin) {
        final Bundle bundle = plugin.getBundle();
        String location = bundle.getLocation();
        int prefixLength = location.indexOf('@');
        if (prefixLength == -1) { throw new RuntimeException(
                "Location of launching bundle does not contain @: " + location); }
        String pluginDir = location.substring(prefixLength + 1);
        File pluginDirFile = new File(pluginDir);
        if (!pluginDirFile.exists()) {
            // pluginDirFile is a relative path, if the working directory is
            // different from
            // the location of the eclipse executable, we try this ...
            String installArea = System.getProperty("osgi.install.area");
            if (installArea.startsWith("file:")) {
                installArea = installArea.substring("file:".length());
            }
            // Path.toOSString() removes a leading slash if on windows, e.g.
            // /D:/Eclipse => D:/Eclipse
            File installFile = new File(new Path(installArea).toOSString());
            pluginDirFile = new File(installFile, pluginDir);
            if (!pluginDirFile.exists())
                throw new RuntimeException("Unable to find (" + pluginDirFile + ") directory for "
                        + plugin.getClass());
        }
        return pluginDirFile.getAbsolutePath() + "/";
    }

    public static IProject[] getRubyProjects() {
        List rubyProjectsList = new ArrayList();
        IProject[] workspaceProjects = RubyCore.getWorkspace().getRoot().getProjects();

        for (int i = 0; i < workspaceProjects.length; i++) {
            IProject iProject = workspaceProjects[i];
            if (isRubyProject(iProject)) rubyProjectsList.add(iProject);
        }

        IProject[] rubyProjects = new IProject[rubyProjectsList.size()];
        return (IProject[]) rubyProjectsList.toArray(rubyProjects);
    }

    public static boolean isRubyProject(IProject aProject) {
        try {
            return aProject.hasNature(RubyCore.NATURE_ID);
        } catch (CoreException e) {
        }
        return false;
    }

    public static IRubyScript create(IFile aFile) {
        return create(aFile, null);
    }

    public static IRubyScript create(IFile file, IRubyProject project) {
        if (project == null) {
            project = create(file.getProject());
        }
        if(isRubyLikeFileName(file.getName())) {
        	return new RubyScript((RubyProject) project, file, file.getName(),
                    DefaultWorkingCopyOwner.PRIMARY);
        }
        return null;
    }

    public static IRubyProject create(IProject project) {
        if (project == null) { return null; }
        RubyModel rubyModel = RubyModelManager.getRubyModelManager().getRubyModel();
        return rubyModel.getRubyProject(project);
    }

    public static void addRubyNature(IProject project, IProgressMonitor monitor)
            throws CoreException {
        if (!project.hasNature(RubyCore.NATURE_ID)) {
            IProjectDescription description = project.getDescription();
            String[] prevNatures = description.getNatureIds();
            String[] newNatures = new String[prevNatures.length + 1];
            System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
            newNatures[prevNatures.length] = RubyCore.NATURE_ID;
            description.setNatureIds(newNatures);
            project.setDescription(description, monitor);
        }
    }

    public static IRubyElement create(IResource resource) {
        switch (resource.getType()) {
        case IResource.FILE:
            return create((IFile) resource);
        case IResource.PROJECT:
            return create((IProject) resource);
        default: // TODO Is this anywhere near correct?
            return null;
        }
    }

    /**
     * Returns the Ruby model.
     * 
     * @param root
     *            the given root
     * @return the Ruby model, or <code>null</code> if the root is null
     */
    public static IRubyModel create(IWorkspaceRoot root) {
        if (root == null) { return null; }
        return RubyModelManager.getRubyModelManager().getRubyModel();
    }

    /**
     * Runs the given action as an atomic Java model operation.
     * <p>
     * After running a method that modifies java elements, registered listeners
     * receive after-the-fact notification of what just transpired, in the form
     * of a element changed event. This method allows clients to call a number
     * of methods that modify java elements and only have element changed event
     * notifications reported at the end of the entire batch.
     * </p>
     * <p>
     * If this method is called outside the dynamic scope of another such call,
     * this method runs the action and then reports a single element changed
     * event describing the net effect of all changes done to java elements by
     * the action.
     * </p>
     * <p>
     * If this method is called in the dynamic scope of another such call, this
     * method simply runs the action.
     * </p>
     * <p>
     * The supplied scheduling rule is used to determine whether this operation
     * can be run simultaneously with workspace changes in other threads. See
     * <code>IWorkspace.run(...)</code> for more details.
     * </p>
     * 
     * @param action
     *            the action to perform
     * @param rule
     *            the scheduling rule to use when running this operation, or
     *            <code>null</code> if there are no scheduling restrictions
     *            for this operation.
     * @param monitor
     *            a progress monitor, or <code>null</code> if progress
     *            reporting and cancellation are not desired
     * @exception CoreException
     *                if the operation failed.
     * @since 3.0
     */
    public static void run(IWorkspaceRunnable action, ISchedulingRule rule, IProgressMonitor monitor)
            throws CoreException {
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        if (workspace.isTreeLocked()) {
            new BatchOperation(action).run(monitor);
        } else {
            // use IWorkspace.run(...) to ensure that a build will be done in
            // autobuild mode
            workspace.run(new BatchOperation(action), rule, IWorkspace.AVOID_UPDATE, monitor);
        }
    }

    public SymbolIndex getSymbolIndex() {
        return symbolIndex;
    }

    public ISymbolFinder getSymbolFinder() {
        return symbolFinder;
    }

    public void setSymbolFinder(ISymbolFinder symbolFinder) {
        this.symbolFinder = symbolFinder;
    }

    /**
     * Helper method for returning one option value only. Equivalent to
     * <code>(String)JavaCore.getOptions().get(optionName)</code> Note that it
     * may answer <code>null</code> if this option does not exist.
     * <p>
     * For a complete description of the configurable options, see
     * <code>getDefaultOptions</code>.
     * </p>
     * 
     * @param optionName
     *            the name of an option
     * @return the String value of a given option
     * @see RubyCore#getDefaultOptions()
     * @see RubyCorePreferenceInitializer for changing default settings
     */
    public static String getOption(String optionName) {
        return RubyModelManager.getRubyModelManager().getOption(optionName);
    }

    /**
     * Returns the workspace root default charset encoding.
     * 
     * @return the name of the default charset encoding for workspace root.
     * @see IContainer#getDefaultCharset()
     * @see ResourcesPlugin#getEncoding()
     */
    public static String getEncoding() {
        // Verify that workspace is not shutting down (see bug
        // https://bugs.eclipse.org/bugs/show_bug.cgi?id=60687)
        IWorkspace workspace = ResourcesPlugin.getWorkspace();
        if (workspace != null) {
            try {
                return workspace.getRoot().getDefaultCharset();
            } catch (CoreException e) {
                // fails silently and return plugin global encoding if core
                // exception occurs
            }
        }
        return ResourcesPlugin.getEncoding();
    }

    /**
     * Returns the table of the current options. Initially, all options have
     * their default values, and this method returns a table that includes all
     * known options.
     * <p>
     * For a complete description of the configurable options, see
     * <code>getDefaultOptions</code>.
     * </p>
     * 
     * @return table of current settings of all options (key type:
     *         <code>String</code>; value type: <code>String</code>)
     * @see #getDefaultOptions()
     * @see RubyCorePreferenceInitializer for changing default settings
     */
    public static Hashtable getOptions() {
        return RubyModelManager.getRubyModelManager().getOptions();
    }

    /**
     * Adds the given listener for changes to Java elements. Has no effect if an
     * identical listener is already registered.
     * 
     * This listener will only be notified during the POST_CHANGE resource
     * change notification and any reconcile operation (POST_RECONCILE). For
     * finer control of the notification, use
     * <code>addElementChangedListener(IElementChangedListener,int)</code>,
     * which allows to specify a different eventMask.
     * 
     * @param listener
     *            the listener
     * @see ElementChangedEvent
     */
    public static void addElementChangedListener(IElementChangedListener listener) {
        addElementChangedListener(listener, ElementChangedEvent.POST_CHANGE
                | ElementChangedEvent.POST_RECONCILE);
    }

    /**
     * Adds the given listener for changes to Java elements. Has no effect if an
     * identical listener is already registered. After completion of this
     * method, the given listener will be registered for exactly the specified
     * events. If they were previously registered for other events, they will be
     * deregistered.
     * <p>
     * Once registered, a listener starts receiving notification of changes to
     * java elements in the model. The listener continues to receive
     * notifications until it is replaced or removed.
     * </p>
     * <p>
     * Listeners can listen for several types of event as defined in
     * <code>ElementChangeEvent</code>. Clients are free to register for any
     * number of event types however if they register for more than one, it is
     * their responsibility to ensure they correctly handle the case where the
     * same java element change shows up in multiple notifications. Clients are
     * guaranteed to receive only the events for which they are registered.
     * </p>
     * 
     * @param listener
     *            the listener
     * @param eventMask
     *            the bit-wise OR of all event types of interest to the listener
     * @see IElementChangedListener
     * @see ElementChangedEvent
     * @see #removeElementChangedListener(IElementChangedListener)
     * @since 0.7.0
     */
    public static void addElementChangedListener(IElementChangedListener listener, int eventMask) {
        RubyModelManager.getRubyModelManager().deltaState.addElementChangedListener(listener,
                eventMask);
    }

    /**
     * Removes the given element changed listener. Has no affect if an identical
     * listener is not registered.
     * 
     * @param listener
     *            the listener
     */
    public static void removeElementChangedListener(IElementChangedListener listener) {
        RubyModelManager.getRubyModelManager().deltaState.removeElementChangedListener(listener);
    }

    /**
     * Returns the single instance of the Ruby core plug-in runtime class.
     * Equivalent to <code>(RubyCore) getPlugin()</code>.
     * 
     * @return the single instance of the Ruby core plug-in runtime class
     */
    public static RubyCore getRubyCore() {
        return (RubyCore) getPlugin();
    }

    public static boolean isRubyLikeFileName(String name) {
        return Util.isRubyLikeFileName(name);
    }
}