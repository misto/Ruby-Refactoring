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

	public static String AbstractVMRunner_0;
	public static String vmRunnerConfig_assert_classNotNull;
	public static String vmRunnerConfig_assert_classPathNotNull;
	public static String vmRunnerConfig_assert_vmArgsNotNull;
	public static String vmRunnerConfig_assert_programArgsNotNull;
	public static String StandardVMRunner__0__at_localhost__1__1;
	public static String StandardVMRunner__0____1___2;
	public static String StandardVMRunner_Specified_working_directory_does_not_exist_or_is_not_a_directory___0__3;
	public static String StandardVMRunner_Unable_to_locate_executable_for__0__1;
	public static String StandardVMRunner_Specified_executable__0__does_not_exist_for__1__4;
	public static String StandardVMRunner_Launching_VM____1;
	public static String StandardVMRunner_Constructing_command_line____2;
	public static String StandardVMRunner_Starting_virtual_machine____3;
	
	private LaunchingMessages() {}

	static {
		NLS.initializeMessages(BUNDLE_NAME, LaunchingMessages.class);
	}
}
