package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.RubyCore;

public class ResourceAdapterFactory implements IAdapterFactory {

	protected static Class[] ADAPTERS_I_CREATE = new Class[] { IRubyElement.class, IRubyScript.class, IRubyProject.class};

	public ResourceAdapterFactory() {
		super();
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (IRubyScript.class.equals(adapterType)) return RubyCore.create((IFile) adaptableObject);

		if (IRubyProject.class.equals(adapterType)) return RubyCore.create((IProject) adaptableObject);

		if (IRubyElement.class.equals(adapterType)) {
			if (adaptableObject instanceof IFile) return RubyCore.create((IFile) adaptableObject);

			if (adaptableObject instanceof IProject) return RubyCore.create((IProject) adaptableObject);
		}

		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTERS_I_CREATE;
	}

}