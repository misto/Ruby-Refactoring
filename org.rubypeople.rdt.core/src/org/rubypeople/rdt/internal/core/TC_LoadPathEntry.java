package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IProject;
import org.rubypeople.eclipse.shams.resources.ShamIProject;
import org.rubypeople.eclipse.shams.runtime.ShamIPath;

import junit.framework.TestCase;

public class TC_LoadPathEntry extends TestCase {

	public TC_LoadPathEntry(String name) {
		super(name);
	}

	public void testToXml() {
		ShamIProject project = new ShamIProject("MyProject");
		project.setFullPath("myLocation");
		LoadPathEntry entry = new LoadPathEntry(project);
		
		String expected = "<pathentry type=\"project\" path=\"myLocation\"/>";
		assertEquals(expected, entry.toXML());
	}
}
