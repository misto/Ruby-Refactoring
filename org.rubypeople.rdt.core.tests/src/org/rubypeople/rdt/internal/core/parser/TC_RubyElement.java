/*
 * Created on Mar 20, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.internal.core.parser;

import junit.framework.TestCase;


/**
 * @author Chris
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class TC_RubyElement extends TestCase {

	public void testGetElementsDoesntReturnLists() {
		RubyElement element = new RubyElement("someElement", new Position(0,1));
		RubyBlock block = new RubyBlock("block", new Position(1,1));
		element.addElement(block);
		RubyInstanceVariable var = new RubyInstanceVariable("var", 1,1);
		block.addElement(var);
		Object[] elements = element.getElements();
		assertEquals(var.getClass(), elements[0].getClass());
		assertEquals(var, elements[0]);
	}
	
	
}
