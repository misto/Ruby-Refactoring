package org.rubypeople.rdt.internal.ui;

import junit.framework.TestCase;

import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.core.RubyProject;
import org.rubypeople.rdt.internal.core.RubyScript;

public class TC_ResourceAdapterFactory extends TestCase {

	private ResourceAdapterFactory factory;
	
	public TC_ResourceAdapterFactory(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		factory = new ResourceAdapterFactory();	
	}

	public void testGetAdapterForRBFile() {
		ShamFile file = new ShamFile("mustBeA.rb");
		assertEquals(RubyScript.class, factory.getAdapter(file, IRubyElement.class).getClass());
		assertTrue(factory.getAdapter(file, IRubyElement.class) instanceof IRubyScript);
	}
	
	public void testGetAdapterForRBWFile() {
		ShamFile file = new ShamFile("mustBeA.rbw");
		assertEquals(RubyScript.class, factory.getAdapter(file, IRubyElement.class).getClass());
		assertTrue(factory.getAdapter(file, IRubyElement.class) instanceof IRubyScript);
	}

	public void testGetAdapterForProject() {
		ShamProject project = new ShamProject("AProject");
		project.addNature(RubyCore.NATURE_ID);
		assertEquals(RubyProject.class, factory.getAdapter(project, IRubyElement.class).getClass());
		assertTrue(factory.getAdapter(project, IRubyElement.class) instanceof IRubyProject);
	}
}
