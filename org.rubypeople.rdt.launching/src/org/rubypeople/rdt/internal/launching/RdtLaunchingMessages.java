package org.rubypeople.rdt.internal.launching;

import org.eclipse.osgi.util.NLS;

public class RdtLaunchingMessages {

	private static final String BUNDLE_NAME = RdtLaunchingMessages.class.getName();

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
	
	private RdtLaunchingMessages() {}

	static {
		NLS.initializeMessages(BUNDLE_NAME, RdtLaunchingMessages.class);
	}
}
