package com.aptana.rdt.internal.core.gems;

import org.eclipse.ui.IStartup;

public class RubyGemsInitializer implements IStartup {

	public void earlyStartup() {
		GemManager.getInstance().initialize();
	}

}
