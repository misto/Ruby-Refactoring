package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;

public class RubyCore {

	public static IProject[] getRubyProjects() {
		List rubyProjectsList = new ArrayList();
		IProject[] workspaceProjects = RubyPlugin.getDefault().getWorkspace().getRoot().getProjects();

		for (int i = 0; i < workspaceProjects.length; i++) {
			IProject iProject = workspaceProjects[i];
			if (isRubyProject(iProject))
				rubyProjectsList.add(iProject);
		}

		IProject[] rubyProjects = new IProject[rubyProjectsList.size()];
		return (IProject[]) rubyProjectsList.toArray(rubyProjects);
	}

	public static RubyProject getRubyProject(String name) {
		IProject aProject = RubyPlugin.getDefault().getWorkspace().getRoot().getProject(name);
		if (isRubyProject(aProject)) {
			RubyProject theRubyProject = new RubyProject();
			theRubyProject.setProject(aProject);
			return theRubyProject;
		}
		return null;
	}

	public static boolean isRubyProject(IProject aProject) {
		try {
			return aProject.hasNature(RubyPlugin.RUBY_NATURE_ID);
		} catch (CoreException e) {}

		return false;
	}
}