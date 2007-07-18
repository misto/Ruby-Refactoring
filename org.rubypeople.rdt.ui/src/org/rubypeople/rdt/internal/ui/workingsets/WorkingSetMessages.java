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
package org.rubypeople.rdt.internal.ui.workingsets;

import org.eclipse.osgi.util.NLS;

public class WorkingSetMessages extends NLS {

	private static final String BUNDLE_NAME = WorkingSetMessages.class.getName();

	public static String WorkingSetModel_others_name;
	
	public static String ClearWorkingSetAction_text;
	public static String ClearWorkingSetAction_toolTip;
	
	public static String SelectWorkingSetAction_text;
	public static String SelectWorkingSetAction_toolTip;
	
	public static String EditWorkingSetAction_text;
	public static String EditWorkingSetAction_toolTip;
	public static String EditWorkingSetAction_error_nowizard_title;
	public static String EditWorkingSetAction_error_nowizard_message;	
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, WorkingSetMessages.class);
	}
}
