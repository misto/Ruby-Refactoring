package org.rubypeople.rdt.internal.debug.ui.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.rubypeople.rdt.internal.launching.*;
import org.rubypeople.rdt.internal.ui.utils.DirectorySelector;

public class RubyArgumentsTab extends AbstractLaunchConfigurationTab {
	protected Text interpreterArgsText, programArgsText;
	protected DirectorySelector workingDirectorySelector;

	public RubyArgumentsTab() {
		super();
	}

	public void createControl(Composite parent) {
		Composite composite = createPageRoot(parent);
		
		new Label(composite, SWT.NONE).setText("Working Directory:");
		workingDirectorySelector = new DirectorySelector(composite);
		workingDirectorySelector.setBrowseDialogMessage("Select a working directory for the launch configuration");
		workingDirectorySelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label verticalSpacer = new Label(composite, SWT.NONE);

		new Label(composite, SWT.NONE).setText("Interpreter Arguments:");
		interpreterArgsText = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		interpreterArgsText.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(composite, SWT.NONE).setText("Program Arguments:");
		programArgsText = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		programArgsText.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {}

	public void initializeFrom(ILaunchConfiguration configuration) {
		String workingDirectory = "", interpreterArgs = "", programArgs = "";

		try {
			workingDirectory = configuration.getAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, "");
			interpreterArgs = configuration.getAttribute(RubyLaunchConfigurationAttribute.INTERPRETER_ARGUMENTS, "");
			programArgs = configuration.getAttribute(RubyLaunchConfigurationAttribute.PROGRAM_ARGUMENTS, "");
		} catch (CoreException e) {}

		workingDirectorySelector.setSelectionText(workingDirectory);
		interpreterArgsText.setText(interpreterArgs);
		programArgsText.setText(programArgs);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, workingDirectorySelector.getSelectionText());
		configuration.setAttribute(RubyLaunchConfigurationAttribute.INTERPRETER_ARGUMENTS, interpreterArgsText.getText());
		configuration.setAttribute(RubyLaunchConfigurationAttribute.PROGRAM_ARGUMENTS, programArgsText.getText());
	}

	public boolean isValid() {
		return true;
	}

	protected Composite createPageRoot(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginWidth = 0;
		compositeLayout.numColumns = 1;
		composite.setLayout(compositeLayout);

		setControl(composite);
		return composite;
	}
}