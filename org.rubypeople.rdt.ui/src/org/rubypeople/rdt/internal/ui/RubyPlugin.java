package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.editors.text.TextEditorPreferenceConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.core.IBuffer;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.WorkingCopyOwner;
import org.rubypeople.rdt.internal.formatter.CodeFormatter;
import org.rubypeople.rdt.internal.ui.preferences.MockupPreferenceStore;
import org.rubypeople.rdt.internal.ui.rubyeditor.DocumentAdapter;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyDocumentProvider;
import org.rubypeople.rdt.internal.ui.rubyeditor.WorkingCopyManager;
import org.rubypeople.rdt.internal.ui.text.IRubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.PreferencesAdapter;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;
import org.rubypeople.rdt.internal.ui.text.folding.RubyFoldingStructureProviderRegistry;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class RubyPlugin extends AbstractUIPlugin implements IRubyColorConstants {

	protected static RubyPlugin plugin;
	public static final String PLUGIN_ID = "org.rubypeople.rdt.ui"; //$NON-NLS-1$

	protected RubyTextTools textTools;
	protected RubyFileMatcher rubyFileMatcher;
	private IWorkingCopyManager fWorkingCopyManager;
	private RubyDocumentProvider fDocumentProvider;
	
	/**
	 * The combined preference store.
	 * @since 3.0
	 */
	private IPreferenceStore fCombinedPreferenceStore;

	/**
	 * Mockup preference store for firing events and registering listeners on
	 * project setting changes. FIXME: Temporary solution.
	 * 
	 * @since 3.0
	 */
	private MockupPreferenceStore fMockupPreferenceStore;

	private RubyFoldingStructureProviderRegistry fFoldingStructureProviderRegistry;

	public RubyPlugin() {
		super();
		plugin = this;
	}

	/**
	 * Returns the mock-up preference store for firing events and registering
	 * listeners on project setting changes. Temporary solution.
	 * 
	 * @return the mock-up preference store
	 */
	public MockupPreferenceStore getMockupPreferenceStore() {
		if (fMockupPreferenceStore == null) fMockupPreferenceStore = new MockupPreferenceStore();

		return fMockupPreferenceStore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		// initialize textTools before any RubyEditor gets initialized, so that
		// textTools can
		// register for property change events first. If the registration takes
		// place in the
		// wrong order, changes to properties in the preferences page are not
		// immediately updated
		// within the ruby editors.
		//getTextTools();

		// Here's where the magic happens that makes the IRubyScript's contents
		// get re-routed to the IDocument's latest contents
		WorkingCopyOwner.setPrimaryBufferProvider(new WorkingCopyOwner() {

			public IBuffer createBuffer(IRubyScript workingCopy) {
				IRubyScript original = workingCopy.getPrimary();
				IResource resource = original.getResource();
				if (resource instanceof IFile) return new DocumentAdapter(workingCopy, (IFile) resource);
				return DocumentAdapter.NULL;
			}
		});

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		try {
			if (fWorkingCopyManager != null) {
				fWorkingCopyManager.shutdown();
				fWorkingCopyManager = null;
			}

			if (fDocumentProvider != null) {
				fDocumentProvider.shutdown();
				fDocumentProvider = null;
			}
			if (textTools != null) {
				textTools.dispose();
				textTools = null;
			}
		} finally {
			super.stop(context);
		}
	}

	/**
	 * @param string
	 */
	public static void log(String string) {
		log(IStatus.OK, string);

	}

	public static RubyPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return RubyCore.getWorkspace();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
		System.out.println(status.getMessage());
		if (status.getException() != null) status.getException().printStackTrace();
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, RubyUIMessages.getString("RdtUiPlugin.internalErrorOccurred"), e)); //$NON-NLS-1$
	}

	public static void log(int severity, String message, Throwable e) {
		Status status = new Status(severity, PLUGIN_ID, IStatus.OK, message, e);
		RubyPlugin.log(status);
	}

	public static Shell getActiveWorkbenchShell() {
		return getActiveWorkbenchWindow().getShell();
	}

	public synchronized RubyTextTools getTextTools() {
		if (textTools == null) textTools = new RubyTextTools(getPreferenceStore(), RubyCore.getPlugin().getPluginPreferences());
		return textTools;
	}

	public CodeFormatter getCodeFormatter() {
		char indentChar = this.getPreferenceStore().getBoolean(PreferenceConstants.FORMAT_USE_TAB) ? '\t' : ' ';
		String codeFormatterOption = Platform.getDebugOption(RubyCore.PLUGIN_ID + "/codeformatter");
		boolean isDebug = codeFormatterOption == null ? false : codeFormatterOption.equalsIgnoreCase("true");
		CodeFormatter codeFormatter = new CodeFormatter(indentChar, isDebug);
		codeFormatter.setIndentation(this.getPreferenceStore().getInt(PreferenceConstants.FORMAT_INDENTATION));
		return codeFormatter;
	}

	protected void initializeDefaultPreferences(IPreferenceStore store) {
		PreferenceConverter.setDefault(store, RUBY_DEFAULT, new RGB(0, 0, 0));
		store.setDefault(RUBY_DEFAULT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(RUBY_DEFAULT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		PreferenceConverter.setDefault(store, RUBY_KEYWORD, new RGB(164, 53, 122));
		store.setDefault(RUBY_KEYWORD + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(RUBY_KEYWORD + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		PreferenceConverter.setDefault(store, RUBY_STRING, new RGB(42, 0, 255));
		store.setDefault(RUBY_STRING + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(RUBY_STRING + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		PreferenceConverter.setDefault(store, RUBY_MULTI_LINE_COMMENT, new RGB(63, 127, 95));
		store.setDefault(RUBY_MULTI_LINE_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(RUBY_MULTI_LINE_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		PreferenceConverter.setDefault(store, RUBY_SINGLE_LINE_COMMENT, new RGB(63, 127, 95));
		store.setDefault(RUBY_SINGLE_LINE_COMMENT + PreferenceConstants.EDITOR_BOLD_SUFFIX, false);
		store.setDefault(RUBY_SINGLE_LINE_COMMENT + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		PreferenceConverter.setDefault(store, TASK_TAG, new RGB(127, 159, 191));
		store.setDefault(TASK_TAG + PreferenceConstants.EDITOR_BOLD_SUFFIX, true);
		store.setDefault(TASK_TAG + PreferenceConstants.EDITOR_ITALIC_SUFFIX, false);
		
		//
		EditorsUI.useAnnotationsPreferencePage(store);
		EditorsUI.useQuickDiffPreferencePage(store);

		PreferenceConverter.setDefault(store, RUBY_CONTENT_ASSISTANT_BACKGROUND, new RGB(150, 150, 0));
		PreferenceConstants.initializeDefaultValues(store);
		TextEditorPreferenceConstants.initializeDefaultValues(store);
	}

	/**
	 * @return
	 */
	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window = getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window == null) return null;
		return getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
	}

	public RubyFileMatcher getRubyFileMatcher() {
		// be lazy in Plugin class
		if (rubyFileMatcher == null) {
			rubyFileMatcher = new RubyFileMatcher();
		}
		return rubyFileMatcher;
	}

	public IResource getSelectedResource() {
		IWorkbenchPage page = RubyPlugin.getActivePage();
		if (page == null) { return null; }
		// first try: a selection in the navigator or ruby resource view
		ISelection selection = page.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object obj = structuredSelection.getFirstElement();
			if (obj instanceof IResource) { return (IResource) obj; }
		}
		// second try: an editor is selected
		IEditorPart part = page.getActiveEditor();
		if (part == null) { return null; }
		IEditorInput input = part.getEditorInput();
		return (IResource) input.getAdapter(IResource.class);
	}

	public boolean isRubyFile(IFile file) {
		// TODO: this is work in progress. Once we can use the content-type
		// extension point, this method must be removed
		return this.getRubyFileMatcher().hasRubyEditorAssociation(file);
	}

	public boolean isRubyFile(IResource resource) {
		if (resource == null || !(resource instanceof IFile)) { return false; }
		return isRubyFile((IFile) resource);
	}

	/**
	 * @return
	 */
	public IWorkingCopyManager getWorkingCopyManager() {
		if (fWorkingCopyManager == null) {
			RubyDocumentProvider provider = getRubyDocumentProvider();
			fWorkingCopyManager = new WorkingCopyManager(provider);
		}
		return fWorkingCopyManager;
	}

	public synchronized RubyDocumentProvider getRubyDocumentProvider() {
		if (fDocumentProvider == null) fDocumentProvider = new RubyDocumentProvider();
		return fDocumentProvider;
	}

	/**
	 * Returns the registry of the extensions to the
	 * <code>org.rubypeople.rdt.ui.rubyFoldingStructureProvider</code>
	 * extension point.
	 * 
	 * @return the registry of contributed
	 *         <code>IRubyFoldingStructureProvider</code>
	 * @since 3.0
	 */
	public synchronized RubyFoldingStructureProviderRegistry getFoldingStructureProviderRegistry() {
		if (fFoldingStructureProviderRegistry == null) fFoldingStructureProviderRegistry = new RubyFoldingStructureProviderRegistry();
		return fFoldingStructureProviderRegistry;
	}

	/**
	 * @return
	 */
	public static String getPluginId() {
		return PLUGIN_ID;
	}

	/**
	 * @param string
	 */
	public static void log(int severity, String string) {
		log(new Status(severity, PLUGIN_ID, IStatus.OK, string, null));
	}

	/**
	 * Returns a combined preference store, this store is read-only.
	 * 
	 * @return the combined preference store
	 * 
	 * @since 3.0
	 */
	public IPreferenceStore getCombinedPreferenceStore() {
		if (fCombinedPreferenceStore == null) {
			IPreferenceStore generalTextStore= EditorsUI.getPreferenceStore(); 
			fCombinedPreferenceStore= new ChainedPreferenceStore(new IPreferenceStore[] { getPreferenceStore(), new PreferencesAdapter(RubyCore.getPlugin().getPluginPreferences()), generalTextStore });
		}
		return fCombinedPreferenceStore;
	}

	public static void logErrorMessage(String message) {
		log(new Status(IStatus.ERROR, getPluginId(), IRubyStatusConstants.INTERNAL_ERROR, message, null));
	}

}