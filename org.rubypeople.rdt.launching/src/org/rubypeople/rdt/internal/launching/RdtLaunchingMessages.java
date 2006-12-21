package org.rubypeople.rdt.internal.launching;

import org.eclipse.osgi.util.NLS;

public class RdtLaunchingMessages {

	private static final String BUNDLE_NAME = RdtLaunchingMessages.class.getName();

	public static String RdtLaunchingPlugin_processTerminatedBecauseNoDebuggerConnection;
	public static String RdtLaunchingPlugin_internalErrorOccurred;
	public static String RdtLaunchingPlugin_noInterpreterSelected;
	public static String RdtLaunchingPlugin_interpreterNotFound;
	public static String RdtLaunchingPlugin_noInterpreterSelectedTitle;
	
	private RdtLaunchingMessages() {}

	static {
		NLS.initializeMessages(BUNDLE_NAME, RdtLaunchingMessages.class);
	}
}
