package org.rubypeople.rdt.internal.ui.text.correction;

import org.eclipse.osgi.util.NLS;

public class CorrectionMessages extends NLS {

	private static final String BUNDLE_NAME = CorrectionMessages.class.getName();
	
	public static String RubyCorrectionProcessor_error_status;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, CorrectionMessages.class);
	}
	
}
