package org.rubypeople.rdt.internal.debug.ui.preferences;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;
import org.rubypeople.rdt.internal.ui.dialog.StatusDialog;
import org.rubypeople.rdt.launching.RubyInterpreter;
import sun.security.krb5.internal.i;

public class EditInterpreterDialog extends StatusDialog {
	protected RubyInterpreter interpreterToEdit;
	protected Text interpreterNameText, interpreterLocationText;
	protected IStatus[] allStatus = new IStatus[2];

	protected EditInterpreterDialog(Shell parentShell, String aDialogTitle) {
		super(parentShell);
		setTitle(aDialogTitle);
	}
	
	protected void setInterpreterToEdit(RubyInterpreter anInterpreter) {
		interpreterToEdit = anInterpreter;
		
		String interpreterName = interpreterToEdit.getName();
		interpreterNameText.setText(interpreterName != null ? interpreterName : "");

		IPath installLocation = interpreterToEdit.getInstallLocation();
		interpreterLocationText.setText(installLocation != null ? installLocation.toOSString() : "");
	}

	protected void createLocationEntryField(Composite composite) {
		new Label(composite, SWT.NONE).setText("Location:");

		Composite locationComposite = new Composite(composite, SWT.NONE);
		RowLayout locationLayout = new RowLayout();
		locationLayout.marginLeft = 0;
		locationComposite.setLayout(locationLayout);

		interpreterLocationText = new Text(locationComposite, SWT.SINGLE | SWT.BORDER);
		interpreterLocationText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				allStatus[1] = validateInterpreterLocationText();
				updateStatusLine();
			}
		});
		interpreterLocationText.setLayoutData(new RowData(120, SWT.DEFAULT));

		Button browseButton = new Button(composite, SWT.PUSH);
		browseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		browseButton.setText("Browse...");
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
		IStatus max = new Status(0, RdtDebugUiPlugin.PLUGIN_ID, IStatus.OK, "", null);
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
		if (path.exists()) {
			File rubyw = new File(path, "bin" + File.separator + "rubyw");
			File rubyw_exe = new File(path, "bin" + File.separator + "rubyw.exe");
			if (rubyw.isFile() || rubyw_exe.isFile())
				return new Status(IStatus.OK, RdtDebugUiPlugin.PLUGIN_ID, 0, "", null);
		}

		return new Status(IStatus.ERROR, RdtDebugUiPlugin.PLUGIN_ID, 1, "The directory containing bin/rubyw must be selected", null);
	}

	protected void createNameEntryField(Composite composite) {
		new Label(composite, SWT.NONE).setText("Interpreter Name:");

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
		String message = "";

		if (interpreterNameText.getText() == null || interpreterNameText.getText().length() <= 0) {
			status = IStatus.ERROR;
			message = "Name cannot be empty";
		}

		return new Status(status, RdtDebugUiPlugin.PLUGIN_ID, 0, message, null);
	}

	protected void browseForInstallDir() {
		DirectoryDialog dialog = new DirectoryDialog(getShell());
		dialog.setFilterPath(interpreterLocationText.getText());
		dialog.setMessage("Choose location");
		String newPath = dialog.open();
		if (newPath != null)
			interpreterLocationText.setText(newPath);
	}

	protected void okPressed() {
		if (interpreterToEdit == null)
			interpreterToEdit = new RubyInterpreter(null, null);

		interpreterToEdit.setName(interpreterNameText.getText());
		interpreterToEdit.setInstallLocation(new Path(interpreterLocationText.getText()));
		super.okPressed();
	}
	

	protected void createStatusDialogAreaContents(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		parent.setLayout(layout);

		createNameEntryField(parent);
		createLocationEntryField(parent);
	}

}