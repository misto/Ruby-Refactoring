package org.rubypeople.rdt.core.tests.core.parser;
import java.util.List;

import junit.framework.TestCase;

import org.rubypeople.rdt.internal.core.parser.JRubyParser;
import org.rubypeople.rdt.internal.core.parser.RubyClass;

public class TC_Tree extends TestCase {
	public TC_Tree(String name) {
		super(name);
	}

	public void testTree() {
		RubyClass class1 = new RubyClass("MyClass");
		RubyClass class2 = new RubyClass("YourClass");
		class1.addClassVariable("@@c");
		class1.addInstanceVariable("@a");
		class2.addInstanceVariable("@b");
		class1.addMethod("printName");
		class1.addMethod("printName2");
		class2.addMethod("printSong");
		
		List actual = JRubyParser.parse("jruby_test.rb");
		assertEquals(2, actual.size());
		
		RubyClass myClass = (RubyClass) actual.get(0);
		RubyClass yourClass = (RubyClass) actual.get(1);
		
		assertEquals("MyClass", myClass.getName());
		assertEquals("YourClass", yourClass.getName());
		
		assertEquals(class1.getClassVariables(), myClass.getClassVariables());
		
		assertEquals(class1.getInstanceVariables(), myClass.getInstanceVariables());
		assertEquals(class2.getInstanceVariables(), yourClass.getInstanceVariables());
		
		assertEquals(class1.getMethods(), myClass.getMethods());
		assertEquals(class2.getMethods(), yourClass.getMethods());
	}
}
