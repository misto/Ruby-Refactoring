package org.rubypeople.rdt.core.tests.core.parser;

import junit.framework.TestCase;

import org.rubypeople.rdt.internal.core.parser.RubyClass;

public class TC_RubyClass extends TestCase {
	public TC_RubyClass(String name) {
		super(name);
	}

	public void testEquals() {
		RubyClass class1 = new RubyClass("AClassName");
		RubyClass class2 = new RubyClass("AClassName");
		RubyClass class3 = new RubyClass("DifferentClassName");
		
		assertTrue(class1.equals(class2));
		assertTrue(!class1.equals(class3));
	}

}
