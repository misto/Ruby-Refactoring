package org.rubypeople.rdt.internal.launching;

import org.rubypeople.rdt.launching.RdtLaunchingPlugin;

public interface RubyLaunchConfigurationAttribute {
	static final String PROJECT_NAME = RdtLaunchingPlugin.PLUGIN_ID + ".PROJECT_NAME";
	static final String FILE_NAME = RdtLaunchingPlugin.PLUGIN_ID + ".FILE_NAME";
	static final String CUSTOM_LOAD_PATH = RdtLaunchingPlugin.PLUGIN_ID + ".CUSTOM_LOAD_PATH";
	static final String USE_DEFAULT_LOAD_PATH = RdtLaunchingPlugin.PLUGIN_ID + ".USE_DEFAULT_LOAD_PATH";
	static final String SELECTED_INTERPRETER = RdtLaunchingPlugin.PLUGIN_ID + ".SELECTED_INTERPRETER";
	static final String WORKING_DIRECTORY = RdtLaunchingPlugin.PLUGIN_ID + ".WORKING_DIRECTORY";
	static final String INTERPRETER_ARGUMENTS = RdtLaunchingPlugin.PLUGIN_ID + ".INTERPRETER_ARGUMENTS";
	static final String PROGRAM_ARGUMENTS = RdtLaunchingPlugin.PLUGIN_ID + ".PROGRAM_ARGUMENTS";
}