/**
 * 
 */
package org.rubypeople.rdt.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.rubypeople.rdt.internal.core.RubyModelStatus;
import org.rubypeople.rdt.internal.core.util.Util;

/**
 * @author Chris
 * 
 */
public class RubyConventions {

	/**
	 * Validate the given ruby script name. A ruby script name must obey the
	 * following rules:
	 * <ul>
	 * <li> it must not be null
	 * <li> it must include the <code>".rb"</code> or <code>".rb"</code>
	 * suffix
	 * <li> its prefix must be a valid identifier
	 * <li> it must not contain any characters or substrings that are not valid
	 * on the file system on which workspace root is located.
	 * </ul>
	 * </p>
	 * 
	 * @param name
	 *            the name of a ruby script
	 * @return a status object with code <code>IStatus.OK</code> if the given
	 *         name is valid as a ruby script name, otherwise a status object
	 *         indicating what is wrong with the name
	 */
	public static IStatus validateRubyScriptName(String name) {
		if (name == null) { return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Util.bind("convention.unit.nullName"), null); //$NON-NLS-1$
		}
		if (!org.rubypeople.rdt.internal.core.util.Util.isRubyLikeFileName(name)) { return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Util.bind("convention.unit.notJavaName"), null); //$NON-NLS-1$
		}
		int index;
		index = name.lastIndexOf('.');
		if (index == -1) { return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1, Util.bind("convention.unit.notJavaName"), null); //$NON-NLS-1$
		}
		IStatus status = ResourcesPlugin.getWorkspace().validateName(name, IResource.FILE);
		if (!status.isOK()) { return status; }
		return RubyModelStatus.VERIFIED_OK;
	}

	/**
	 * Validate the given Java identifier. The identifier must not have the same
	 * spelling as a Java keyword, boolean literal (<code>"true"</code>,
	 * <code>"false"</code>), or null literal (<code>"null"</code>). See
	 * section 3.8 of the <em>Java Language Specification, Second Edition</em>
	 * (JLS2). A valid identifier can act as a simple type name, method name or
	 * field name.
	 * 
	 * @param id
	 *            the Java identifier
	 * @return a status object with code <code>IStatus.OK</code> if the given
	 *         identifier is a valid Java identifier, otherwise a status object
	 *         indicating what is wrong with the identifier
	 */
	public static IStatus validateIdentifier(String id) {
		// if (scannedIdentifier(id) != null) {
		// FIXME We should actually check the identifier
		return RubyModelStatus.VERIFIED_OK;
		// } else {
		// return new Status(IStatus.ERROR, RubyCore.PLUGIN_ID, -1,
		// Util.bind("convention.illegalIdentifier", id), null); //$NON-NLS-1$
		// }
	}
}
