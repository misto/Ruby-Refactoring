/*
 * Created on Mar 20, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.internal.core.parser.ast;

import junit.framework.TestCase;

/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TC_RubyElement extends TestCase {

	public void testGetElementsDoesntReturnLists() {
		RubyElement element = new RubyElement(RubyElement.METHOD, "someElement", 0,1);
		RubyElement block = new RubyElement(RubyElement.WHILE, "while", 1, 1);
		element.addElement(block);
		RubyElement var = new RubyElement(RubyElement.INSTANCE_VAR, "var", 1,1);
		block.addElement(var);
		Object[] elements = element.getElements();
		assertEquals(var.getClass(), elements[0].getClass());
		assertEquals(var, elements[0]);
	}
	
	
}
