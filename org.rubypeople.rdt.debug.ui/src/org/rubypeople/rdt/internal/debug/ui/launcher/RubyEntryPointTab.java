package org.rubypeople.rdt.internal.debug.ui.launcher;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.rubypeople.rdt.ui.swtutils.*;

public class RubyEntryPointTab extends AbstractLaunchConfigurationTab {

	public RubyEntryPointTab() {
		super();
	}

	public void createControl(Composite parent) {
		Composite composite = createPageRoot(parent);
		
		new Label(composite, SWT.NONE).setText("Working directory");
		RubyProjectSelector browser = new RubyProjectSelector(composite);
		browser.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		System.out.println("RubyEntryPointTab.setDefaults()");
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		System.out.println("RubyEntryPointTab.initializeFrom()");
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		System.out.println("RubyEntryPointTab.performApply()");
	}

	public boolean isValid() {
		System.out.println("RubyEntryPointTab.isValid():false");

		return false;
	}

	protected Composite createPageRoot(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		setControl(composite);
		return composite;
	}
}