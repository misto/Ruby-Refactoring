package org.rubypeople.rdt.internal.debug.ui;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILauncher;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;

public class RubyApplicationWizardPage extends WizardPage {

	protected RubyApplicationWizardPage() {
		super(RdtDebugUiMessages.getString("RubyApplicationWizardPage.name")); //$NON-NLS-1$
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = true;
		composite.setLayout(layout);
		
		new Label(composite, SWT.NONE).setText("Launch Ruby Application"); //$NON-NLS-1$
		
		setDescription(RdtDebugUiMessages.getString("RubyApplicationWizardPage.description")); //$NON-NLS-1$
		setTitle(RdtDebugUiMessages.getString("RubyApplicationWizardPage.title")); //$NON-NLS-1$
		setControl(composite);
	}

}
