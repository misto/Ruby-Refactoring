package org.rubypeople.rdt.internal.ui.preferences;

import java.io.File;

import org.eclipse.core.runtime.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.rubypeople.rdt.internal.core.RubyPlugin;
import org.rubypeople.rdt.internal.core.RubyLibrary;
import org.rubypeople.rdt.internal.ui.dialog.StatusDialog;

public class EditLibraryDialog extends StatusDialog {
	protected RubyLibrary interpreterToEdit;
	protected Text interpreterNameText, interpreterLocationText;
	protected IStatus[] allStatus = new IStatus[2];

	public EditLibraryDialog(Shell parentShell, String aDialogTitle) {
		super(parentShell);
		setTitle(aDialogTitle);
	}

	public void setInterpreterToEdit(RubyLibrary anInterpreter) {
		interpreterToEdit = anInterpreter;

		String interpreterName = interpreterToEdit.getName();
		interpreterNameText.setText(interpreterName != null
				? interpreterName
				: ""); //$NON-NLS-1$
		IPath installLocation = interpreterToEdit.getInstallLocation();
		interpreterLocationText.setText(installLocation != null
				? installLocation.toOSString()
				: ""); //$NON-NLS-1$
	}

	protected void createLocationEntryField(Composite composite) {
		new Label(composite, SWT.NONE).setText("Edit Library Path"); //$NON-NLS-1$
		Composite locationComposite = new Composite(composite, SWT.NONE);
		RowLayout locationLayout = new RowLayout();
		locationLayout.marginLeft = 0;
		locationComposite.setLayout(locationLayout);

		interpreterLocationText = new Text(locationComposite, SWT.SINGLE
				| SWT.BORDER);
		interpreterLocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				allStatus[1] = validateInterpreterLocationText();
				updateStatusLine();
			}
		});
		interpreterLocationText.setLayoutData(new RowData(120, SWT.DEFAULT));

		Button browseButton = new Button(composite, SWT.PUSH);
		browseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		browseButton.setText("Browse"); //$NON-NLS-1$
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseForInstallDir();
			}
		});
	}

	protected void updateStatusLine() {
		updateStatus(getMostSevereStatus());
	}

	protected IStatus getMostSevereStatus() {
		IStatus max = new Status(0, RubyPlugin.PLUGIN_ID, IStatus.OK, "", null); //$NON-NLS-1$
		for (int i = 0; i < allStatus.length; i++) {
			IStatus curr = allStatus[i];
			if (curr != null) {
				if (curr.matches(IStatus.ERROR)) {
					return curr;
				}
				if (max == null || curr.getSeverity() > max.getSeverity()) {
					max = curr;
				}
			}
		}
		return max;
	}

	protected IStatus validateInterpreterLocationText() {
		File path = new File(interpreterLocationText.getText());
		if (path.exists() || path.isDirectory()) {
			return new Status(IStatus.OK, RubyPlugin.PLUGIN_ID, 0, "",
						null); //$NON-NLS-1$
		}

		return new Status(
				IStatus.ERROR,
				RubyPlugin.PLUGIN_ID,
				1,
				"Invalid Path", null); //$NON-NLS-1$
	}

	protected void createNameEntryField(Composite composite) {
		new Label(composite, SWT.NONE).setText("Name"); //$NON-NLS-1$
		GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gridData.horizontalSpan = 2;

		interpreterNameText = new Text(composite, SWT.SINGLE | SWT.BORDER);
		interpreterNameText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				allStatus[0] = validateInterpreterNameText();
				updateStatusLine();
			}
		});
		interpreterNameText.setLayoutData(gridData);
	}

	protected IStatus validateInterpreterNameText() {
		int status = IStatus.OK;
		String message = ""; //$NON-NLS-1$
		if (interpreterNameText.getText() == null
				|| interpreterNameText.getText().length() <= 0) {
			status = IStatus.ERROR;
			message = "Invalid Name"; //$NON-NLS-1$
		}

		return new Status(status, RubyPlugin.PLUGIN_ID, 0, message, null);
	}

	protected void browseForInstallDir() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setFilterPath(interpreterLocationText.getText());
		dialog
				.setMessage("Browse Message"); //$NON-NLS-1$
		String newPath = dialog.open();
		if (newPath != null)
			interpreterLocationText.setText(newPath);
	}

	protected void okPressed() {
		if (interpreterToEdit == null)
			interpreterToEdit = new RubyLibrary(null, null);

		interpreterToEdit.setName(interpreterNameText.getText());
		interpreterToEdit.setInstallLocation(new Path(interpreterLocationText
				.getText()));
		super.okPressed();
	}
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		composite.setLayout(layout);

		createNameEntryField(composite);
		createLocationEntryField(composite);

		return composite;
	}

}
