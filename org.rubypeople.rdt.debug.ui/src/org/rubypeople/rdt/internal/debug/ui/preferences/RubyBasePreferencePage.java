package org.rubypeople.rdt.internal.debug.ui.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages;

public class RubyBasePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public RubyBasePreferencePage() {
		super();
	}

	public void init(IWorkbench workbench) {}

	protected Control createContents(Composite parent) {
		noDefaultAndApplyButton();

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);

		new Label(composite, SWT.NONE).setText(RdtDebugUiMessages.getString("RubyBasePreferencePage.label")); //$NON-NLS-1$

		return composite;
	}
}
