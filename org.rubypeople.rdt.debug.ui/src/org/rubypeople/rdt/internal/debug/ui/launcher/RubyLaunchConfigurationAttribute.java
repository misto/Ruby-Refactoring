package org.rubypeople.rdt.internal.debug.ui.launcher;

import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;

public interface RubyLaunchConfigurationAttribute {
	static final String PROJECT_NAME = RdtDebugUiPlugin.PLUGIN_ID + ".PROJECT_NAME";
	static final String FILE_NAME = RdtDebugUiPlugin.PLUGIN_ID + ".FILE_NAME";

	static final String LOAD_PATH_LIST = RdtDebugUiPlugin.PLUGIN_ID + ".LOAD_PATH_LIST";
	static final String WORKING_DIRECTORY = RdtDebugUiPlugin.PLUGIN_ID + ".WORKING_DIRECTORY";
	static final String INTERPRETER_ARGUMENTS = RdtDebugUiPlugin.PLUGIN_ID + ".INTERPRETER_ARGUMENTS";
	static final String PROGRAM_ARGUMENTS = RdtDebugUiPlugin.PLUGIN_ID + ".PROGRAM_ARGUMENTS";
}