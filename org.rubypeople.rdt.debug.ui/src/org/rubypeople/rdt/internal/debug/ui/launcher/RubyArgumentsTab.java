package org.rubypeople.rdt.internal.debug.ui.launcher;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class RubyArgumentsTab extends AbstractLaunchConfigurationTab {

	public RubyArgumentsTab() {
		super();
	}

	public void createControl(Composite parent) {
		Composite composite = createPageRoot(parent);

		new Label(composite, SWT.NONE).setText("Arguments tab label");
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		System.out.println("RubyArgumentsTab.setDefaults()");
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		System.out.println("RubyArgumentsTab.initializeFrom()");
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		System.out.println("RubyArgumentsTab.performApply()");
	}

	public boolean isValid() {
		System.out.println("RubyArgumentsTab.isValid():false");

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