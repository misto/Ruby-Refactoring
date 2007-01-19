package org.rubypeople.rdt.launching;

import org.rubypeople.rdt.internal.launching.LaunchingPlugin;

public interface IRubyLaunchConfigurationConstants {

	/**
	 * Status code indicating the specified working directory
	 * does not exist.
	 */
	public static final int ERR_WORKING_DIRECTORY_DOES_NOT_EXIST = 108;	
	
	/**
	 * Status code indicating an unexpected internal error.
	 */
	public static final int ERR_INTERNAL_ERROR = 150;

	/**
	 * Identifier for the ruby process type, which is annotated on processes created
	 * by the local ruby application launch delegate.
	 * 
	 * (value <code>"ruby"</code>).
	 */
	public static final String ID_RUBY_PROCESS_TYPE = "ruby"; //$NON-NLS-1$ 

	/**
	 * Attribute key for VM specific attributes found in the
	 * <code>ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP</code>. The value is a String,
	 * indicating the String to use to invoke the Ruby VM.
	 */
	public static final String ATTR_RUBY_COMMAND = LaunchingPlugin.getUniqueIdentifier() + ".RUBY_COMMAND";	 //$NON-NLS-1$

}
