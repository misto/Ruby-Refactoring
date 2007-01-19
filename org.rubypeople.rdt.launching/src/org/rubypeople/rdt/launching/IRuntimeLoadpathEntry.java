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
package org.rubypeople.rdt.launching;


import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.rubypeople.rdt.core.ILoadpathEntry;
import org.rubypeople.rdt.core.IRubyProject;

/**
 * Represents an entry on a runtime classpath. A runtime classpath entry
 * may refer to one of the following:
 * <ul>
 * 	<li>A Ruby project (type <code>PROJECT</code>) - a project entry refers
 * 		to all of the built classes in a project, and resolves to the output
 * 		location(s) of the associated Ruby project.</li>
 * 	<li>An archive (type <code>ARCHIVE</code>) - an archive refers to a jar, zip, or
 * 		folder in the workspace or in the local file system containing class
 * 		files. An archive may have attached source.</li>
 * 	<li>A variable (type <code>VARIABLE</code>) - a variable refers to a 
 * 		classpath variable, which may refer to a jar.</li>
 * 	<li>A library (type <code>CONTAINER</code>) - a container refers to classpath
 * 		container variable which refers to a collection of archives derived
 * 		dynamically, on a per project basis.</li>
 *  <li>A contributed classpath entry (type <code>OTHER</code>) - a contributed
 *      classpath entry is an extension contributed by a plug-in. The resolution
 *      of a contributed classpath entry is client defined. See
 * 		<code>IRuntimeLoadpathEntry2</code>.
 * </ul>
 * <p>
 * Clients may implement this interface for contributed a classpath entry
 * types (i.e. type <code>OTHER</code>). Note, contributed classpath entries
 * are new in 3.0, and are only intended to be contributed by the Ruby debugger.
 * </p>
 * @since 2.0
 * @see org.eclipse.jdt.launching.IRuntimeLoadpathEntry2
 */
public interface IRuntimeLoadpathEntry {
	
	/**
	 * Type identifier for project entries.
	 */
	public static final int PROJECT = 1;
	
	/**
	 * Type identifier for archive entries.
	 */
	public static final int ARCHIVE = 2;	
		
	/**
	 * Type identifier for variable entries.
	 */
	public static final int VARIABLE = 3;
	
	/**
	 * Type identifier for container entries.
	 */
	public static final int CONTAINER = 4;
	
	/**
	 * Type identifier for contributed entries.
	 * @since 3.0
	 */
	public static final int OTHER = 5;	

	/**
	 * Loadpath property identifier for entries that appear on the
	 * bootstrap path by default.
	 */
	public static final int STANDARD_CLASSES = 1;	
	
	/**
	 * Loadpath property identifier for entries that should appear on the
	 * bootstrap path explicitly.
	 */
	public static final int BOOTSTRAP_CLASSES = 2;	
		
	/**
	 * Loadpath property identifier for entries that should appear on the
	 * user classpath.
	 */
	public static final int USER_CLASSES = 3;	
	
	/**
	 * Returns this classpath entry's type. The type of a runtime classpath entry is
	 * identified by one of the following constants:
	 * <ul>
	 * <li><code>PROJECT</code></li>
	 * <li><code>ARCHIVE</code></li>
	 * <li><code>VARIABLE</code></li>
	 * <li><code>CONTAINER</code></li>
	 * <li><code>OTHER</code></li>
	 * </ul>
	 * <p>
	 * Since 3.0, a type of <code>OTHER</code> may be returned.
	 * </p>
	 * @return this classpath entry's type
	 */
	public int getType();
	
	/**
	 * Returns a memento for this classpath entry.
	 * <p>
	 * Since 3.0, the memento for a contributed classpath entry (i.e. of
	 * type <code>OTHER</code>), must be in the form of an XML document,
	 * with the following element structure:
	 * <pre>
	 * <runtimeLoadpathEntry id="exampleId">
	 *    <memento
	 *       key1="value1"
	 * 		 ...>
	 *    </memento>
	 * </runtimeLoadpathEntry>
	 * </pre>
	 * The <code>id</code> attribute is the unique identifier of the extension
	 * that contributed this runtime classpath entry type, via the extension
	 * point <code>org.eclipse.jdt.launching.runtimeLoadpathEntries</code>.
	 * The <code>memento</code> element will be used to initialize a
	 * restored runtime classpath entry, via the method
	 * <code>IRuntimeLoadpathEntry2.initializeFrom(Element memento)</code>. The 
	 * attributes of the <code>memento</code> element are client defined.
	 * </p>
	 * 
	 * @return a memento for this classpath entry
	 * @exception CoreException if an exception occurs generating a memento
	 */
	public String getMemento() throws CoreException;
	
