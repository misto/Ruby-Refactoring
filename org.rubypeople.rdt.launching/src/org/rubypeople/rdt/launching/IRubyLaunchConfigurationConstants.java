package org.rubypeople.rdt.launching;

import org.rubypeople.rdt.internal.launching.LaunchingPlugin;

public interface IRubyLaunchConfigurationConstants {
	
	/**
	 * Status code indicating a launch configuration does not
	 * specify a file to launch.
	 */
	public static final int ERR_UNSPECIFIED_FILE_NAME = 101;	
	
	/**
	 * Status code indicating a launch configuration does not
	 * specify a VM Install
	 */
	public static final int ERR_UNSPECIFIED_VM_INSTALL = 103;
	
	/**
	 * Status code indicating a launch configuration's VM install
	 * could not be found.
	 */
	public static final int ERR_VM_INSTALL_DOES_NOT_EXIST = 105;
	
	/**
	 * Status code indicating a VM runner could not be located
	 * for the VM install specified by a launch configuration.
	 */
	public static final int ERR_VM_RUNNER_DOES_NOT_EXIST = 106;	
	
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

	/**
	 * Launch configuration attribute key. The value is a string specifying
	 * program arguments for a Ruby launch configuration, as they should appear
	 * on the command line.
	 */
	public static final String ATTR_PROGRAM_ARGUMENTS = LaunchingPlugin.getUniqueIdentifier() + ".PROGRAM_ARGUMENTS"; //$NON-NLS-1$
		
	/**
	 * Launch configuration attribute key. The value is a string specifying
	 * VM arguments for a Ruby launch configuration, as they should appear
	 * on the command line.
	 */
	public static final String ATTR_VM_ARGUMENTS = LaunchingPlugin.getUniqueIdentifier() + ".VM_ARGUMENTS";	 //$NON-NLS-1$
	
	/**
	 * Launch configuration attribute key. The value is a string specifying a
	 * path to the working directory to use when launching a local VM.
	 * When specified as an absolute path, the path represents a path in the local
	 * file system. When specified as a full path, the path represents a workspace
	 * relative path. When unspecified, the working directory defaults to the project
	 * associated with a launch configuration. When no project is associated with a
	 * launch configuration, the working directory is inherited from the current
	 * process.
	 */
	public static final String ATTR_WORKING_DIRECTORY = LaunchingPlugin.getUniqueIdentifier() + ".WORKING_DIRECTORY";	 //$NON-NLS-1$
	
	/**
	 * Launch configuration attribute key. The value is a Map of attributes specific
	 * to a particular VM install type, used when launching a local Ruby
	 * application. The map is passed to a <code>VMRunner</code> via a <code>VMRunnerConfiguration</code>
	 * when launching a VM. The attributes in the map are implementation dependent
	 * and are limited to String keys and values.
	 */
	public static final String ATTR_VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP = LaunchingPlugin.getUniqueIdentifier() + "VM_INSTALL_TYPE_SPECIFIC_ATTRS_MAP"; //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The value is a fully qualified name
	 * of a file to launch.
	 */
	public static final String ATTR_FILE_NAME = LaunchingPlugin.getUniqueIdentifier() + ".FILE_NAME";	 //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The value is an identifier of a
	 * classpath provider extension used to compute the classpath
	 * for a launch configuration. When unspecified, the default classpath
	 * provider is used - <code>StandardClasspathProvider</code>.
	 */
	public static final String ATTR_CLASSPATH_PROVIDER = LaunchingPlugin.getUniqueIdentifier() + ".CLASSPATH_PROVIDER";	 //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The value is a boolean specifying
	 * whether a default classpath should be used when launching a local
	 * Java application. When <code>false</code>, a classpath must be specified
	 * via the <code>ATTR_CLASSPATH</code> attribute. When <code>true</code> or
	 * unspecified, a classpath is computed by the classpath provider associated
	 * with a launch configuration.
	 */
	public static final String ATTR_DEFAULT_CLASSPATH = LaunchingPlugin.getUniqueIdentifier() + ".DEFAULT_CLASSPATH"; //$NON-NLS-1$

	/**
	 * Launch configuration attribute key. The attribute value is an ordered list of strings
	 * which are mementos for runtime class path entries. When unspecified, a default
	 * classpath is generated by the classpath provider associated with a launch
	 * configuration (via the <code>ATTR_CLASSPATH_PROVIDER</code> attribute).
	 */
	public static final String ATTR_CLASSPATH = LaunchingPlugin.getUniqueIdentifier() + ".CLASSPATH";	 //$NON-NLS-1$
	
	
}
