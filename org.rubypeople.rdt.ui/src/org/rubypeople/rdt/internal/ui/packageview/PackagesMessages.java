/**
 * Copyright (c) 2007 Aptana, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl -v10.html. If redistributing this code,
 * this entire header must remain intact.
 *
 */
package org.rubypeople.rdt.internal.ui.packageview;

import org.eclipse.osgi.util.NLS;

public class PackagesMessages extends NLS {

	private static final String BUNDLE_NAME = PackagesMessages.class.getName();
	
	public static String ClassPathContainer_unbound_label;
	public static String ClassPathContainer_unknown_label;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, PackagesMessages.class);
	}
}
