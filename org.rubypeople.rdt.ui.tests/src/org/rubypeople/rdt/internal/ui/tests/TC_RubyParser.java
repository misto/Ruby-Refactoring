package org.rubypeople.rdt.internal.ui.tests;

import java.util.List;

import junit.framework.TestCase;

import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.rdt.core.RubyParsedComponent;
import org.rubypeople.rdt.core.RubyParser;

public class TC_RubyParser extends TestCase {
	protected RubyParser parser;
	protected ShamFile file;
	
	public TC_RubyParser(String name) {
		super(name);
	}
	
	protected void setUp() {
		parser = new RubyParser();
		file = new ShamFile("thePath");
	}

	public void testGetComponentHierarchy() {
		String content = "class Simple\n\ndef initialize()\n@variable = \"hello\"\nend\n\nend";
		file.setContents(content);
		
		RubyParsedComponent fileComponent = parser.getComponentHierarchy(file);
		
		assertEquals("There should be one child, which is the class", 1, fileComponent.getChildren().size());
		assertEquals(0, fileComponent.nameOffset());
		assertEquals(0, fileComponent.nameLength());
		assertEquals("length for top level should be the full file length", content.length(), fileComponent.length());
		assertEquals(0, fileComponent.offset());
		
		RubyParsedComponent classComponent = (RubyParsedComponent) fileComponent.getChildren().get(0);
		assertEquals("Simple", classComponent.getName());
		assertEquals(6, classComponent.nameOffset());
		assertEquals(6, classComponent.nameLength());
		assertEquals(content.length(), classComponent.length());
		assertEquals(0, classComponent.offset());

		RubyParsedComponent firstMethodComponent = (RubyParsedComponent) classComponent.getChildren().get(0);
		assertEquals("initialize()", firstMethodComponent.getName());
		assertEquals(18, firstMethodComponent.nameOffset());
		assertEquals(12, firstMethodComponent.nameLength());
		assertEquals(40, firstMethodComponent.length());
		assertEquals(14, firstMethodComponent.offset());
	}
	
	public void testGetComponentHierarchy_multipleMethods() {
		file.setContents("class Simple\n\n\n\ndef someMethod var = 1\nend\n\ndef initialize()\n@variable = \"hello\"\nend\n\nend");
		
		RubyParsedComponent parsedComponent = parser.getComponentHierarchy(file);
		List children = ((RubyParsedComponent)parsedComponent.getChildren().get(0)).getChildren();
		
		assertEquals("there should be two children", 2, children.size());
		assertEquals("The first childs name should be correct", "someMethod", ((RubyParsedComponent)children.get(0)).getName());
		assertEquals("The second childs name should be correct", "initialize()", ((RubyParsedComponent)children.get(1)).getName());		
	}
}
