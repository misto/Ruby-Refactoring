package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import sun.security.krb5.internal.i;

public class RubyCore {

	public static IProject[] getRubyProjects() {
		List rubyProjectsList = new ArrayList();
		IProject[] workspaceProjects = RubyPlugin.getDefault().getWorkspace().getRoot().getProjects();

		for (int i = 0; i < workspaceProjects.length; i++) {
			IProject iProject = workspaceProjects[i];
			try {
				if (iProject.hasNature(RubyPlugin.RUBY_NATURE_ID))
					rubyProjectsList.add(iProject);
			} catch(CoreException e) {}
		}
		IProject[] rubyProjects = new IProject[rubyProjectsList.size()];
		return (IProject[]) rubyProjectsList.toArray(rubyProjects);
	}
}