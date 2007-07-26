package com.aptana.rdt.ui;

import org.eclipse.ui.IStartup;
import org.rubypeople.rdt.launching.IVMInstall;
import org.rubypeople.rdt.launching.IVMInstallChangedListener;
import org.rubypeople.rdt.launching.PropertyChangeEvent;
import org.rubypeople.rdt.launching.RubyRuntime;

import com.aptana.rdt.AptanaRDTPlugin;

public class RubyGemsInitializer implements IStartup, IVMInstallChangedListener {

	public void earlyStartup() {
		// If ruby isn't set up, listen until we have an interpreter
		IVMInstall vm = RubyRuntime.getDefaultVMInstall();
		if (vm == null) {
			RubyRuntime.addVMInstallChangedListener(this);
		} else {
			AptanaRDTPlugin.getDefault().getGemManager().initialize();
		}
	}

	public void defaultVMInstallChanged(IVMInstall previous, IVMInstall current) {
		if (current != null && !AptanaRDTPlugin.getDefault().getGemManager().isInitialized()) {
			AptanaRDTPlugin.getDefault().getGemManager().initialize();
		}		
	}

	public void vmAdded(IVMInstall newVm) {		
	}

	public void vmChanged(PropertyChangeEvent event) {		
	}

	public void vmRemoved(IVMInstall removedVm) {		
	}

}
