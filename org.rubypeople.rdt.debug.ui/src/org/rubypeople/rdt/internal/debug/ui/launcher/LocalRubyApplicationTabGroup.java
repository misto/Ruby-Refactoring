package org.rubypeople.rdt.internal.debug.ui.launcher;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

public class LocalRubyApplicationTabGroup
	extends AbstractLaunchConfigurationTabGroup {

	public LocalRubyApplicationTabGroup() {
		super();
	}

	/**
	 * @see ILaunchConfigurationTabGroup#createTabs(ILaunchConfigurationDialog, String)
	 */
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {
		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
			new RubyEntryPointTab(),
			new RubyArgumentsTab(),
			new RubyEnvironmentTab(),
			new CommonTab()
		};
		setTabs(tabs);
	}

}
