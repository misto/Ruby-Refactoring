package org.rubypeople.rdt.internal.debug.ui.launcher;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class RubyEnvironmentTab extends AbstractLaunchConfigurationTab {

	public RubyEnvironmentTab() {
		super();
	}

	public void createControl(Composite parent) {
		Composite composite = createPageRoot(parent);

		new Label(composite, SWT.NONE).setText("Environment tab label");
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		System.out.println("RubyEnvironmentTab.setDefaults()");
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		System.out.println("RubyEnvironmentTab.initializeFrom()");
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		System.out.println("RubyEnvironmentTab.performApply()");
	}

	public boolean isValid() {
		System.out.println("RubyEnvironmentTab.isValid():false");

		return false;
	}

	protected Composite createPageRoot(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		composite.setLayout(layout);
		setControl(composite);
		return composite;
	}
}
