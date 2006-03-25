/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.rubypeople.rdt.core.*;
 
/**
 * A base content provider for Ruby elements. It provides access to the
 * Ruby element hierarchy without listening to changes in the Ruby model.
 * If updating the presentation on Ruby model change is required than 
 * clients have to subclass, listen to Ruby model changes and have to update
 * the UI using corresponding methods provided by the JFace viewers or their 
 * own UI presentation.
 * <p>
 * The following Ruby element hierarchy is surfaced by this content provider:
 * <p>
 * <pre>
Ruby model (<code>IRubyModel</code>)
   Ruby project (<code>IRubyProject</code>)
      package fragment root (<code>IPackageFragmentRoot</code>)
         package fragment (<code>IPackageFragment</code>)
            compilation unit (<code>ICompilationUnit</code>)
            binary class file (<code>IClassFile</code>)
 * </pre>
 * </p> 			
 * <p>
 * Note that when the entire Ruby project is declared to be package fragment root,
 * the corresponding package fragment root element that normally appears between the
 * Ruby project and the package fragments is automatically filtered out.
 * </p>
 * 
 * @since 2.0
 */
public class StandardRubyElementContentProvider implements ITreeContentProvider {

	protected static final Object[] NO_CHILDREN= new Object[0];
	protected boolean fProvideMembers;
	protected boolean fProvideWorkingCopy;
	
	/**
	 * Creates a new content provider. The content provider does not
	 * provide members of compilation units or class files.
	 */	
	public StandardRubyElementContentProvider() {
		this(false);
	}
	
	/**
	 *@deprecated Use {@link #StandardRubyElementContentProvider(boolean)} instead.
	 * Since 3.0 compilation unit children are always provided as working copies. The Ruby Model
	 * does not support the 'original' mode anymore.
	 */
	public StandardRubyElementContentProvider(boolean provideMembers, boolean provideWorkingCopy) {
		this(provideMembers);
	}
	
	
	/**
	 * Creates a new <code>StandardRubyElementContentProvider</code>.
	 *
	 * @param provideMembers if <code>true</code> members below compilation units 
	 * and class files are provided. 
	 */
	public StandardRubyElementContentProvider(boolean provideMembers) {
		fProvideMembers= provideMembers;
		fProvideWorkingCopy= provideMembers;
	}
	
	/**
	 * Returns whether members are provided when asking
	 * for a compilation units or class file for its children.
	 * 
	 * @return <code>true</code> if the content provider provides members; 
	 * otherwise <code>false</code> is returned
	 */
	public boolean getProvideMembers() {
		return fProvideMembers;
	}

	/**
	 * Sets whether the content provider is supposed to return members
	 * when asking a compilation unit or class file for its children.
	 * 
	 * @param b if <code>true</code> then members are provided. 
	 * If <code>false</code> compilation units and class files are the
	 * leaves provided by this content provider.
	 */
	public void setProvideMembers(boolean b) {
		//hello
		fProvideMembers= b;
	}
	
	/**
	 * @deprecated Since 3.0 compilation unit children are always provided as working copies. The Ruby model
	 * does not support the 'original' mode anymore. 
	 */
	public boolean getProvideWorkingCopy() {
		return fProvideWorkingCopy;
	}

	/**
	 * @deprecated Since 3.0 compilation unit children are always provided from the working copy. The Ruby model
	 * offers a unified world and does not support the 'original' mode anymore. 
	 */
	public void setProvideWorkingCopy(boolean b) {
		fProvideWorkingCopy= b;
	}

	/* (non-Rubydoc)
	 * @see IWorkingCopyProvider#providesWorkingCopies()
	 */
	public boolean providesWorkingCopies() {
		return getProvideWorkingCopy();
	}

	/* (non-Rubydoc)
	 * Method declared on IStructuredContentProvider.
	 */
	public Object[] getElements(Object parent) {
		return getChildren(parent);
	}
	
	/* (non-Rubydoc)
	 * Method declared on IContentProvider.
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	/* (non-Rubydoc)
	 * Method declared on IContentProvider.
	 */
	public void dispose() {
	}

	/* (non-Rubydoc)
	 * Method declared on ITreeContentProvider.
	 */
	public Object[] getChildren(Object element) {
		if (!exists(element))
			return NO_CHILDREN;
			
		try {
			if (element instanceof IRubyModel) 
				return getRubyProjects((IRubyModel)element);
			
			if (element instanceof IRubyProject) 
				return getRubyScripts((IRubyProject)element);
				
			if (element instanceof IFolder)
				return getResources((IFolder)element);
			
			if (getProvideMembers() && element instanceof ISourceReference && element instanceof IParent) {
				return ((IParent)element).getChildren();
			}
		} catch (RubyModelException e) {
			return NO_CHILDREN;
		}		
		return NO_CHILDREN;	
	}

