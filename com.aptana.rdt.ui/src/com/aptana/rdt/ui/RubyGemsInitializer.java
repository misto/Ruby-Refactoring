package com.aptana.rdt.ui;

import org.eclipse.ui.IStartup;

import com.aptana.rdt.AptanaRDTPlugin;

public class RubyGemsInitializer implements IStartup {

	public void earlyStartup() {
		AptanaRDTPlugin.getDefault().getGemManager().initialize();
	}

}
