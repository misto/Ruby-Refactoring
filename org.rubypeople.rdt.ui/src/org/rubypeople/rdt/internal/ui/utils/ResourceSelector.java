package org.rubypeople.rdt.internal.ui.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public abstract class ResourceSelector {
	protected Composite composite;
	protected Button browseButton;
	protected Text textField;
	protected String browseDialogMessage = "", browseDialogTitle = "";

	public ResourceSelector(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		GridLayout compositeLayout = new GridLayout();
		compositeLayout.marginWidth = 0;
		compositeLayout.marginHeight = 0;
		compositeLayout.numColumns = 2;
		composite.setLayout(compositeLayout);

		textField = new Text(composite, SWT.SINGLE | SWT.BORDER);
		textField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		browseButton = new Button(composite, SWT.PUSH);
		browseButton.setText("Browse...");
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowseSelected();
			}
		});
	}

	protected abstract void handleBrowseSelected();

	protected Shell getShell() {
		return composite.getShell();
	}

	public void setLayoutData(Object layoutData) {
		composite.setLayoutData(layoutData);
	}

	public void addModifyListener(ModifyListener aListener) {
		textField.addModifyListener(aListener);
	}

	public void setBrowseDialogMessage(String aMessage) {
		browseDialogMessage = aMessage;
	}

	public void setBrowseDialogTitle(String aTitle) {
		browseDialogTitle = aTitle;
	}

	public String getSelectionText() {
		return textField.getText();
	}

	public void setSelectionText(String projectName) {
		textField.setText(projectName);
	}
}
