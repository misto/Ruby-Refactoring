/**
 * 
 */
package org.rubypeople.rdt.refactoring.core;

import org.jruby.ast.visitor.rewriter.DefaultFormatHelper;

public class FormatWithParenthesis extends DefaultFormatHelper {
	public String beforeCallArguments() {
		return "("; //$NON-NLS-1$
	}

	public String afterCallArguments() {
		return ")"; //$NON-NLS-1$
	}
}