package org.rubypeople.rdt.internal.ui.preferences;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.JFacePreferences;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.internal.WorkbenchMessages;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.rubypeople.rdt.internal.ui.RdtUiMessages;
import org.rubypeople.rdt.internal.ui.RdtUiPlugin;
import org.rubypeople.rdt.internal.ui.text.RubyColorConstants;
import sun.beans.editors.ColorEditor;

public class RubyEditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	protected TextPropertyWidget[] textPropertyWidgets;
	protected final String[] colorProperties = { RubyColorConstants.RUBY_KEYWORD, RubyColorConstants.RUBY_MULTI_LINE_COMMENT, RubyColorConstants.RUBY_SINGLE_LINE_COMMENT, RubyColorConstants.RUBY_STRING, RubyColorConstants.RUBY_DEFAULT };

	protected Control createContents(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_FILL));

		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 10;

		composite.setLayout(layout);

		Group colorComposite = new Group(composite, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 3;
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 8;
		layout.marginWidth = 10;
		layout.marginHeight = 10;

		colorComposite.setLayout(layout);
		colorComposite.setText(RdtUiMessages.getString("RubyEditorPropertyPage.highlighting.group")); //$NON-NLS-1$
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		colorComposite.setLayoutData(data);

		Label header = new Label(colorComposite, SWT.BOLD);
		header.setText(RdtUiMessages.getString("RubyEditorPropertyPage.property"));
		header = new Label(colorComposite, SWT.BOLD);
		header.setText(RdtUiMessages.getString("RubyEditorPropertyPage.color"));
		header.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_CENTER));
		header = new Label(colorComposite, SWT.BOLD);
		header.setText(RdtUiMessages.getString("RubyEditorPropertyPage.bold"));
		header.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		textPropertyWidgets = new TextPropertyWidget[colorProperties.length];
		for (int i = 0; i < colorProperties.length; i++) {
			textPropertyWidgets[i] = new TextPropertyWidget(colorComposite, colorProperties[i]);
		}

		return composite;
	}

	protected IPreferenceStore doGetPreferenceStore() {
		return RdtUiPlugin.getDefault().getPreferenceStore();
	}

	public boolean performOk() {
		for (int i = 0; i < textPropertyWidgets.length; i++) {
			TextPropertyWidget widget = textPropertyWidgets[i];
			widget.stringColorEditor.store();
			this.doGetPreferenceStore().setValue(widget.property + RubyColorConstants.RUBY_ISBOLD_APPENDIX, widget.boldCheckBox.getSelection());
		}
		return true;
	}

	public void init(IWorkbench workbench) {
	}

	protected void performDefaults() {
		super.performDefaults();
		for (int i = 0; i < textPropertyWidgets.length; i++) {
			TextPropertyWidget widget = textPropertyWidgets[i];
			widget.stringColorEditor.loadDefault();
			widget.boldCheckBox.setSelection(this.doGetPreferenceStore().getDefaultBoolean(widget.property + RubyColorConstants.RUBY_ISBOLD_APPENDIX));
		}
	}

	class TextPropertyWidget {
		protected ColorFieldEditor stringColorEditor;
		protected Button boldCheckBox;
		protected String property;
		TextPropertyWidget(Composite parent, String property) {
			this.property = property;
			Label label = new Label(parent, SWT.NORMAL);
			label.setText(RdtUiMessages.getString("RubyEditorPropertyPage." + property));

			Composite dummyComposite = new Composite(parent, SWT.NONE);
			// ColorFieldEditor sets its parent composite to 2 columns, therefore a dummyComposite is used here
			stringColorEditor = new ColorFieldEditor(property, "", dummyComposite);
			stringColorEditor.setPreferenceStore(doGetPreferenceStore());
			stringColorEditor.load();

			boldCheckBox = new Button(parent, SWT.CHECK);
			boldCheckBox.setSelection(doGetPreferenceStore().getBoolean(property + RubyColorConstants.RUBY_ISBOLD_APPENDIX));
			boldCheckBox.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		}
	}

}
