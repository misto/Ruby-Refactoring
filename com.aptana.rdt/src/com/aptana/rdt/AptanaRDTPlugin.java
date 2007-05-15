package com.aptana.rdt;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Set;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.internal.launching.LaunchingPlugin;
import org.rubypeople.rdt.internal.ui.IRubyStatusConstants;

import com.aptana.rdt.internal.gems.Gem;
import com.aptana.rdt.internal.gems.GemManager;
import com.aptana.rdt.internal.gems.GemManager.GemListener;
import com.aptana.rdt.internal.ui.RubyRedMessages;

/**
 * The activator class controls the plug-in life cycle
 */
public class AptanaRDTPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "com.aptana.rdt";

	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 */
	public static final String COMPILER_PB_UNUSED_PARAMETER = PLUGIN_ID + ".compiler.problem.unusedParameter"; //$NON-NLS-1$
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_UNUSED_PRIVATE_MEMBER = PLUGIN_ID + ".compiler.problem.unusedPrivateMember"; //$NON-NLS-1$
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_UNNECESSARY_ELSE = PLUGIN_ID + ".compiler.problem.unnecessaryElse"; //$NON-NLS-1$

	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_LOCAL_MASKS_METHOD = PLUGIN_ID + ".compiler.problem.localVariableMasksMethod"; //$NON-NLS-1$
		
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_MISSPELLED_CONSTRUCTOR = PLUGIN_ID + ".compiler.problem.misspelledConstructor"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_POSSIBLE_ACCIDENTAL_BOOLEAN_ASSIGNMENT = PLUGIN_ID + ".compiler.problem.possibleAccidentalBooleanAssignment"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_CODE_COMPLEXITY_BRANCHES = PLUGIN_ID + ".compiler.problem.codeComplexityBranches"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_CODE_COMPLEXITY_LINES = PLUGIN_ID + ".compiler.problem.codeComplexityLines"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_CODE_COMPLEXITY_RETURNS = PLUGIN_ID + ".compiler.problem.codeComplexityReturns"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_CODE_COMPLEXITY_LOCALS = PLUGIN_ID + ".compiler.problem.codeComplexityLocals"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_CODE_COMPLEXITY_ARGUMENTS = PLUGIN_ID + ".compiler.problem.codeComplexityArguments"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_MAX_LOCALS = PLUGIN_ID + ".compiler.problem.maxLocals"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_MAX_RETURNS = PLUGIN_ID + ".compiler.problem.maxReturns"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_MAX_BRANCHES = PLUGIN_ID + ".compiler.problem.maxBranches"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_MAX_LINES = PLUGIN_ID + ".compiler.problem.maxLines"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_MAX_ARGUMENTS = PLUGIN_ID + ".compiler.problem.maxArguments"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_SIMILAR_VARIABLE_NAMES = PLUGIN_ID + ".compiler.problem.similarVariableNames"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 0.9.0
	 */
	public static final String COMPILER_PB_UNREACHABLE_CODE = PLUGIN_ID + ".compiler.problem.unreachableCode"; //$NON-NLS-1$
		
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 1.0.0
	 */
	public static final String COMPILER_PB_COMPARABLE_MISSING_METHOD = PLUGIN_ID + ".compiler.problem.comparableMissingMethod"; //$NON-NLS-1$
	
	/**
	 * Possible  configurable option ID.
	 * @see #getDefaultOptions()
	 * @since 1.0.0
	 */
	public static final String COMPILER_PB_ENUMERABLE_MISSING_METHOD = PLUGIN_ID + ".compiler.problem.enumerableMissingMethod"; //$NON-NLS-1$
	
	/**
 	 * Possible  configurable option ID.
 	 * @see #getDefaultOptions()
	 * @since 1.0.0
	 */
	public static final String COMPILER_PB_SUBCLASS_DOESNT_CALL_SUPER = PLUGIN_ID + ".compiler.problem.subclassDoesntCallSuper"; //$NON-NLS-1$

	/**
 	 * Possible  configurable option ID.
 	 * @see #getDefaultOptions()
	 * @since 1.0.0
	 */
	public static final String COMPILER_PB_ASSIGNMENT_PRECEDENCE = PLUGIN_ID + ".compiler.problem.assignmentPrecedence"; //$NON-NLS-1$

	// The shared instance
	private static AptanaRDTPlugin plugin;
	
	/**
	 * The constructor
	 */
	public AptanaRDTPlugin() {
		super();
		plugin = this;
	}
	
	private static class RubyDebugGemListener extends Job {

		private GemListener listener;

		public RubyDebugGemListener(GemListener listener) {
			super("Removing temporary gem listener");
			this.listener = listener;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			GemManager.getInstance().removeGemListener(listener);
			return Status.OK_STATUS;
		}
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		Set<Gem> gems = GemManager.getInstance().getGems();
		if (gems.isEmpty()) {
			GemManager.getInstance().addGemListener(new GemListener() {
		
				public void gemsRefreshed() {
					boolean rubyDebugInstalled = GemManager.getInstance().gemInstalled("ruby-debug-ide");
					LaunchingPlugin.getDefault().getPluginPreferences().setValue(org.rubypeople.rdt.internal.launching.PreferenceConstants.USE_RUBY_DEBUG, rubyDebugInstalled);
					Job job = new RubyDebugGemListener(this);
					job.schedule();
				}
		
				public void gemRemoved(Gem gem) {
					// ignore
				}
		
				public void gemAdded(Gem gem) {
					// ignore		
				}
		
			});
		} else {
			boolean rubyDebugInstalled = GemManager.getInstance().gemInstalled("ruby-debug-ide");
			LaunchingPlugin.getDefault().getPluginPreferences().setValue(org.rubypeople.rdt.internal.launching.PreferenceConstants.USE_RUBY_DEBUG, rubyDebugInstalled);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AptanaRDTPlugin getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}
	
	public static File getFileInPlugin(IPath path) {
		try {
			URL installURL = new URL(
					getDefault().getBundle().getEntry("/"), path.toString()); //$NON-NLS-1$
			URL localURL = FileLocator.toFileURL(installURL);
			return new File(localURL.getFile());
		} catch (IOException ioe) {
			return null;
		}
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, getPluginId(), IRubyStatusConstants.INTERNAL_ERROR, RubyRedMessages.RubyRedPlugin_internal_error, e)); 
	}
	
	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}
		
	public static String getPluginId() {
		return PLUGIN_ID;
	}
}
