/*
 * Created on Mar 21, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.jface.viewers.ViewerSorter;
import org.rubypeople.rdt.internal.core.parser.ast.RubyClass;
import org.rubypeople.rdt.internal.core.parser.ast.RubyClassVariable;
import org.rubypeople.rdt.internal.core.parser.ast.RubyGlobal;
import org.rubypeople.rdt.internal.core.parser.ast.RubyInstanceVariable;
import org.rubypeople.rdt.internal.core.parser.ast.RubyMethod;
import org.rubypeople.rdt.internal.core.parser.ast.RubyModule;
import org.rubypeople.rdt.internal.core.parser.ast.RubyRequires;


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
		if (element instanceof RubyRequires) return RUBY_REQUIRE;
		if (element instanceof RubyClassVariable) return RUBY_CLASS_VARIABLE;
		if (element instanceof RubyInstanceVariable) return RUBY_INSTANCE_VARIABLE;
		if (element instanceof RubyMethod) return RUBY_METHOD;
		if (element instanceof RubyModule) return RUBY_MODULE;
		if (element instanceof RubyClass) return RUBY_CLASS;
		if (element instanceof RubyGlobal) return RUBY_GLOBAL;
		return 0;
	}
	
}
