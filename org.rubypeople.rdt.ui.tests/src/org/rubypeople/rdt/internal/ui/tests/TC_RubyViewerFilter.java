package org.rubypeople.rdt.internal.ui.tests;

import junit.framework.TestCase;

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.rubypeople.rdt.internal.core.RubyCore;
import org.rubypeople.rdt.internal.ui.RubyViewerFilter;

public class TC_RubyViewerFilter extends TestCase {

	public TC_RubyViewerFilter(String name) {
		super(name);
	}

	public void testSelect() throws Exception {
		RubyViewerFilter filter = new RubyViewerFilter();
		
		// DUPLICATION: TC_RubyCore#testMakeRubyProject
		IWorkspace workspace = new Workspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject project = root.getProject("ProjectOne");
		project.open(null);
		// DUPLICATION
		assertTrue(!filter.select(null, root, project));
		
		RubyCore.addRubyNature(project, null);
	}
}
