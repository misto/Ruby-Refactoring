package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rubypeople.rdt.core.*;
import org.rubypeople.rdt.core.RubyFile;

public class ResourceAdapterFactory implements IAdapterFactory {

	protected static Class[] ADAPTERS_I_CREATE = new Class[] {
		RubyFile.class, RubyProject.class
	};

	public ResourceAdapterFactory() {
		super();
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (RubyFile.class.equals(adapterType))
			return RubyCore.create((IFile) adaptableObject);

		if (RubyProject.class.equals(adapterType))
			return RubyCore.create((IProject) adaptableObject);

		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTERS_I_CREATE;
	}

}
