package org.rubypeople.rdt.refactoring.core;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.rubypeople.rdt.refactoring.core.messages"; //$NON-NLS-1$

	public static String RefactoringConditionChecker_EmptyDocument;

	public static String RefactoringConditionChecker_SyntaxErrorInCurrent;

	public static String RefactoringConditionChecker_SyntaxErrorInProject;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
