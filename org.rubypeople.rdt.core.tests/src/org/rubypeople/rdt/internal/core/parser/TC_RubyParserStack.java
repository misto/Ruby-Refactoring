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
public class TC_RubyParserStack extends TestCase {

	public void testPushAndPeek() throws Exception {
		RubyParserStack stack = new RubyParserStack();
		RubyElement element = new RubyElement("fakeName", new Position(1, 1));
		stack.push(element);
		assertEquals( element, stack.peek());
	}
	
	public void testPeekEmptyStack() {
		RubyParserStack stack = new RubyParserStack();
		try { 
			stack.peek();
			fail("Did not throw a StackEmptyException when peeking an empty stack");
		}
		catch (StackEmptyException e) {
			// ignore expected exception
		}
	}
	
	public void testPushAndPop() throws Exception {
		RubyParserStack stack = new RubyParserStack();
		RubyElement element = new RubyElement("fakeName", new Position(1, 1));
		stack.push(element);
		assertEquals( element, stack.pop());
	}
	
	public void testPopEmptyStack() {
		RubyParserStack stack = new RubyParserStack();
		try { 
			stack.pop();
			fail("Did not throw a StackEmptyException when popping an empty stack");
		}
		catch (StackEmptyException e) {
			// ignore expected exception
		}
	}
	
	public void testPushAndClear() throws Exception {
		RubyParserStack stack = new RubyParserStack();
		RubyElement element = new RubyElement("fakeName", new Position(1, 1));
		stack.push(element);
		assertEquals(1, stack.size());
		stack.clear();
		assertTrue(stack.isEmpty());
	}
	
	public void testFindParentClassOrModuleAtRoot() {
		RubyParserStack stack = new RubyParserStack();
		RubyClass element = new RubyClass("fakeName", new Position(1, 1));
		stack.push(element);
		assertEquals(1, stack.size());
		assertEquals(element, stack.findParentClassOrModule());
	}
	
	public void testFindParentClassOrModuleAtEnd() {
		RubyParserStack stack = new RubyParserStack();
		stack.push(new RubyElement("fakeName", new Position(1,1)));
		RubyClass element = new RubyClass("fakeName", new Position(1, 1));
		stack.push(element);
		assertEquals(2, stack.size());
		assertEquals(element, stack.findParentClassOrModule());
	}
	
}