	/**
	 * Returns the path associated with this entry, or <code>null</code>
	 * if none. The format of the
	 * path returned depends on this entry's type:
	 * <ul>
	 * <li><code>PROJECT</code> - a workspace relative path to the associated
	 * 		project.</li>
	 * <li><code>ARCHIVE</code> - the absolute path of the associated archive,
	 * 		which may or may not be in the workspace.</li>
	 * <li><code>VARIABLE</code> - the path corresponding to the associated
	 * 		classpath variable entry.</li>
	 * <li><code>CONTAINER</code> - the path corresponding to the associated
	 * 		classpath container variable entry.</li>
	 * <li><code>OTHER</code> - the path returned is client defined.</li>
	 * </ul>
	 * <p>
	 * Since 3.0, this method may return <code>null</code>.
	 * </p>
	 * @return the path associated with this entry, or <code>null</code>
	 * @see org.eclipse.jdt.core.ILoadpathEntry#getPath()
	 */
	public IPath getPath();
		
	/**
	 * Returns the resource associated with this entry, or <code>null</code>
	 * if none. A project, archive, or folder entry may be associated
	 * with a resource.
	 * 
	 * @return the resource associated with this entry, or <code>null</code>
	 */ 
	public IResource getResource();
	
	/**
	 * Returns a constant indicating where this entry should appear on the 
	 * runtime classpath by default.
	 * The value returned is one of the following:
	 * <ul>
	 * <li><code>STANDARD_CLASSES</code> - a standard entry does not need to appear
	 * 		on the runtime classpath</li>
	 * <li><code>BOOTSTRAP_CLASSES</code> - a bootstrap entry should appear on the
	 * 		boot path</li>
	 * <li><code>USER_CLASSES</code> - a user entry should appear on the path
	 * 		containing user or application classes</li>
	 * </ul>
	 * 
	 * @return where this entry should appear on the runtime classpath
	 */
	public int getLoadpathProperty();
	
	/**
	 * Sets whether this entry should appear on the bootstrap classpath,
	 * the user classpath, or whether this entry is a standard bootstrap entry
	 * that does not need to appear on the classpath.
	 * The location is one of:
	 * <ul>
	 * <li><code>STANDARD_CLASSES</code> - a standard entry does not need to appear
	 * 		on the runtime classpath</li>
	 * <li><code>BOOTSTRAP_CLASSES</code> - a bootstrap entry should appear on the
	 * 		boot path</li>
	 * <li><code>USER_CLASSES</code> - a user entry should appear on the path
	 * 		conatining user or application classes</li>
	 * </ul>
	 * 
	 * @param location a classpat property constant
	 */
	public void setLoadpathProperty(int location);	
	
	/**
	 * Returns an absolute path in the local file system for this entry,
	 * or <code>null</code> if none, or if this entry is of type <code>CONTAINER</code>.
	 * 
	 * @return an absolute path in the local file system for this entry,
	 *  or <code>null</code> if none
	 */
	public String getLocation();
		
	/**
	 * Returns the first segment of the path associated with this entry, or <code>null</code>
	 * if this entry is not of type <code>VARIABLE</code> or <code>CONTAINER</code>.
	 * 
	 * @return the first segment of the path associated with this entry, or <code>null</code>
	 *  if this entry is not of type <code>VARIABLE</code> or <code>CONTAINER</code>
	 */
	public String getVariableName();
	
	/**
	 * Returns a classpath entry equivalent to this runtime classpath entry,
	 * or <code>null</code> if none.
	 * @return a classpath entry equivalent to this runtime classpath entry,
	 *  or <code>null</code>
	 * @since 0.9.0
	 */
	public ILoadpathEntry getLoadpathEntry();
	
	/**
	 * Returns the Ruby project associated with this runtime classpath entry
	 * or <code>null</code> if none. Runtime classpath entries of type
	 * <code>CONTAINER</code> may be associated with a project for the
	 * purposes of resolving the entries in a container. 
	 * 
	 * @return the Ruby project associated with this runtime classpath entry
	 * or <code>null</code> if none
	 * @since 0.9.0
	 */
	public IRubyProject getRubyProject();
}
