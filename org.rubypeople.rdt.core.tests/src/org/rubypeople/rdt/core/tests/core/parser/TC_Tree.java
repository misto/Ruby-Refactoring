package org.rubypeople.rdt.core.tests.core.parser;
import junit.framework.TestCase;

import org.rubypeople.rdt.internal.core.parser.JRubyParser;
import org.rubypeople.rdt.internal.core.parser.RubyClass;
import org.rubypeople.rdt.internal.core.parser.RubyFile;

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
		
		RubyFile actual = JRubyParser.parse("jruby_test.rb");
		assertEquals(2, actual.getChildren().size());
		
		RubyClass myClass = (RubyClass) actual.getChildren().get(0);
		RubyClass yourClass = (RubyClass) actual.getChildren().get(1);
		
		assertEquals("MyClass", myClass.getName());
		assertEquals("YourClass", yourClass.getName());
		
		assertEquals(class1.getChildren(), myClass.getChildren());
	}
}
