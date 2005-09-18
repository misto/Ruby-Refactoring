package org.rubypeople.rdt.internal.ui;
import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.rubypeople.eclipse.testutils.ResourceTools;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.internal.ui.RubyViewerFilter;
import org.rubypeople.rdt.internal.ui.resourcesview.RubyResourcesView;

public class TC_RubyViewerFilter extends TestCase {

	private RubyViewerFilter filter;
		
	public TC_RubyViewerFilter(String name) {
		super(name);
	}
	
	protected void setUp() throws Exception {
		super.setUp();
		filter = new RubyViewerFilter(new RubyResourcesView());
	}

	public void testFilterShowsRubyProjects() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		RubyCore.addRubyNature(project, null);
		assertTrue(filter.select(null, null, project));
	}
	
	public void testFilterShowsNonRubyProjects() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		assertTrue(filter.select(null, null, project));
	}
	
	public void testFilterShowsRBFiles() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		IFile file = project.getFile("TCRubyViewerFilterFile.rb");
		assertTrue(filter.select(null, null, file));
	}
	
	public void testFilterShowsRBWFiles() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		IFile file = project.getFile("TCRubyViewerFilterFile.rbw");
		assertTrue(filter.select(null, null, file));
	}
	
	public void testFilterShowsCGIFiles() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		IFile file = project.getFile("TCRubyViewerFilterFile.cgi");
		assertTrue(filter.select(null, null, file));
	}
	
	public void testFilterShowsRHTMLFiles() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		IFile file = project.getFile("TCRubyViewerFilterFile.rhtml");
		assertTrue(filter.select(null, null, file));
	}
	
	public void testFilterShowsYAMLFiles() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		IFile file = project.getFile("TCRubyViewerFilterFile.yaml");
		assertTrue(filter.select(null, null, file));
	}
	
	public void testFilterShowsYMLFiles() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		IFile file = project.getFile("TCRubyViewerFilterFile.yml");
		assertTrue(filter.select(null, null, file));
	}
	
	public void testFilterShowsRakefiles() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		IFile file = project.getFile("Rakefile");
		assertTrue(filter.select(null, null, file));
	}
	
	public void testFilterShowsGemFiles() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		IFile file = project.getFile("TCRubyViewerFilterFile.gem");
		assertTrue(filter.select(null, null, file));
	}
	
	public void testFilterShowsFolders() throws Exception {
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		IFolder folder = project.getFolder("TCRubyViewerFilterFolder");
		assertTrue(filter.select(null, null, folder));
	}
}
