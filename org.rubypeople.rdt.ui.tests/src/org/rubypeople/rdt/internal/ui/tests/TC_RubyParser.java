package org.rubypeople.rdt.internal.ui.tests;

import junit.framework.TestCase;

import org.rubypeople.rdt.core.RubyParsedComponent;
import org.rubypeople.rdt.core.RubyParser;

public class TC_RubyParser extends TestCase {

	public TC_RubyParser(String name) {
		super(name);
	}
	
	public void testGetComponentHierarchy() {
		RubyParser parser = new RubyParser();
		RubyParsedComponent parsedComponent = parser.getComponentHierarchy("somefilename", "class Simple\n\ndef initialize()\n@variable = \"hello\"\nend\n\nend");
		
		assertTrue(parsedComponent instanceof RubyParsedComponent);
		assertEquals("There should be one child", 1, parsedComponent.getChildren().size());
		
		RubyParsedComponent classComponent = (RubyParsedComponent) parsedComponent.getChildren().get(0);
		assertEquals("The first component should be the class", "Simple", classComponent.getName());
		
		RubyParsedComponent firstMethodComponent = (RubyParsedComponent) classComponent.getChildren().get(0);
		assertEquals("The first method should be named correctly", "initialize()", firstMethodComponent.getName());
	}

}
