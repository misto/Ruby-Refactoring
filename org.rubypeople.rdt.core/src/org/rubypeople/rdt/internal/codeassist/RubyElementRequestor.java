package org.rubypeople.rdt.internal.codeassist;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourceAttributes;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;

public class RubyElementRequestor {

	private IRubyProject[] projects;

	public RubyElementRequestor(IRubyProject[] projects) {
		this.projects = projects;
		// Get path of folder containing ruby core stubs
		String rootDirName = RubyCore.getOSDirectory(RubyCore.getPlugin());
		String dirName = rootDirName
				+ "ruby/lib";

		File rubyfolder = new File(dirName);
		IPath projectPath = new Path(rubyfolder.getAbsolutePath());
		// XXX This is still a big hack. We're adding the ruby core library on demand. We need to add it when the project is created (and/ore interpreter is installed)
		IFolder folder = projects[0].getProject().getFolder("ruby_core");
		if (!folder.exists()) {
			try {
				folder.createLink(projectPath, IResource.ALLOW_MISSING_LOCAL, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Hide ruby_core resource folder
		try {
			ResourceAttributes ra = folder.getResourceAttributes();
			if ( ra != null ) {
				
				//TODO: Doesn't hide & make readonly for some reason?
				ra.setHidden(true);
				ra.setReadOnly(true);
				
				folder.setResourceAttributes(ra);
				
				// Mark ruby_core as derived to keep out of source control
				folder.setDerived(true);
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public IType findType(String typeName) {
		try {
			for (int x = 0; x < projects.length; x++) {
				IRubyProject project = projects[x];
				IType type =  project.findType(typeName);
				if (type != null)
					return type;				
			}
		} catch (RubyModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
