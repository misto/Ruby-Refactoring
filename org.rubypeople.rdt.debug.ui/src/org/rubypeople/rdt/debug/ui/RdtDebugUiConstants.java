package org.rubypeople.rdt.debug.ui;

import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;

public interface RdtDebugUiConstants {
	public static final String DEFAULT_WORKING_DIRECTORY = RdtDebugUiPlugin.getWorkspace().getRoot().getLocation().toString();

	public static final String PREFERENCE_KEYWORDS = RdtDebugUiPlugin.PLUGIN_ID + ".preference_keywords";
}