	private Object[] getRubyScripts(IRubyProject project) throws RubyModelException {
		return project.getRubyScripts();
	}

	/* (non-Rubydoc)
	 * @see ITreeContentProvider
	 */
	public boolean hasChildren(Object element) {
		if (getProvideMembers()) {
			// assume scripts are never empty
			if (element instanceof IRubyScript) {
				return true;
			}
		} else {
			// don't allow to drill down into a script
			if (element instanceof IRubyScript ||
				element instanceof IFile)
			return false;
		}
			
		if (element instanceof IRubyProject) {
			IRubyProject jp= (IRubyProject)element;
			if (!jp.getProject().isOpen()) {
				return false;
			}	
		}
		
		if (element instanceof IParent) {
			try {
				// when we have Ruby children return true, else we fetch all the children
				if (((IParent)element).hasChildren())
					return true;
			} catch(RubyModelException e) {
				return true;
			}
		}
		Object[] children= getChildren(element);
		return (children != null) && children.length > 0;
	}
	 
	/* (non-Rubydoc)
	 * Method declared on ITreeContentProvider.
	 */
	public Object getParent(Object element) {
		if (!exists(element))
			return null;
		return internalGetParent(element);			
	}	

	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected Object[] getRubyProjects(IRubyModel jm) throws RubyModelException {
		return jm.getRubyProjects();
	}
		
	private Object[] getResources(IFolder folder) {
		try {
			IResource[] members= folder.members();
			IRubyProject javaProject= RubyCore.create(folder.getProject());
			if (javaProject == null || !javaProject.exists())
				return members;
//			boolean isFolderOnClasspath = javaProject.isOnClasspath(folder);
			boolean isFolderOnClasspath = true;
			List nonRubyResources= new ArrayList();
			// Can be on classpath but as a member of non-java resource folder
			for (int i= 0; i < members.length; i++) {
				IResource member= members[i];
				// A resource can also be a java element
				// in the case of exclusion and inclusion filters.
				// We therefore exclude Ruby elements from the list
				// of non-Ruby resources.
				if (isFolderOnClasspath) {
//					if (javaProject.findPackageFragmentRoot(member.getFullPath()) == null) {
//						nonRubyResources.add(member);
//					} 
//				} else if (!javaProject.isOnClasspath(member)) {
//					nonRubyResources.add(member);
				}
			}
			return nonRubyResources.toArray();
		} catch(CoreException e) {
			return NO_CHILDREN;
		}
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected boolean isClassPathChange(IRubyElementDelta delta) {
		
		// need to test the flags only for package fragment roots
		//if (delta.getElement().getElementType() != IRubyElement.PACKAGE_FRAGMENT_ROOT)
			return false;
		
//		int flags= delta.getFlags();
//		return (delta.getKind() == IRubyElementDelta.CHANGED && 
//			((flags & IRubyElementDelta.F_ADDED_TO_CLASSPATH) != 0) ||
//			 ((flags & IRubyElementDelta.F_REMOVED_FROM_CLASSPATH) != 0) ||
//			 ((flags & IRubyElementDelta.F_REORDER) != 0));
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected boolean exists(Object element) {
		if (element == null) {
			return false;
		}
		if (element instanceof IResource) {
			return ((IResource)element).exists();
		}
		if (element instanceof IRubyElement) {
			return ((IRubyElement)element).exists();
		}
		return true;
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected Object internalGetParent(Object element) {

		// try to map resources to the containing package fragment
		if (element instanceof IResource) {
			IResource parent= ((IResource)element).getParent();
			IRubyElement jParent= RubyCore.create(parent);
			// http://bugs.eclipse.org/bugs/show_bug.cgi?id=31374
			if (jParent != null && jParent.exists()) 
				return jParent;
			return parent;
		} else if (element instanceof IRubyElement) {
			IRubyElement parent= ((IRubyElement) element).getParent();
			return parent;
		}
		return null;
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 */
	protected static Object[] concatenate(Object[] a1, Object[] a2) {
		int a1Len= a1.length;
		int a2Len= a2.length;
		Object[] res= new Object[a1Len + a2Len];
		System.arraycopy(a1, 0, res, 0, a1Len);
		System.arraycopy(a2, 0, res, a1Len, a2Len); 
		return res;
	}


}
