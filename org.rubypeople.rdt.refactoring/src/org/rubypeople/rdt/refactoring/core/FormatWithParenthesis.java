/**
 * 
 */
package org.rubypeople.rdt.refactoring.core;

import org.jruby.ast.visitor.rewriter.DefaultFormatHelper;

public class FormatWithParenthesis extends DefaultFormatHelper {
	public String beforeCallArguments() {
		return "(";
	}

	public String afterCallArguments() {
		return ")";
	}
}