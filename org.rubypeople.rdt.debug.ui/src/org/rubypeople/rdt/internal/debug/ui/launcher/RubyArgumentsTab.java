package org.rubypeople.rdt.internal.debug.ui.launcher;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.rubypeople.rdt.debug.ui.RdtDebugUiConstants;
import org.rubypeople.rdt.debug.ui.RdtDebugUiImages;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiMessages;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.launching.RubyLaunchConfigurationAttribute;
import org.rubypeople.rdt.internal.ui.utils.DirectorySelector;

public class RubyArgumentsTab extends AbstractLaunchConfigurationTab {
	protected Text interpreterArgsText, programArgsText;
	protected DirectorySelector workingDirectorySelector;
	protected Button useDefaultWorkingDirectoryButton;

	public RubyArgumentsTab() {
		super();
	}

	public void createControl(Composite parent) {
		Composite composite = createPageRoot(parent);

		new Label(composite, SWT.NONE).setText(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyArguments.working_dir"));
		workingDirectorySelector = new DirectorySelector(composite);
		workingDirectorySelector.setBrowseDialogMessage(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyArguments.working_dir_browser_message"));
		workingDirectorySelector.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		workingDirectorySelector.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		Composite defaultWorkingDirectoryComposite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		defaultWorkingDirectoryComposite.setLayout(layout);
		useDefaultWorkingDirectoryButton = new Button(defaultWorkingDirectoryComposite, SWT.CHECK);
		useDefaultWorkingDirectoryButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				setUseDefaultWorkingDirectory(((Button) e.getSource()).getSelection());
			}
		});
		new Label(defaultWorkingDirectoryComposite, SWT.NONE).setText(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyArguments.working_dir_use_default_message"));
		defaultWorkingDirectoryComposite.pack();

		Label verticalSpacer = new Label(composite, SWT.NONE);

		new Label(composite, SWT.NONE).setText(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyArguments.interpreter_args_box_title"));
		interpreterArgsText = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		interpreterArgsText.setLayoutData(new GridData(GridData.FILL_BOTH));

		new Label(composite, SWT.NONE).setText(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyArguments.program_args_box_title"));
		programArgsText = new Text(composite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		programArgsText.setLayoutData(new GridData(GridData.FILL_BOTH));
	}

	protected void setUseDefaultWorkingDirectory(boolean useDefault) {
		if (!useDefaultWorkingDirectoryButton.getSelection() == useDefault)
			useDefaultWorkingDirectoryButton.setSelection(useDefault);
		if (useDefault)
			workingDirectorySelector.setSelectionText(RdtDebugUiConstants.DEFAULT_WORKING_DIRECTORY);
		workingDirectorySelector.setEnabled(!useDefault);
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		setUseDefaultWorkingDirectory(true);
		configuration.setAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, RdtDebugUiConstants.DEFAULT_WORKING_DIRECTORY);
		// set hidden attribute
		configuration.setAttribute(ILaunchConfiguration.ATTR_SOURCE_LOCATOR_ID, "org.rubypeople.rdt.debug.ui.rubySourceLocator") ;
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		String workingDirectory = "", interpreterArgs = "", programArgs = "";
		boolean useDefaultWorkDir = true;
		try {
			workingDirectory = configuration.getAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, "");
			interpreterArgs = configuration.getAttribute(RubyLaunchConfigurationAttribute.INTERPRETER_ARGUMENTS, "");
			programArgs = configuration.getAttribute(RubyLaunchConfigurationAttribute.PROGRAM_ARGUMENTS, "");
			useDefaultWorkDir = configuration.getAttribute(RubyLaunchConfigurationAttribute.USE_DEFAULT_WORKING_DIRECTORY, true);
		} catch (CoreException e) {
			log(e);
		}

		workingDirectorySelector.setSelectionText(workingDirectory);
		interpreterArgsText.setText(interpreterArgs);
		programArgsText.setText(programArgs);
		setUseDefaultWorkingDirectory(useDefaultWorkDir);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, workingDirectorySelector.getValidatedSelectionText());
		configuration.setAttribute(RubyLaunchConfigurationAttribute.INTERPRETER_ARGUMENTS, interpreterArgsText.getText());
		configuration.setAttribute(RubyLaunchConfigurationAttribute.PROGRAM_ARGUMENTS, programArgsText.getText());
		configuration.setAttribute(RubyLaunchConfigurationAttribute.USE_DEFAULT_WORKING_DIRECTORY, useDefaultWorkingDirectoryButton.getSelection());
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

	public String getName() {
		return RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyArguments.name");
	}

	public boolean isValid(ILaunchConfiguration launchConfig) {
		try {
			String workingDirectory = launchConfig.getAttribute(RubyLaunchConfigurationAttribute.WORKING_DIRECTORY, "");
			if (workingDirectory.length() == 0) {
				setErrorMessage(RdtDebugUiMessages.getString("LaunchConfigurationTab.RubyArguments.working_dir_error_message"));
				return false;
			}
		} catch (CoreException e) {
			log(e);
		}

		setErrorMessage(null);
		return true;
	}
	
	protected void log(Throwable t) {
		RdtDebugUiPlugin.getDefault().log(t);
	}

	public Image getImage() {
		return RdtDebugUiImages.get(RdtDebugUiImages.IMG_EVIEW_ARGUMENTS_TAB);
	}

}