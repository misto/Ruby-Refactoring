/*
 * Created on Mar 21, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.viewers.ViewerSorter;
import org.rubypeople.rdt.internal.core.parser.ast.RubyElement;


/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class RubyElementSorter extends ViewerSorter {

	private static final int RUBY_REQUIRE = 1;
	private static final int RUBY_GLOBAL = 2;
	private static final int RUBY_CLASS_VARIABLE = 3;
	private static final int RUBY_INSTANCE_VARIABLE = 4;
	private static final int RUBY_METHOD = 5;
	private static final int RUBY_MODULE = 6;
	private static final int RUBY_CLASS = 7;

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
	 */
	public int category(Object element) {
		if (element instanceof RubyElement) {
			RubyElement rubyElement = (RubyElement) element;
			if (rubyElement.isType(RubyElement.INSTANCE_VAR)) return RUBY_INSTANCE_VARIABLE;
			if (rubyElement.isType(RubyElement.GLOBAL)) return RUBY_GLOBAL;
			if (rubyElement.isType(RubyElement.METHOD)) return RUBY_METHOD;
			if (rubyElement.isType(RubyElement.MODULE)) return RUBY_MODULE;
			if (rubyElement.isType(RubyElement.CLASS)) return RUBY_CLASS;
			if (rubyElement.isType(RubyElement.REQUIRES)) return RUBY_REQUIRE;
			if (rubyElement.isType(RubyElement.CLASS_VAR)) return RUBY_CLASS_VARIABLE;
		}
		return 0;
	}
	
}
