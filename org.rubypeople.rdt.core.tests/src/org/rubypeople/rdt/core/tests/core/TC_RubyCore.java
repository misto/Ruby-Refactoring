package org.rubypeople.rdt.core.tests.core;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.rdt.core.RubyFile;
import org.rubypeople.rdt.internal.core.RubyCore;
import org.rubypeople.rdt.internal.core.RubyPlugin;

public class TC_RubyCore extends TestCase {

	public TC_RubyCore(String name) {
		super(name);
	}

	public void testCreate() {
		RubyCore core = new RubyCore();

		ShamFile file = new ShamFile("some/folder/theFile.rb");
		RubyFile rubyFile = core.create(file);
		assertNotNull("The core should create a RubyFile when the resource is a file with .rb extension.", rubyFile);
		assertEquals("The core should place the resource into the RubyFile.", file, rubyFile.getUnderlyingResource());

		file = new ShamFile("some/folder/theFile.xyz");
		assertNull("The core should not create a RubyFile when the resource is a file without the .rb extension.", core.create(file));

		ShamProject project = new ShamProject("aProject");
		project.addNature("someOtherNature");
		assertNull("The core should not create a RubyProject when the resource does not have the RubyProjectNature.", core.create(project));
		project.addNature(RubyPlugin.RUBY_NATURE_ID);
		assertNotNull("The core should create a RubyProject when the resource has the RubyProjectNature.", core.create(project));
	}

	public void testAddRubyNature() throws Exception {
		IProject project = createProject("someProject");
		RubyCore.addRubyNature(project, null);
		assertTrue(project.hasNature(RubyPlugin.RUBY_NATURE_ID));
	}

	public IProject createProject(String name) throws CoreException {
		IWorkspace workspace = RubyPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject(name);
		if (!project.exists()) {
			IProjectDescription desc = workspace.newProjectDescription(project.getName());
			project.create(desc, null);
		}
		if (!project.isOpen())
			project.open(null);

		return project;
	}
}
