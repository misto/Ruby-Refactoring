package org.rubypeople.rdt.internal.debug.ui;

import org.eclipse.debug.core.ILauncher;
import org.eclipse.debug.ui.ILaunchWizard;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;

public class RubyApplicationWizard extends Wizard implements ILaunchWizard {
	protected Object[] selectedElements;
	protected ILauncher launcher;
	protected String runMode;

	public RubyApplicationWizard() {
	}

	public boolean performFinish()  {
		launcher.launch(selectedElements, runMode);
		return true;
	}

	public void init(ILauncher theLauncher, String mode, IStructuredSelection selection)  {
		launcher = theLauncher;
		runMode = mode;
		selectedElements = selection.toArray();
	}

	public void addPages() {
		super.addPages();
		addPage(new RubyApplicationWizardPage());
	}
}
