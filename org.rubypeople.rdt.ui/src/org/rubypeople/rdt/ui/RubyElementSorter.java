/*
 * Created on Mar 21, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.ui;

import org.eclipse.jface.viewers.ViewerSorter;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IType;

/**
 * @author Chris
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class RubyElementSorter extends ViewerSorter {

	private static final int RUBY_REQUIRE = 1;
	private static final int RUBY_GLOBAL = 2;
	private static final int RUBY_CLASS_VARIABLE = 3;
	private static final int RUBY_INSTANCE_VARIABLE = 4;
	private static final int RUBY_METHOD = 5;
	private static final int RUBY_MODULE = 6;
	private static final int RUBY_CLASS = 7;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.ViewerSorter#category(java.lang.Object)
	 */
	public int category(Object element) {
		if (element instanceof IRubyElement) {
			IRubyElement rubyElement = (IRubyElement) element;
			if (rubyElement.isType(IRubyElement.INSTANCE_VAR)) return RUBY_INSTANCE_VARIABLE;
			if (rubyElement.isType(IRubyElement.GLOBAL)) return RUBY_GLOBAL;
			if (rubyElement.isType(IRubyElement.METHOD)) return RUBY_METHOD;
			if (rubyElement.isType(IRubyElement.TYPE)) {
			    IType rubyType = (IType) rubyElement;
			    if(rubyType.isClass()) return RUBY_CLASS;
			    return RUBY_MODULE;
			}
			if (rubyElement.isType(IRubyElement.IMPORT)) return RUBY_REQUIRE;
			if (rubyElement.isType(IRubyElement.CLASS_VAR)) return RUBY_CLASS_VARIABLE;
		}
		return 0;
	}

}