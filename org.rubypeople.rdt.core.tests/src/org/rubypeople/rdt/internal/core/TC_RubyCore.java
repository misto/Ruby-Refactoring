package org.rubypeople.rdt.internal.core;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.eclipse.testutils.ResourceTools;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;

public class TC_RubyCore extends TestCase {

	public TC_RubyCore(String name) {
		super(name);
	}

	public void testCreate() throws RubyModelException {
		ShamFile file = new ShamFile("some/folder/theFile.rb");
		IRubyScript rubyFile = RubyCore.create(file);
		assertNotNull("The core should create an IRubyScript when the resource is a file with .rb extension.", rubyFile);
		assertEquals("The core should place the resource into the RubyFile.", file, rubyFile.getUnderlyingResource());

		file = new ShamFile("some/folder/theFile.xyz");
		assertNull("The core should not create a RubyFile when the resource is a file without the .rb extension.", RubyCore.create(file));

		ShamProject project = new ShamProject("aProject");
		project.addNature(RubyCore.NATURE_ID);
		assertNotNull("The core should create a RubyProject when the resource has the RubyProjectNature.", RubyCore.create(project));
	}

	public void testAddRubyNature() throws Exception {
		IProject project = ResourceTools.createProject("someProject");
		RubyCore.addRubyNature(project, null);
		assertTrue(project.hasNature(RubyCore.NATURE_ID));
	}
}
