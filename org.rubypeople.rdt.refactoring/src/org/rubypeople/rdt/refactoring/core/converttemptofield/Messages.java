package org.rubypeople.rdt.refactoring.core.converttemptofield;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.rubypeople.rdt.refactoring.core.converttemptofield.messages"; //$NON-NLS-1$

	public static String ConvertTempToFieldRefactoring_Name;

	public static String TempToFieldConditionChecker_AlreadyExists;

	public static String TempToFieldConditionChecker_CannotConvertBlockParameters;

	public static String TempToFieldConditionChecker_CannotConvertMethodParameters;

	public static String TempToFieldConditionChecker_CannotConvertNonlocalVars;

	public static String TempToFieldConditionChecker_FieldWithName;

	public static String TempToFieldConditionChecker_NoEnclosingClassToInsert;

	public static String TempToFieldConditionChecker_NoLocalVarAtpos;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
