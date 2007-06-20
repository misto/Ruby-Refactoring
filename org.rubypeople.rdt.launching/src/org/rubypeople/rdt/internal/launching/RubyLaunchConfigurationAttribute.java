package org.rubypeople.rdt.internal.launching;

import org.rubypeople.rdt.launching.IRubyLaunchConfigurationConstants;


/**
 * @deprecated Please use the externally visible IRubyLaunchConfigurationConstants
 * 
 */
public interface RubyLaunchConfigurationAttribute {

	/**
	 * @deprecated Please use <code>IRubyLaunchConfigurationConstants.ATTR_LOADPATH</code>
	 */
	static final String CUSTOM_LOAD_PATH = IRubyLaunchConfigurationConstants.ATTR_LOADPATH;
	
	/**
	 * @deprecated Please use <code>IRubyLaunchConfigurationConstants.ATTR_FILE_NAME</code>
	 */
	static final String FILE_NAME = IRubyLaunchConfigurationConstants.ATTR_FILE_NAME;
	
	/**
	 * @deprecated Please use <code>IRubyLaunchConfigurationConstants.ATTR_VM_ARGUMENTS</code>
	 */
	static final String INTERPRETER_ARGUMENTS = IRubyLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;
	
	/**
	 * @deprecated Please use <code>IRubyLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS</code>
	 */
	static final String PROGRAM_ARGUMENTS = IRubyLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS;
	
	/**
	 * @deprecated Please use <code>IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME</code>
	 */
	static final String PROJECT_NAME = IRubyLaunchConfigurationConstants.ATTR_PROJECT_NAME;
	
	/**
	 * @deprecated Please use <code>IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME</code>.
	 */
	static final String SELECTED_INTERPRETER = IRubyLaunchConfigurationConstants.ATTR_VM_INSTALL_NAME;
	
	/**
	 * @deprecated Please use <code>IRubyLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY</code>
	 */
	static final String WORKING_DIRECTORY = IRubyLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY;
	
	/**
	 * @deprecated Please use <code>IRubyLaunchConfigurationConstants.ATTR_DEFAULT_LOADPATH</code>
	 */
	static final String USE_DEFAULT_LOAD_PATH = IRubyLaunchConfigurationConstants.ATTR_DEFAULT_LOADPATH;
	
	
	static final String USE_DEFAULT_WORKING_DIRECTORY = LaunchingPlugin.PLUGIN_ID + ".USE_DEFAULT_WORKING_DIRECTORY";
}