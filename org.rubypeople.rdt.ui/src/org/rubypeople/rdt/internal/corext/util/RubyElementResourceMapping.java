/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.corext.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.mapping.ResourceMapping;
import org.eclipse.core.resources.mapping.ResourceMappingContext;
import org.eclipse.core.resources.mapping.ResourceTraversal;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyModel;
import org.rubypeople.rdt.core.IRubyProject;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.IRubyType;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.Assert;
import org.rubypeople.rdt.internal.ui.RubyPlugin;

/**
 * An abstract super class to describe mappings from a Ruby element to a
 * set of resources. The class also provides factory methods to create
 * resource mappings.
 * 
 * @since 3.1
 */
public abstract class RubyElementResourceMapping extends ResourceMapping {
	
	/* package */ RubyElementResourceMapping() {
	}
	
	public IRubyElement getRubyElement() {
		Object o= getModelObject();
		if (o instanceof IRubyElement)
			return (IRubyElement)o;
		return null;
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof RubyElementResourceMapping))
			return false;
		return getRubyElement().equals(((RubyElementResourceMapping)obj).getRubyElement());
	}
	
	public int hashCode() {
		return getRubyElement().hashCode();
	}
	
	//---- the factory code ---------------------------------------------------------------
	
	private static final class RubyModelResourceMapping extends RubyElementResourceMapping {
		private final IRubyModel fRubyModel;
		private RubyModelResourceMapping(IRubyModel model) {
			Assert.isNotNull(model);
			fRubyModel= model;
		}
		public Object getModelObject() {
			return fRubyModel;
		}
		public IProject[] getProjects() {
			IRubyProject[] projects= null;
			try {
				projects= fRubyModel.getRubyProjects();
			} catch (RubyModelException e) {
				RubyPlugin.log(e);
				return new IProject[0];
			}
			IProject[] result= new IProject[projects.length];
			for (int i= 0; i < projects.length; i++) {
				result[i]= projects[i].getProject();
			}
			return result;
		}
		public ResourceTraversal[] getTraversals(ResourceMappingContext context, IProgressMonitor monitor) throws CoreException {
			IRubyProject[] projects= fRubyModel.getRubyProjects();
			ResourceTraversal[] result= new ResourceTraversal[projects.length];
			for (int i= 0; i < projects.length; i++) {
				result[i]= new ResourceTraversal(new IResource[] {projects[i].getProject()}, IResource.DEPTH_INFINITE, 0);
			}
			return result;
		}
	}
	
	private static final class RubyProjectResourceMapping extends RubyElementResourceMapping {
		private final IRubyProject fProject;
		private RubyProjectResourceMapping(IRubyProject project) {
			Assert.isNotNull(project);
			fProject= project;
		}
		public Object getModelObject() {
			return fProject;
		}
		public IProject[] getProjects() {
			return new IProject[] {fProject.getProject() };
		}
		public ResourceTraversal[] getTraversals(ResourceMappingContext context, IProgressMonitor monitor) throws CoreException {
			return new ResourceTraversal[] {
				new ResourceTraversal(new IResource[] {fProject.getProject()}, IResource.DEPTH_INFINITE, 0)
			};
		}
	}
		
	private static final class RubyScriptResourceMapping extends RubyElementResourceMapping {
		private final IRubyScript fUnit;
		private RubyScriptResourceMapping(IRubyScript unit) {
			Assert.isNotNull(unit);
			fUnit= unit;
		}
		public Object getModelObject() {
			return fUnit;
		}
		public IProject[] getProjects() {
			return new IProject[] {fUnit.getRubyProject().getProject() };
		}
		public ResourceTraversal[] getTraversals(ResourceMappingContext context, IProgressMonitor monitor) throws CoreException {
			return new ResourceTraversal[] {
				new ResourceTraversal(new IResource[] {fUnit.getCorrespondingResource()}, IResource.DEPTH_ONE, 0)
			};
		}
	}

		
	public static ResourceMapping create(IRubyElement element) {
		switch (element.getElementType()) {
			case IRubyElement.TYPE:
				return create((IRubyType)element);
			case IRubyElement.SCRIPT:
				return create((IRubyScript)element);
			case IRubyElement.PROJECT:
				return create((IRubyProject)element);
			case IRubyElement.RUBY_MODEL:
				return create((IRubyModel)element);
			default:
				return null;
		}		
		
	}

	public static ResourceMapping create(final IRubyModel model) {
		return new RubyModelResourceMapping(model);
	}
	
	public static ResourceMapping create(final IRubyProject project) {
		return new RubyProjectResourceMapping(project);
	}
	
	public static ResourceMapping create(IRubyScript unit) {
		unit= RubyModelUtil.toOriginal(unit);
		if (unit == null)
			return null;
		return new RubyScriptResourceMapping(unit);
	}
	
	public static ResourceMapping create(IRubyType type) {
		// top level types behave like the CU
		IRubyElement parent= type.getParent();
		if (parent instanceof IRubyScript) {
			return create((IRubyScript)parent);
		}
		return null;
	}
}
