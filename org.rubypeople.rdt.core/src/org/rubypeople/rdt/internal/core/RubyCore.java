package org.rubypeople.rdt.internal.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.RubyFile;
import org.rubypeople.rdt.core.RubyProject;

public class RubyCore {

	public static IProject[] getRubyProjects() {
		List rubyProjectsList = new ArrayList();
		IProject[] workspaceProjects = RubyPlugin.getWorkspace().getRoot().getProjects();

		for (int i = 0; i < workspaceProjects.length; i++) {
			IProject iProject = workspaceProjects[i];
			if (isRubyProject(iProject))
				rubyProjectsList.add(iProject);
		}

		IProject[] rubyProjects = new IProject[rubyProjectsList.size()];
		return (IProject[]) rubyProjectsList.toArray(rubyProjects);
	}

	public static RubyProject getRubyProject(String name) {
		IProject aProject = RubyPlugin.getWorkspace().getRoot().getProject(name);
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
		} catch (CoreException e) {
		}

		return false;
	}

	public static RubyFile create(IFile aFile) {
		for (int i = 0; i < RubyFile.EXTENSIONS.length; i++) {
			if (RubyFile.EXTENSIONS[i].equalsIgnoreCase(aFile.getFileExtension()))
				return new RubyFile(aFile);
		}
		
		//if (RubyFile.EXTENSION.equalsIgnoreCase(aFile.getFileExtension()))
		//	return new RubyFile(aFile);

		return null;
	}

	public static RubyProject create(IProject aProject) {
		try {
			if (aProject.hasNature(RubyPlugin.RUBY_NATURE_ID)) {
				RubyProject project = new RubyProject();
				project.setProject(aProject);
				return project;
			}
		} catch (CoreException e) {
			RubyPlugin.log("Exception occurred in RubyCore#create(IProject): " + e.toString());
		}

		return null;
	}

	public static void addRubyNature(IProject project, IProgressMonitor monitor) throws CoreException {
		if (!project.hasNature(RubyPlugin.RUBY_NATURE_ID)) {
			IProjectDescription description = project.getDescription();
			String[] prevNatures= description.getNatureIds();
			String[] newNatures= new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length]= RubyPlugin.RUBY_NATURE_ID;
			description.setNatureIds(newNatures);
			project.setDescription(description, monitor);
		}
	}
}