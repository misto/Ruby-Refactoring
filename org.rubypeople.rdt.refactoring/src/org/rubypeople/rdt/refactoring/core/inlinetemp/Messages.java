package org.rubypeople.rdt.refactoring.core.inlinetemp;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.rubypeople.rdt.refactoring.core.inlinetemp.messages"; //$NON-NLS-1$

	public static String InlineTempConditionChecker_CannotBlockArgument;

	public static String InlineTempConditionChecker_CannotMethodParameters;

	public static String InlineTempConditionChecker_CannotMultiAssigned;

	public static String InlineTempConditionChecker_CannotMultipleAssignments;

	public static String InlineTempConditionChecker_CannotSelfReferencing;

	public static String InlineTempConditionChecker_NameNotUnique;

	public static String InlineTempConditionChecker_NoLocalVariable;

	public static String InlineTempConditionChecker_NoTarget;

	public static String InlineTempRefactoring_Name;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
