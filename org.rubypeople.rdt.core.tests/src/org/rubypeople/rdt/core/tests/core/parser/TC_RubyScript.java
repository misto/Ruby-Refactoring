package org.rubypeople.rdt.core.tests.core.parser;

import junit.framework.TestCase;

import org.rubypeople.rdt.internal.core.parser.RubyScript;

public class TC_RubyScript extends TestCase {

	public TC_RubyScript(String name) {
		super(name);
	}
	
	public void testGetElements() {
		RubyScript script = new RubyScript("class bob");
		Object[] elements = script.getElements();
		assertEquals("bob", elements[0]);

		script = new RubyScript("class bob\nclass jane");
		elements = script.getElements();
		
		assertEquals("bob", elements[0]);
		assertEquals("jane", elements[1]);
	}
}
