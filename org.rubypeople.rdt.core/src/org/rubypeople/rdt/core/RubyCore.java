package org.rubypeople.rdt.core;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterManager;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.internal.core.DefaultWorkingCopyOwner;
import org.rubypeople.rdt.internal.core.ResourceAdapterFactory;
import org.rubypeople.rdt.internal.core.RubyElementAdapterFactory;
import org.rubypeople.rdt.internal.core.RubyModelManager;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.RubyScript;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class RubyCore extends Plugin {

	private static Plugin RUBY_CORE_PLUGIN = null;

	public final static String PLUGIN_ID = "org.rubypeople.rdt.core";
	public final static String NATURE_ID = PLUGIN_ID + ".rubynature";

	/**
	 * New Preferences API
	 * 
	 * @since 3.1
	 */
	public static final IEclipsePreferences[] preferencesLookup = new IEclipsePreferences[2];
	static final int PREF_INSTANCE = 0;
	static final int PREF_DEFAULT = 1;

	/**
	 * Default task tag
	 * 
	 * @since 3.0
	 */
	public static final String DEFAULT_TASK_TAGS = "TODO,FIXME,XXX"; //$NON-NLS-1$

	/**
	 * The identifier for the Ruby builder
	 * (value <code>"org.rubypeople.rdt.core.rubybuilder"</code>).
	 */
	public static final String BUILDER_ID = PLUGIN_ID + ".rubybuilder" ; //$NON-NLS-1$

	/**
	 * Default task priority
	 * 
	 * @since 3.0
	 */
	public static final String DEFAULT_TASK_PRIORITIES = "NORMAL,HIGH,NORMAL"; //$NON-NLS-1$
	/**
	 * Possible configurable option ID.
	 * 
	 * @see #getDefaultOptions()
	 * @since 2.1
	 */
	public static final String COMPILER_TASK_PRIORITIES = PLUGIN_ID + ".compiler.taskPriorities"; //$NON-NLS-1$
	/**
	 * Possible configurable option value for COMPILER_TASK_PRIORITIES.
	 * 
	 * @see #getDefaultOptions()
	 * @since 2.1
	 */
	public static final String COMPILER_TASK_PRIORITY_HIGH = "HIGH"; //$NON-NLS-1$
	/**
	 * Possible configurable option value for COMPILER_TASK_PRIORITIES.
	 * 
	 * @see #getDefaultOptions()
	 * @since 2.1
	 */
	public static final String COMPILER_TASK_PRIORITY_LOW = "LOW"; //$NON-NLS-1$
	/**
	 * Possible configurable option value for COMPILER_TASK_PRIORITIES.
	 * 
	 * @see #getDefaultOptions()
	 * @since 2.1
	 */
	public static final String COMPILER_TASK_PRIORITY_NORMAL = "NORMAL"; //$NON-NLS-1$

	/**
	 * Possible configurable option ID.
	 * 
	 * @see #getDefaultOptions()
	 * @since 2.1
	 */
	public static final String COMPILER_TASK_TAGS = PLUGIN_ID + ".compiler.taskTags"; //$NON-NLS-1$
	/**
	 * Possible configurable option ID.
	 * 
	 * @see #getDefaultOptions()
	 * @since 3.0
	 */
	public static final String COMPILER_TASK_CASE_SENSITIVE = PLUGIN_ID + ".compiler.taskCaseSensitive"; //$NON-NLS-1$	
	/**
	 * Possible configurable option value.
	 * 
	 * @see #getDefaultOptions()
	 * @since 2.0
	 */
	public static final String ENABLED = "enabled"; //$NON-NLS-1$
	/**
	 * Possible configurable option value.
	 * 
	 * @see #getDefaultOptions()
	 * @since 2.0
	 */
	public static final String DISABLED = "disabled"; //$NON-NLS-1$

	public RubyCore() {
		super();
		RUBY_CORE_PLUGIN = this;
	}

	/**
	 * Returns the single instance of the Ruby core plug-in runtime class.
	 * 
	 * @return the single instance of the Ruby core plug-in runtime class
	 */
	public static Plugin getPlugin() {
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
		// init preferences
		initializeDefaultPreferences();

		// Listen to instance preferences node removal from parent in order to
		// refresh stored one
		IEclipsePreferences.INodeChangeListener listener = new IEclipsePreferences.INodeChangeListener() {

			public void added(IEclipsePreferences.NodeChangeEvent event) {
			// do nothing
			}

			public void removed(IEclipsePreferences.NodeChangeEvent event) {
				if (event.getChild() == preferencesLookup[PREF_INSTANCE]) {
					preferencesLookup[PREF_INSTANCE] = new InstanceScope().getNode(PLUGIN_ID);
				}
			}
		};
		((IEclipsePreferences) getInstancePreferences().parent()).addNodeChangeListener(listener);

		// Listen to default preferences node removal from parent in order to
		// refresh stored one
		listener = new IEclipsePreferences.INodeChangeListener() {

			public void added(IEclipsePreferences.NodeChangeEvent event) {
			// do nothing
			}

			public void removed(IEclipsePreferences.NodeChangeEvent event) {
				if (event.getChild() == preferencesLookup[PREF_DEFAULT]) {
					preferencesLookup[PREF_DEFAULT] = new DefaultScope().getNode(PLUGIN_ID);
				}
			}
		};
		((IEclipsePreferences) getDefaultPreferences().parent()).addNodeChangeListener(listener);

		IAdapterManager manager = Platform.getAdapterManager();
		manager.registerAdapters(new RubyElementAdapterFactory(), IRubyElement.class);
		manager.registerAdapters(new ResourceAdapterFactory(), IResource.class);
		String rubyParserOption = Platform.getDebugOption(RubyCore.PLUGIN_ID + "/rubyparser");
		RubyParser.setDebugging(rubyParserOption == null ? false : rubyParserOption.equalsIgnoreCase("true"));
	}

	/*
	 * Initializes the default preferences settings for this plug-in.
	 */
	protected void initializeDefaultPreferences() {
		// Init and store default and instance preferences
		IEclipsePreferences defaultPreferences = getDefaultPreferences();

		// Override some compiler defaults
		defaultPreferences.put(COMPILER_TASK_TAGS, DEFAULT_TASK_TAGS);
		defaultPreferences.put(COMPILER_TASK_PRIORITIES, DEFAULT_TASK_PRIORITIES);
		defaultPreferences.put(COMPILER_TASK_CASE_SENSITIVE, ENABLED);
	}

	/**
	 * @since 3.1
	 */
	public static IEclipsePreferences getDefaultPreferences() {
		if (preferencesLookup[PREF_DEFAULT] == null) {
			preferencesLookup[PREF_DEFAULT] = new DefaultScope().getNode(PLUGIN_ID);
		}
		return preferencesLookup[PREF_DEFAULT];
	}

	/**
	 * @since 3.1
	 */
	public static IEclipsePreferences getInstancePreferences() {
		if (preferencesLookup[PREF_INSTANCE] == null) {
			preferencesLookup[PREF_INSTANCE] = new InstanceScope().getNode(PLUGIN_ID);
		}
		return preferencesLookup[PREF_INSTANCE];
	}

	public static void log(Exception e) {
		log(Status.ERROR, e.getMessage(), e);
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
		String location = plugin.getBundle().getLocation();
		int prefixLength = location.indexOf('@');
		if (prefixLength == -1) { throw new RuntimeException("Location of launching bundle does not contain @: " + location); }
		String pluginDir = location.substring(prefixLength + 1);
		if (!new File(pluginDir).exists()) { throw new RuntimeException("Expected directory of eclipseDebug.rb does not exist: " + pluginDir); }
		return pluginDir;
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

	public static RubyProject getRubyProject(String name) {
		IProject aProject = RubyCore.getWorkspace().getRoot().getProject(name);
		if (isRubyProject(aProject)) {
			RubyProject theRubyProject = new RubyProject();
			theRubyProject.setProject(aProject);
			return theRubyProject;
		}
		return null;
	}

	public static boolean isRubyProject(IProject aProject) {
		try {
			return aProject.hasNature(RubyCore.NATURE_ID);
		} catch (CoreException e) {}
		return false;
	}

	public static IRubyScript create(IFile aFile) {
		return create(aFile, null);
	}

	public static IRubyScript create(IFile file, IRubyProject project) {
		if (project == null) {
			project = create(file.getProject());
		}
		// FIXME Use the associations to determine if we should create the file!
		for (int i = 0; i < IRubyScript.EXTENSIONS.length; i++) {
			if (IRubyScript.EXTENSIONS[i].equalsIgnoreCase(file.getFileExtension())) {
				RubyScript script = new RubyScript((RubyProject) project, file, file.getName(), DefaultWorkingCopyOwner.PRIMARY);
				return script;
			}
		}
		return null;
	}

	public static IRubyProject create(IProject aProject) {
		if (aProject == null) { return null; }
		try {
			// TODO Do we need to check for the ruby nature?
			if (aProject.hasNature(RubyCore.NATURE_ID)) { return new RubyProject(aProject, RubyModelManager.getRubyModelManager().getRubyModel()); }
		} catch (CoreException e) {
			RubyCore.log("Exception occurred in RubyCore#create(IProject): " + e.toString());
		}

		return null;
	}

	public static void addRubyNature(IProject project, IProgressMonitor monitor) throws CoreException {
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
}