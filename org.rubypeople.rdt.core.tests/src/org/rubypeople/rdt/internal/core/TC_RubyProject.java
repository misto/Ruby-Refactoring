package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.tests.ModifyingResourceTest;

public class TC_RubyProject extends ModifyingResourceTest {
	
	public TC_RubyProject(String name) {
		super(name);
	}
	
	@Override
	protected void setUp() throws Exception {
		// TODO Only run once per suite/class, not every method
		super.setUp();
		setUpRubyProject("RubyProjectTests");
	}
	
	@Override
	protected void tearDown() throws Exception {
//		 TODO Only run once per suite/class, not every method
		deleteProject("RubyProjectTests");
		super.tearDown();
	}
	
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
				"    <pathentry type=\"src\" path=\"/P1\"/>\n" +
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
	
	/**
	 * Test that a ruby script
	 * has a corresponding resource.
	 */
	public void testRubyScriptCorrespondingResource() throws RubyModelException {
		IRubyScript element= getRubyScript("RubyProjectTests", "", "q", "A.rb");
		IResource corr= element.getCorrespondingResource();
		IResource res= getWorkspace().getRoot().getProject("RubyProjectTests").getFolder("q").getFile("A.rb");
		assertTrue("incorrect corresponding resource", corr.equals(res));
		assertEquals("Project is incorrect for the ruby script", "RubyProjectTests", corr.getProject().getName());
	}
}