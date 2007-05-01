package org.rubypeople.rdt.internal.corext.util;

import org.eclipse.osgi.util.NLS;

public class CorextMessages extends NLS {
	
	private static final String BUNDLE_NAME = CorextMessages.class.getName();
	
	public static String History_error_serialize;
	public static String History_error_read;
	public static String TypeInfoHistory_consistency_check;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, CorextMessages.class);
	}
}
