package org.rubypeople.rdt.internal.ui.dialog;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class StatusLabel {
	public Label label;

	public StatusLabel(Composite parent) {
		label = new Label(parent, SWT.NONE);
	}

	public void setStatus(IStatus status) {
		switch (status.getSeverity()) {
			case IStatus.ERROR :
			case IStatus.WARNING :
				label.setText(status.getMessage());
				break;

			default :
				label.setText("");
		}		
	}
}
