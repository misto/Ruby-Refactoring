package org.rubypeople.rdt.internal.core;

import junit.framework.TestCase;

import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;

public class TC_ResourceAdapterFactory extends TestCase {

	public TC_ResourceAdapterFactory(String name) {
		super(name);
	}

	public void testGetAdapter() {
		ResourceAdapterFactory factory = new ResourceAdapterFactory();

		ShamFile file = new ShamFile("mustBeA.rb");
		assertEquals(RubyScript.class, factory.getAdapter(file, IRubyScript.class).getClass());
		assertEquals(RubyScript.class, factory.getAdapter(file, IRubyElement.class).getClass());

		ShamProject project = new ShamProject("AProject");
		project.addNature(RubyCore.NATURE_ID);
		assertEquals(RubyProject.class, factory.getAdapter(project, IRubyProject.class).getClass());
		assertEquals(RubyProject.class, factory.getAdapter(project, IRubyElement.class).getClass());
	}
}
