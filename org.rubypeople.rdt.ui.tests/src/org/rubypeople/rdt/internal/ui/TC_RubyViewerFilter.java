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

	public TC_RubyViewerFilter(String name) {
		super(name);
	}

	public void testSelect() throws Exception {
		RubyViewerFilter filter = new RubyViewerFilter(new RubyResourcesView() );
		
		IProject project = ResourceTools.createProject("TCRubyViewerFilter");
		assertTrue(!filter.select(null, null, project));
		
		RubyCore.addRubyNature(project, null);
		assertTrue(filter.select(null, null, project));
		
		IFile file = project.getFile("TCRubyViewerFilterFile");
		assertTrue(!filter.select(null, null, file));
		
		file = project.getFile("TCRubyViewerFilterFile.rb");
		assertTrue(filter.select(null, null, file));
		
		file = project.getFile("TCRubyViewerFilterFile.rbw");
		assertTrue(filter.select(null, null, file));
		
		file = project.getFile("TCRubyViewerFilterFile.cgi");
		assertTrue(filter.select(null, null, file));
		
		IFolder folder = project.getFolder("TCRubyViewerFilterFolder");
		assertTrue(filter.select(null, null, folder));
	}
}
