package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.tests.ModifyingResourceTest;

public class TC_RubyProject extends ModifyingResourceTest {

//	public void testGetLibraryPathXML() {
//		ShamRubyProject rubyProject = new ShamRubyProject();
//		rubyProject.setProject(new ShamProject("TheWorkingProject"));
//
//		IProject referencedProject = new ShamProject(new ShamIPath("TheReferencedProject"), "TheReferencedProject");
//		rubyProject.addLoadPathEntry(referencedProject);
//		assertEquals("XML should indicate only one referenced project.", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"" + referencedProject.getFullPath() + "\"/></loadpath>", rubyProject.getLoadPathXML());
//
//		IProject anotherReferencedProject = new ShamProject("AnotherReferencedProject");
//		rubyProject.addLoadPathEntry(anotherReferencedProject);
//		assertEquals("XML should indicate two referenced projects.", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"" + referencedProject.getFullPath() + "\"/><pathentry type=\"project\" path=\"" + anotherReferencedProject.getFullPath() + "\"/></loadpath>", rubyProject.getLoadPathXML());
//		
//		rubyProject.removeLoadPathEntry(referencedProject);
//		assertEquals("XML should indicate one referenced project after removing one.", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"" + anotherReferencedProject.getFullPath() + "\"/></loadpath>", rubyProject.getLoadPathXML());
//	}
	
	public void testGetRequiredProjectNames() throws CoreException {	
		try {
		IRubyProject p2 = createRubyProject("P2");
		waitForAutoBuild();
		editFile(
			"/P2/.loadpath", 
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"/StoredReferencedProject\"/><pathentry type=\"project\" path=\"/AnotherStoredReferencedProject\"/></loadpath>"
		);	
		waitForAutoBuild();
		
		String[] required = p2.getRequiredProjectNames();
		assertEquals(2, required.length);
		assertEquals("StoredReferencedProject", required[0]);
		assertEquals("AnotherStoredReferencedProject", required[1]);
		} finally {
			deleteProject("P2");
		}
	}
	
	/*
	 * Ensures that adding a project prerequisite in the loadpath updates the referenced projects
	 */
	public void testAddProjectPrerequisite() throws CoreException {
		try {
			createRubyProject("P1");
			createRubyProject("P2");
			waitForAutoBuild();
			editFile(
				"/P2/.loadpath", 
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<loadpath>\n" +
				"    <loadpathentry kind=\"src\" path=\"/P1\"/>\n" +
				"</loadpath>"
			);
			waitForAutoBuild();
			IProject[] referencedProjects = getProject("P2").getReferencedProjects();
			assertResourcesEqual(
				"Unexpected project references", 
				"/P1", 
				referencedProjects);
		} finally {
			deleteProjects(new String[] {"P1", "P2"});
		}
	}
}