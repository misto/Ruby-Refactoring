package org.rubypeople.rdt.internal.launching;



public interface RubyLaunchConfigurationAttribute {
	static final String RUBY_LAUNCH_CONFIGURATION_TYPE = "org.rubypeople.rdt.launching.LaunchConfigurationTypeRubyApplication";

	static final String CUSTOM_LOAD_PATH = LaunchingPlugin.PLUGIN_ID + ".CUSTOM_LOAD_PATH";
	static final String FILE_NAME = LaunchingPlugin.PLUGIN_ID + ".FILE_NAME";
	static final String INTERPRETER_ARGUMENTS = LaunchingPlugin.PLUGIN_ID + ".INTERPRETER_ARGUMENTS";
	static final String MODULE_NAME = LaunchingPlugin.PLUGIN_ID + ".MODULE_NAME";
	static final String PROGRAM_ARGUMENTS = LaunchingPlugin.PLUGIN_ID + ".PROGRAM_ARGUMENTS";
	static final String PROJECT_NAME = LaunchingPlugin.PLUGIN_ID + ".PROJECT_NAME";
	static final String SELECTED_INTERPRETER = LaunchingPlugin.PLUGIN_ID + ".SELECTED_INTERPRETER";
	static final String WORKING_DIRECTORY = LaunchingPlugin.PLUGIN_ID + ".WORKING_DIRECTORY";
	static final String USE_DEFAULT_LOAD_PATH = LaunchingPlugin.PLUGIN_ID + ".USE_DEFAULT_LOAD_PATH";
	static final String USE_DEFAULT_WORKING_DIRECTORY = LaunchingPlugin.PLUGIN_ID + ".USE_DEFAULT_WORKING_DIRECTORY";
}