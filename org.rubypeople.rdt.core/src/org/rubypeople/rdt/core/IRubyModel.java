/*
 * Created on Jan 13, 2005
 *
 */
package org.rubypeople.rdt.core;

import org.eclipse.core.resources.IWorkspace;

/**
 * @author cawilliams
 * 
 */
public interface IRubyModel extends IParent {

	/**
	 * Returns the Ruby project with the given name. This is a handle-only
	 * method. The project may or may not exist.
	 * 
	 * @param name
	 *            the name of the Ruby project
	 * @return the Ruby project with the given name
	 */
	IRubyProject getRubyProject(String name);

	/**
	 * Returns the Ruby projects in this Ruby model, or an empty array if there
	 * are none.
	 * 
	 * @return the Ruby projects in this Ruby model, or an empty array if there
	 *         are none
	 * @exception RubyModelException
	 *                if this request fails.
	 */
	IRubyProject[] getRubyProjects() throws RubyModelException;

	/**
	 * Returns an array of non-Ruby resources (that is, non-Ruby projects) in
	 * the workspace.
	 * <p>
	 * Non-Ruby projects include all projects that are closed (even if they have
	 * the Ruby nature).
	 * </p>
	 * 
	 * @return an array of non-Ruby projects (<code>IProject</code>s)
	 *         contained in the workspace.
	 * @throws RubyModelException
	 *             if this element does not exist or if an exception occurs
	 *             while accessing its corresponding resource
	 * @since 2.1
	 */
	Object[] getNonRubyResources() throws RubyModelException;

	/**
	 * Returns the workspace associated with this Ruby model.
	 * 
	 * @return the workspace associated with this Ruby model
	 */
	IWorkspace getWorkspace();
}
