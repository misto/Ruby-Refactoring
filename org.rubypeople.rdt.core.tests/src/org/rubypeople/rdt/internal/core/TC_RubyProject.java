package org.rubypeople.rdt.internal.core;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.rubypeople.eclipse.shams.resources.ShamFile;
import org.rubypeople.eclipse.shams.resources.ShamProject;
import org.rubypeople.eclipse.shams.runtime.ShamIPath;
import org.rubypeople.rdt.core.RubyProject;

public class TC_RubyProject extends TestCase {
	protected ShamFile loadPathEntriesFile;

	public TC_RubyProject(String name) {
		super(name);
	}
	
	protected void setUp() {
		loadPathEntriesFile = new ShamFile("thePath");
	}

	public void testGetLibraryPathXML() {
		ShamRubyProject rubyProject = new ShamRubyProject();
		rubyProject.setProject(new ShamProject("TheWorkingProject"));

		IProject referencedProject = new ShamProject(new ShamIPath("TheReferencedProject"), "TheReferencedProject");
		rubyProject.addLoadPathEntry(referencedProject);
		assertEquals("XML should indicate only one referenced project.", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"" + referencedProject.getFullPath() + "\"/></loadpath>", rubyProject.getLoadPathXML());

		IProject anotherReferencedProject = new ShamProject("AnotherReferencedProject");
		rubyProject.addLoadPathEntry(anotherReferencedProject);
		assertEquals("XML should indicate two referenced projects.", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"" + referencedProject.getFullPath() + "\"/><pathentry type=\"project\" path=\"" + anotherReferencedProject.getFullPath() + "\"/></loadpath>", rubyProject.getLoadPathXML());
		
		rubyProject.removeLoadPathEntry(referencedProject);
		assertEquals("XML should indicate one referenced project after removing one.", "<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"" + anotherReferencedProject.getFullPath() + "\"/></loadpath>", rubyProject.getLoadPathXML());
	}
	
	public void testGetReferencedProjects() {
		ShamRubyProject rubyProject = new ShamRubyProject();
		loadPathEntriesFile.setContents("<?xml version=\"1.0\" encoding=\"UTF-8\"?><loadpath><pathentry type=\"project\" path=\"/StoredReferencedProject\"/><pathentry type=\"project\" path=\"/AnotherStoredReferencedProject\"/></loadpath>");
		
		IProject referencedProject = (IProject) rubyProject.getReferencedProjects().get(0);
		assertNotNull(referencedProject);
	}

	public class ShamRubyProject extends RubyProject {
		protected IFile getLoadPathEntriesFile() {
			return loadPathEntriesFile;
		}
		
		protected IProject getProject(String name) {
			return new ShamProject(name);
		}

		protected String getLoadPathXML() {
			return super.getLoadPathXML();
		}
	}
}