package org.rubypeople.rdt.internal.launching;

import org.eclipse.osgi.util.NLS;

public class LaunchingMessages {

	private static final String BUNDLE_NAME = LaunchingMessages.class.getName();

	public static String RdtLaunchingPlugin_processTerminatedBecauseNoDebuggerConnection;
	public static String RdtLaunchingPlugin_internalErrorOccurred;
	public static String RdtLaunchingPlugin_noInterpreterSelected;
	public static String RdtLaunchingPlugin_interpreterNotFound;
	public static String RdtLaunchingPlugin_noInterpreterSelectedTitle;
	public static String RubyRuntime_badFormat;
	public static String RubyRuntime_VM_type_element_with_unknown_id_1;
	public static String RubyRuntime_VM_element_specified_with_no_id_attribute_2;
	public static String RubyRuntime_exceptionOccurred;
	public static String vmInstall_assert_typeNotNull;
	public static String vmInstall_assert_idNotNull;
	public static String AbstractInterpreterInstall_0;
	public static String AbstractInterpreterInstall_1;
	public static String AbstractInterpreterInstall_3;
	public static String AbstractInterpreterInstall_4;
	public static String LaunchingPlugin_33;
	public static String LaunchingPlugin_34;
	public static String RubyRuntime_exceptionsOccurred;
	public static String vmInstallType_duplicateVM;
	public static String StandardVMType_Standard_VM_3;
	public static String StandardVMType_Standard_VM_not_supported_on_MacOS__1;
	public static String StandardVMType_Not_a_JDK_Root__Java_executable_was_not_found_1;
	public static String StandardVMType_ok_2;
	public static String StandardVMType_Not_a_JDK_root__System_library_was_not_found__1;
	
	private LaunchingMessages() {}

	static {
		NLS.initializeMessages(BUNDLE_NAME, LaunchingMessages.class);
	}
}
