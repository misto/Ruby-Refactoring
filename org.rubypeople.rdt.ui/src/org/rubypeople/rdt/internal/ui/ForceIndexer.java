package org.rubypeople.rdt.internal.ui;

import org.eclipse.ui.IStartup;
import org.rubypeople.rdt.core.RubyCore;

public class ForceIndexer implements IStartup {

	public void earlyStartup() {
		RubyCore.forceIndexing();
	}

}
