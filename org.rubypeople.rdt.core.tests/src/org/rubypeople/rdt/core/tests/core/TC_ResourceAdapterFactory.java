package org.rubypeople.rdt.core.tests.core;

import junit.framework.TestCase;

import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.rdt.core.RubyElement;
import org.rubypeople.rdt.core.RubyFile;
import org.rubypeople.rdt.core.RubyProject;
import org.rubypeople.rdt.internal.core.ResourceAdapterFactory;
import org.rubypeople.rdt.internal.core.RubyPlugin;

public class TC_ResourceAdapterFactory extends TestCase {

	public TC_ResourceAdapterFactory(String name) {
		super(name);
	}

	public void testGetAdapter() {
		ResourceAdapterFactory factory = new ResourceAdapterFactory();

		ShamFile file = new ShamFile("mustBeA.rb");
		assertEquals(RubyFile.class, factory.getAdapter(file, RubyFile.class).getClass());
		assertEquals(RubyFile.class, factory.getAdapter(file, RubyElement.class).getClass());
		
		ShamProject project = new ShamProject("AProject");
		project.addNature(RubyPlugin.RUBY_NATURE_ID);
		assertEquals(RubyProject.class, factory.getAdapter(project, RubyProject.class).getClass());
		assertEquals(RubyProject.class, factory.getAdapter(project, RubyElement.class).getClass());
	}
}
