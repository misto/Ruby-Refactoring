package org.rubypeople.rdt.ui.actions;

import org.eclipse.osgi.util.NLS;

public class ActionMessages {
	private static final String BUNDLE_NAME = "org.rubypeople.rdt.ui.actions.ActionMessages"; //$NON-NLS-1$
	
	private ActionMessages() {}

	public static String SurroundWithBeginRescueAction_label;
	public static String SurroundWithBeginRescueAction_error;
	public static String SurroundWithBeginRescueAction_dialog_title;

	public static String QuickMenuAction_menuTextWithShortcut;
	public static String ShowInPackageViewAction_label;
	public static String ShowInPackageViewAction_description;
	public static String ShowInPackageViewAction_tooltip;
	public static String ShowInPackageViewAction_error_message;
	public static String ShowInPackageViewAction_dialog_title;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, ActionMessages.class);
	}
}