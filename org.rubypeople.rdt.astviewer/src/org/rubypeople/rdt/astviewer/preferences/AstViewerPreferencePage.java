package org.rubypeople.rdt.astviewer.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.rubypeople.rdt.astviewer.Activator;

public class AstViewerPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {

	public AstViewerPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("Select the nodes you want to see in the tree:");
	}
	
	public void createFieldEditors() {

		addField( new BooleanFieldEditor(
				PreferenceConstants.P_SHOW_NEWLINE,
				"show &NewlineNodes",
				getFieldEditorParent()));
		
		addField( new BooleanFieldEditor(
				PreferenceConstants.P_SHOW_SCOPE,
				"show &ScopeNodes",
				getFieldEditorParent()));
	}

	public void init(IWorkbench workbench) {
	}
	
}