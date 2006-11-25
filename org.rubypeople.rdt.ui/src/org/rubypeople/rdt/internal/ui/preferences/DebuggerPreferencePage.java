package org.rubypeople.rdt.internal.ui.preferences;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Preferences;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.rubypeople.rdt.internal.launching.RdtLaunchingPlugin;
import org.rubypeople.rdt.internal.ui.text.PreferencesAdapter;
import org.rubypeople.rdt.ui.PreferenceConstants;

public class DebuggerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	public DebuggerPreferencePage() {
		super(GRID);
		Preferences launchingPreferences = RdtLaunchingPlugin.getDefault().getPluginPreferences();
		setPreferenceStore(new PreferencesAdapter(launchingPreferences));
		setDescription(PreferencesMessages.DebuggerPreferencePage_description_label);
	}
	

	public void createFieldEditors() {
		addField(new BooleanFieldEditor(PreferenceConstants.DEBUGGER_USE_RUBY_DEBUG, PreferencesMessages.DebuggerPreferencePage_useRubyDebug_label, getFieldEditorParent()));
	}

	protected Control createContents(Composite parent) {
		Control result = super.createContents(parent);
		Label label = new Label(parent, SWT.WRAP);
		URL entry = RdtLaunchingPlugin.getDefault().getBundle().getEntry("/");
		String installLocation;
		try {
			installLocation = FileLocator.resolve(entry).toString();
		} catch (IOException e) {
			installLocation = "<eclipseInstallation>/plugins/org.rubypeople.rdt.launching_<version>";
		}
		String message = MessageFormat.format(PreferencesMessages.DebuggerPreferencePage_useRubyDebug_comment, new Object[] { installLocation });
		label.setText(message);

		FontData[] fontData = getFont().getFontData();
		if (fontData.length > 0) {
			FontData italicFont = new FontData(fontData[0].name, fontData[0].height, SWT.ITALIC);
			label.setFont(new Font(null, italicFont));
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {}

}