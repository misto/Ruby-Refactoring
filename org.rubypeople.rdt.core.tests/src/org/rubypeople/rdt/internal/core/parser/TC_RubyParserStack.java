/*
 * Created on Mar 20, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.rubypeople.rdt.internal.core.parser;

import junit.framework.TestCase;

import org.rubypeople.rdt.internal.core.parser.ast.RubyElement;

/**
 * @author Chris
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class TC_RubyParserStack extends TestCase {

	public void testPushAndPeek() throws Exception {
		RubyParserStack stack = new RubyParserStack();
		RubyElement element = new RubyElement(RubyElement.METHOD, "fakeName", 1, 1);
		stack.push(element);
		assertEquals(element, stack.peek());
	}

	public void testPeekEmptyStack() {
		RubyParserStack stack = new RubyParserStack();
		try {
			stack.peek();
			fail("Did not throw a StackEmptyException when peeking an empty stack");
		} catch (StackEmptyException e) {
			// ignore expected exception
		}
	}

	public void testPushAndClose() throws Exception {
		RubyParserStack stack = new RubyParserStack();
		RubyElement element = new RubyElement(RubyElement.METHOD, "fakeName", 1, 1);
		stack.push(element);
		stack.closeLastOpenElement(2,3);
		assertEquals(new Position(2, 3), element.getEnd());
	}

	public void testCloseEmptyStack() {
		RubyParserStack stack = new RubyParserStack();
		try {
			stack.closeLastOpenElement(1, 1);
			fail("Did not throw a StackEmptyException when closing an element on an empty stack");
		} catch (StackEmptyException e) {
			// ignore expected exception
		}
	}

	public void testPushAndClear() throws Exception {
		RubyParserStack stack = new RubyParserStack();
		RubyElement element = new RubyElement(RubyElement.METHOD, "fakeName", 1, 1);
		stack.push(element);
		assertEquals(1, stack.size());
		stack.clear();
		assertTrue(stack.isEmpty());
	}

	public void testFindParentClassOrModuleAtRoot() {
		RubyParserStack stack = new RubyParserStack();
		RubyElement element = new RubyElement(RubyElement.CLASS, "fakeName", 1, 1);
		stack.push(element);
		assertEquals(1, stack.size());
		assertEquals(element, stack.findParentClassOrModule());
	}

	public void testFindParentClassOrModuleAtEnd() {
		RubyParserStack stack = new RubyParserStack();
		stack.push(new RubyElement(RubyElement.METHOD, "fakeName", 1, 1));
		RubyElement element = new RubyElement(RubyElement.CLASS, "fakeName", 1, 1);
		stack.push(element);
		assertEquals(2, stack.size());
		assertEquals(element, stack.findParentClassOrModule());
	}

}
