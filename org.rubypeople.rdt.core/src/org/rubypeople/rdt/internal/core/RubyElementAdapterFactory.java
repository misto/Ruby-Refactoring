package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rubypeople.rdt.core.RubyFile;

public class RubyElementAdapterFactory implements IAdapterFactory {
	
	protected static Class[] ADAPTERS_I_CREATE = new Class[] {
		IResource.class
	};

	public RubyElementAdapterFactory() {
		super();
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		RubyFile rubyElement = (RubyFile) adaptableObject;
		
		if (IResource.class.equals(adapterType))
			return getResource(rubyElement);

		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTERS_I_CREATE;
	}

	protected IResource getResource(RubyFile rubyElement) {
		return rubyElement.getUnderlyingResource();
	}
}
