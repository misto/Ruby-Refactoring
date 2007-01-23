package org.rubypeople.rdt.internal.codeassist;

import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyModelException;

public class RubyElementRequestor {

	private IRubyProject[] projects;

	public RubyElementRequestor(IRubyProject[] projects) {
		this.projects = projects;
	}

	public RubyElementRequestor(IRubyProject rubyProject) {
		this(new IRubyProject[] {rubyProject});
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
