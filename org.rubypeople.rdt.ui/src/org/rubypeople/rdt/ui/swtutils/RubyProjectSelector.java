package org.rubypeople.rdt.ui.swtutils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.ui.dialog.RubyProjectListSelectionDialog;

public class RubyProjectSelector {
	protected Composite composite;
	protected Text textField;
	protected Button browseButton;

	public RubyProjectSelector(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.numColumns = 2;
		composite.setLayout(compositeLayout);

		textField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				browseForProject();
			}
		});
	}

	public void setLayoutData(Object layoutData) {
		composite.setLayoutData(layoutData);
	}

	protected void browseForProject() {
		RubyProjectListSelectionDialog dialog = new RubyProjectListSelectionDialog(getShell());

		if (dialog.open() == dialog.OK) {
			textField.setText(((RubyProject) dialog.getResult()[0]).getProject().getName());
		}
	}

	protected Shell getShell() {
		return composite.getShell();
	}
}