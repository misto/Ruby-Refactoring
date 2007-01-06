/*
 * Author: C.Williams
 * 
 * Copyright (c) 2004 RubyPeople.
 * 
 * This file is part of the Ruby Development Tools (RDT) plugin for eclipse. You
 * can get copy of the GPL along with further information about RubyPeople and
 * third party software bundled with RDT in the file
 * org.rubypeople.rdt.core_x.x.x/RDT.license or otherwise at
 * http://www.rubypeople.org/RDT.license.
 * 
 * RDT is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * RDT is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * RDT; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package org.rubypeople.rdt.core;

import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * @author Chris
 * 
 */
public interface IRubyProject extends IRubyElement, IParent {

	public abstract IProject getProject();

	public String[] getRequiredProjectNames() throws RubyModelException;

	/**
	 * Returns the first type found following this project's classpath with the
	 * given fully qualified name or <code>null</code> if none is found. The
	 * fully qualified name is a dot-separated name. For example, a class B
	 * defined as a member type of a class A in package x.y should have a the
	 * fully qualified name "x.y.A.B".
	 * 
	 * Note that in order to be found, a type name (or its toplevel enclosing
	 * type name) must match its corresponding compilation unit name. As a
	 * consequence, secondary types cannot be found using this functionality.
	 * Secondary types can however be explicitely accessed through their
	 * enclosing unit or found by the <code>SearchEngine</code>.
	 * 
	 * @param fullyQualifiedName
	 *            the given fully qualified name
	 * @exception RubyModelException
	 *                if this element does not exist or if an exception occurs
	 *                while accessing its corresponding resource
	 * @return the first type found following this project's classpath with the
	 *         given fully qualified name or <code>null</code> if none is
	 *         found
	 * @see IType#getFullyQualifiedName(char)
	 * @since 2.0
	 */
	IType findType(String fullyQualifiedName) throws RubyModelException;

    public boolean upgrade() throws CoreException;

    /**
     * Helper method for returning one option value only. Equivalent to <code>(String)this.getOptions(inheritRubyCoreOptions).get(optionName)</code>
     * Note that it may answer <code>null</code> if this option does not exist, or if there is no custom value for it.
     * <p>
     * For a complete description of the configurable options, see <code>RubyCore#getDefaultOptions</code>.
     * </p>
     * 
     * @param optionName the name of an option
     * @param inheritRubyCoreOptions - boolean indicating whether RubyCore options should be inherited as well
     * @return the String value of a given option
     * @see RubyCore#getDefaultOptions()
     */
    String getOption(String optionName, boolean inheritRubyCoreOptions);
    
    /**
     * Returns the table of the current custom options for this project. Projects remember their custom options,
     * in other words, only the options different from the the RubyCore global options for the workspace.
     * A boolean argument allows to directly merge the project options with global ones from <code>RubyCore</code>.
     * <p>
     * For a complete description of the configurable options, see <code>RubyCore#getDefaultOptions</code>.
     * </p>
     * 
     * @param inheritRubyCoreOptions - boolean indicating whether RubyCore options should be inherited as well
     * @return table of current settings of all options 
     *   (key type: <code>String</code>; value type: <code>String</code>)
     * @see RubyCore#getDefaultOptions()
     */
    Map getOptions(boolean inheritRubyCoreOptions);

	public abstract IRubyScript[] getRubyScripts() throws RubyModelException;

	public abstract Object[] getNonRubyResources() throws RubyModelException;
	
	public abstract ISourceFolder[] getSourceFolders() throws RubyModelException;

	public abstract ISourceFolderRoot getSourceFolderRoot(IResource resource);

	public abstract ILoadpathEntry[] getRawLoadpath() throws RubyModelException;

	public abstract ISourceFolderRoot[] getSourceFolderRoots() throws RubyModelException;

	public abstract boolean isOnLoadpath(IRubyElement element);
	
	ILoadpathEntry[] getResolvedLoadpath(boolean ignoreUnresolvedEntry) throws RubyModelException;

	public void setRawLoadpath(ILoadpathEntry[] newEntries,
			IPath newOutputLocation,
			IProgressMonitor monitor,
			boolean canChangeResource,
			ILoadpathEntry[] oldResolvedPath,
			boolean needValidation,
			boolean needSave)
			throws RubyModelException;
	
	void setRawLoadpath(ILoadpathEntry[] entries, boolean canModifyResources, IProgressMonitor monitor) throws RubyModelException;

	void setRawLoadpath(ILoadpathEntry[] entries, IProgressMonitor monitor)
		throws RubyModelException;

	void setRawLoadpath(ILoadpathEntry[] entries, IPath outputLocation, IProgressMonitor monitor)
		throws RubyModelException;

	public abstract ISourceFolderRoot getSourceFolderRoot(String rootPath);
}