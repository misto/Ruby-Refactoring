package org.rubypeople.rdt.internal.ui;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.IPluginDescriptor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.editors.text.TextEditorPreferenceConstants;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.texteditor.MarkerAnnotationPreferences;
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

	public RdtUiPlugin(IPluginDescriptor descriptor) {
		super(descriptor);
		plugin = this;
		// initialize textTools before any RubyEditor gets initialized, so that
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
		char indentChar = this.getPreferenceStore().getBoolean(PreferenceConstants.FORMAT_USE_TAB) ? '\t' : ' ' ;
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
		PreferenceConverter.setDefault(store, RUBY_CONTENT_ASSISTANT_BACKGROUND, new RGB(150, 150, 0));
		PreferenceConstants.initializeDefaultValues(store);
		super.initializeDefaultPreferences(store);
		MarkerAnnotationPreferences.initializeDefaultValues(store);
		TextEditorPreferenceConstants.initializeDefaultValues(store);
	}

	/**
	 * @return
	 */
	public static IWorkbenchPage getActivePage() {
		IWorkbenchWindow window = getDefault().getWorkbench().getActiveWorkbenchWindow();
		if (window == null)
			return null;
		else
			return getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

	}

}
