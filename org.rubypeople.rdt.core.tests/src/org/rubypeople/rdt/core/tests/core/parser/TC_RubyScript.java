package org.rubypeople.rdt.core.tests.core.parser;

import junit.framework.TestCase;

import org.rubypeople.rdt.internal.core.parser.RubyClass;
import org.rubypeople.rdt.internal.core.parser.RubyMethod;
import org.rubypeople.rdt.internal.core.parser.RubyScript;

public class TC_RubyScript extends TestCase {

	public TC_RubyScript(String name) {
		super(name);
	}

	public void testGetElements() {
		RubyScript script = new RubyScript("class bob");
		Object[] elements = script.getElements();
		assertEquals(new RubyClass("bob"), elements[0]);

		script = new RubyScript("class bob\nclass jane");
		elements = script.getElements();
		assertEquals(new RubyClass("bob"), elements[0]);
		assertEquals(new RubyClass("jane"), elements[1]);
	}

	public void testGetElements_methods() {
		RubyScript script = new RubyScript("class bob def name");
		Object[] elements = script.getElements();
		RubyClass bobClass = (RubyClass) elements[0];
		assertEquals(new RubyClass("bob"), bobClass);
		assertEquals(new RubyMethod("name"), bobClass.getElements()[0]);

		script = new RubyScript("class bob\ndef name\ndef otherName");
		elements = script.getElements();
		bobClass = (RubyClass) elements[0];
		assertEquals(new RubyClass("bob"), bobClass);
		assertEquals(new RubyMethod("name"), bobClass.getElements()[0]);
		assertEquals(new RubyMethod("otherName"), bobClass.getElements()[1]);
	}
}
