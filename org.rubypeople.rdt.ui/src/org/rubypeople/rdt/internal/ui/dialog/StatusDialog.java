package org.rubypeople.rdt.internal.ui.dialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

public abstract class StatusDialog extends Dialog {
	protected StatusLabel statusLabel;
	protected String title;

	protected StatusDialog(Shell parentShell, String theTitle) {
		super(parentShell);
		title = theTitle;
	}

	protected void updateStatus(IStatus status) {
		updateOkButtonState(status);
		if (statusLabel != null)
			statusLabel.setStatus(status);
	}

	protected void updateOkButtonState(IStatus status) {
		Button okButton = super.getOKButton();
		if (okButton != null && !okButton.isDisposed())
			okButton.setEnabled(!status.matches(IStatus.ERROR));
	}

	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		if (title != null)
			shell.setText(title);
	}
	protected Control createDialogArea(Composite parent) {
		Composite superDialogArea = (Composite) super.createDialogArea(parent);

		FillLayout fillLayout = new FillLayout();
		fillLayout.type = SWT.VERTICAL;
		superDialogArea.setLayout(fillLayout);

		createStatusDialogAreaContents(new Composite(superDialogArea, SWT.NONE));
		statusLabel = new StatusLabel(superDialogArea);
		statusLabel.label.setAlignment(SWT.LEFT);

		return superDialogArea;
	}

	protected abstract void createStatusDialogAreaContents(Composite parent);
}