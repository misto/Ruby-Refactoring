package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IStatus;
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
import org.osgi.framework.BundleContext;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.formatter.CodeFormatter;
import org.rubypeople.rdt.internal.ui.text.RubyColorConstants;
import org.rubypeople.rdt.internal.ui.text.RubyTextTools;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class RdtUiPlugin extends AbstractUIPlugin implements RubyColorConstants {

	protected static RdtUiPlugin plugin;

	public static final String PLUGIN_ID = "org.rubypeople.rdt.ui"; //$NON-NLS-1$
	public static final String RUBY_RESOURCES_VIEW_ID = PLUGIN_ID + ".ViewRubyResources"; //$NON-NLS-1$
	public static final String EDITOR_ID = "org.rubypeople.rdt.ui.EditorRubyFile"; //$NON-NLS-1$
	public static final String EXTERNAL_FILES_EDITOR_ID = "org.rubypeople.rdt.ui.ExternalRubyEditor"; //$NON-NLS-1$

	protected RubyTextTools textTools;
	protected RubyFileMatcher rubyFileMatcher;

	public RdtUiPlugin() {
		super();
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		//		 initialize textTools before any RubyEditor gets initialized, so that
		// textTools can
		// register for property change events first. If the registration takes
		// place in the
		// wrong order, changes to properties in the preferences page are not
		// immediately updated
		// within the ruby editors.
		textTools = new RubyTextTools();
	}

	public static RdtUiPlugin getDefault() {
		return plugin;
	}

	public static IWorkspace getWorkspace() {
		return RubyPlugin.getWorkspace();
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(Throwable e) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, IStatus.ERROR, RdtUiMessages.getString("RdtUiPlugin.internalErrorOccurred"), e)); //$NON-NLS-1$
	}

	public static void log(int severity, String message, Throwable e) {
		Status status = new Status(severity, PLUGIN_ID, IStatus.OK, message, e);
		RdtUiPlugin.log(status);
	}

	public static Shell getActiveWorkbenchShell() {
		return getActiveWorkbenchWindow().getShell();
	}

	public RubyTextTools getTextTools() {
		return textTools;
	}

	public CodeFormatter getCodeFormatter() {
		char indentChar = this.getPreferenceStore().getBoolean(PreferenceConstants.FORMAT_USE_TAB) ? '\t' : ' ';
		CodeFormatter codeFormatter = new CodeFormatter(indentChar, RubyPlugin.getDefault().isCodeFormatterDebugging());
		codeFormatter.setIndentation(this.getPreferenceStore().getInt(PreferenceConstants.FORMAT_INDENTATION));
		return codeFormatter;
	}

	protected void initializeDefaultPreferences(IPreferenceStore store) {
		PreferenceConverter.setDefault(store, RUBY_DEFAULT, new RGB(0, 0, 0));
		store.setDefault(RUBY_DEFAULT + RUBY_ISBOLD_APPENDIX, false);
		PreferenceConverter.setDefault(store, RUBY_KEYWORD, new RGB(164, 53, 122));
		store.setDefault(RUBY_KEYWORD + RUBY_ISBOLD_APPENDIX, true);
		PreferenceConverter.setDefault(store, RUBY_STRING, new RGB(42, 0, 255));
		store.setDefault(RUBY_STRING + RUBY_ISBOLD_APPENDIX, false);
		PreferenceConverter.setDefault(store, RUBY_MULTI_LINE_COMMENT, new RGB(63, 127, 95));
		store.setDefault(RUBY_MULTI_LINE_COMMENT + RUBY_ISBOLD_APPENDIX, false);
		PreferenceConverter.setDefault(store, RUBY_SINGLE_LINE_COMMENT, new RGB(63, 127, 95));
		store.setDefault(RUBY_SINGLE_LINE_COMMENT + RUBY_ISBOLD_APPENDIX, false);
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
			rubyFileMatcher = new RubyFileMatcher() ;
		}
		return rubyFileMatcher ;
	}
	
	public IResource getSelectedResource() {
		IWorkbenchPage page = RdtUiPlugin.getActivePage();
		if (page == null) {
			return null;
		}
		// first try: a selection in the navigator or ruby resource view
		ISelection selection = page.getSelection();
		if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			Object obj = structuredSelection.getFirstElement();
			if (obj instanceof IResource) {
				return (IResource) obj;
			}
		}
		// second try: an editor is selected
		IEditorPart part = page.getActiveEditor();
		if (part == null) {
			return null;
		}
		IEditorInput input = part.getEditorInput();
		return (IResource) input.getAdapter(IResource.class);
	}
	
	public boolean isRubyFile(IFile file) {
	   // TODO: this is work in progress. Once we can use the content-type
	   // extension point, this method must be removed
	   return this.getRubyFileMatcher().hasRubyEditorAssociation(file) ;
	}
	
	public boolean isRubyFile(IResource resource) {
	    if (resource == null || !(resource instanceof IFile)) {
	        return false ;	        
	    }
	    return isRubyFile((IFile) resource) ;
	}	
	
}