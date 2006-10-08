package org.rubypeople.rdt.internal.ui.preferences;

import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.launching.RdtLaunchingPlugin;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.text.PreferencesAdapter;
import org.rubypeople.rdt.ui.PreferenceConstants;


public class DebuggerPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public DebuggerPreferencePage() {
		super(GRID);
		Preferences launchingPreferences = RdtLaunchingPlugin.getDefault().getPluginPreferences() ;
		setPreferenceStore(new PreferencesAdapter(launchingPreferences)) ;
		setDescription(PreferencesMessages.DebuggerPreferencePage_description_label); 
	}
			
	public void createFieldEditors() {
		addField( new BooleanFieldEditor( PreferenceConstants.DEBUGGER_USE_RUBY_DEBUG,
				PreferencesMessages.DebuggerPreferencePage_useRubyDebug_label, getFieldEditorParent() ) ); 
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {}
	
}