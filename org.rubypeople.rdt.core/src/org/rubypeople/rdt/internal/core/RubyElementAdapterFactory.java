package org.rubypeople.rdt.internal.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdapterFactory;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.core.RubyCore;

public class RubyElementAdapterFactory implements IAdapterFactory {
	
	protected static Class[] ADAPTERS_I_CREATE = new Class[] {
		IResource.class
	};

	public RubyElementAdapterFactory() {
		super();
	}

	public Object getAdapter(Object adaptableObject, Class adapterType) {
		IRubyElement rubyElement = (IRubyElement) adaptableObject;
		
		try {
			if (IResource.class.equals(adapterType))
				return getResource(rubyElement);
		} catch (RubyModelException e) {
			RubyCore.log(e);
		}

		return null;
	}

	public Class[] getAdapterList() {
		return ADAPTERS_I_CREATE;
	}

	protected IResource getResource(IRubyElement rubyElement) throws RubyModelException {
		return rubyElement.getUnderlyingResource();
	}
}
