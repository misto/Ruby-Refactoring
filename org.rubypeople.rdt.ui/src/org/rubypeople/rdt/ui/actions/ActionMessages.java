package org.rubypeople.rdt.ui.actions;

import org.eclipse.osgi.util.NLS;

public class ActionMessages {
	private static final String BUNDLE_NAME = "org.rubypeople.rdt.ui.actions.ActionMessages"; //$NON-NLS-1$
	
	private ActionMessages() {}

	public static String SurroundWithBeginRescueAction_label;
	public static String SurroundWithBeginRescueAction_error;
	public static String SurroundWithBeginRescueAction_dialog_title;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, ActionMessages.class);
	}
}