package org.rubypeople.rdt.launching;

import org.rubypeople.rdt.internal.launching.LaunchingPlugin;

public interface IRubyLaunchConfigurationConstants {
	
	/**
	 * Status code indicating a launch configuration does not
	 * specify a VM Install
	 */
	public static final int ERR_UNSPECIFIED_VM_INSTALL = 103;
	
	/**
	 * Status code indicating the project associated with
	 * a launch configuration is not a Ruby project.
	 */
	public static final int ERR_NOT_A_RUBY_PROJECT = 107;

	/**
	 * Status code indicating the specified working directory
	 * does not exist.
	 */
	public static final int ERR_WORKING_DIRECTORY_DOES_NOT_EXIST = 108;	
	
	/**
	 * Status code indicating that the project referenced by a launch configuration
	 * is closed.
	 * 
	 * @since 0.9.0
	 */
	public static final int ERR_PROJECT_CLOSED = 124;	
	
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

	/**
	 * Launch configuration attribute key. The value is a name of
	 * a Ruby project associated with a Ruby launch configuration.
	 */
	public static final String ATTR_PROJECT_NAME = LaunchingPlugin.getUniqueIdentifier() + ".PROJECT_ATTR"; //$NON-NLS-1$
	

	/**
	 * Launch configuration attribute key. The value is a path identifying the JRE used
	 * when launching a local VM. The path is a classpath container corresponding
	 * to the <code>JavaRuntime.JRE_CONTAINER</code> classpath container.
	 * <p>
	 * When unspecified the default JRE for a launch configuration is used (which is the
	 * JRE associated with the project being launched, or the workspace default JRE when
	 * no project is associated with a configuration). The default JRE classpath container
	 * refers explicitly to the workspace default JRE.
	 * </p>
	 * @since 3.2
	 */
	public static final String ATTR_JRE_CONTAINER_PATH = RubyRuntime.RUBY_CONTAINER;
	
	/**
	 * Launch configuration attribute key. The value is a name of a VM install
	 * to use when launching a local VM. This attribute must be qualified
	 * by a VM install type, via the <code>ATTR_VM_INSTALL_TYPE</code>
	 * attribute. When unspecified, the default VM is used.
	 * 
	 * @deprecated use <code>ATTR_JRE_CONTAINER_PATH</code>
	 */
	public static final String ATTR_VM_INSTALL_NAME = LaunchingPlugin.getUniqueIdentifier() + ".VM_INSTALL_NAME"; //$NON-NLS-1$
		
	/**
	 * Launch configuration attribute key. The value is an identifier of
	 * a VM install type. Used in conjunction with a VM install name, to 
	 * specify the VM to use when launching a local Java application.
	 * The associated VM install name is specified via the attribute
	 * <code>ATTR_VM_INSTALL_NAME</code>.
	 * 
	 * @deprecated use <code>ATTR_JRE_CONTAINER_PATH</code>
	 */
	public static final String ATTR_VM_INSTALL_TYPE = LaunchingPlugin.getUniqueIdentifier() + ".VM_INSTALL_TYPE_ID"; //$NON-NLS-1$

}
