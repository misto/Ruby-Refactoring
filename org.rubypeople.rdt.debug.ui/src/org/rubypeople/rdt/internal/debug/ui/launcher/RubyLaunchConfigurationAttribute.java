package org.rubypeople.rdt.internal.debug.ui.launcher;

import org.rubypeople.rdt.internal.debug.ui.RdtDebugUiPlugin;

public interface RubyLaunchConfigurationAttribute {
	static final String PROJECT_NAME = RdtDebugUiPlugin.PLUGIN_ID + ".PROJECT_NAME";
	static final String FILE_NAME = RdtDebugUiPlugin.PLUGIN_ID + ".FILE_NAME";
	static final String CUSTOM_LOAD_PATH = RdtDebugUiPlugin.PLUGIN_ID + ".CUSTOM_LOAD_PATH";
	static final String USE_DEFAULT_LOAD_PATH = RdtDebugUiPlugin.PLUGIN_ID + ".USE_DEFAULT_LOAD_PATH";
	static final String SELECTED_INTERPRETER = RdtDebugUiPlugin.PLUGIN_ID + ".SELECTED_INTERPRETER";
	static final String WORKING_DIRECTORY = RdtDebugUiPlugin.PLUGIN_ID + ".WORKING_DIRECTORY";
	static final String INTERPRETER_ARGUMENTS = RdtDebugUiPlugin.PLUGIN_ID + ".INTERPRETER_ARGUMENTS";
	static final String PROGRAM_ARGUMENTS = RdtDebugUiPlugin.PLUGIN_ID + ".PROGRAM_ARGUMENTS";
}